/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.audit.influx;

import org.apache.streampipes.audit.api.AuditEntry;
import org.apache.streampipes.audit.api.AuditEntryDetails;
import org.apache.streampipes.audit.api.AuditEventReader;
import org.apache.streampipes.audit.api.AuditPage;
import org.apache.streampipes.audit.api.AuditQuery;
import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;
import org.apache.streampipes.dataexplorer.influx.InfluxQueryTransport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.influxdb.dto.Query;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Explicit summary projections and keyset paging across event types; no Data Explorer dataset registration. */
final class InfluxAuditEventReader implements AuditEventReader {
  private static final String CURSOR_VERSION = "v1";
  private static final String TOKEN_SEPARATOR = ":";
  private static final String FINGERPRINT_ALGORITHM = "SHA-256";
  private static final int MAXIMUM_LOCATOR_LENGTH = 512;
  private static final int MAXIMUM_QUERY_ROWS = 10_000;
  private static final int MAXIMUM_DETAILS_LENGTH = 32 * 1024;
  private static final int IDENTITY_LOOKUP_LIMIT = 2;
  private static final Pattern EVENT_TYPE_PATTERN = Pattern.compile("[a-z][a-z0-9-]*(\\.[a-z][a-z0-9-]*)+");
  private static final String PROJECTION = Stream.of(
      InfluxAuditSchema.EVENT_ID, InfluxAuditSchema.EVENT_TYPE, InfluxAuditSchema.OUTCOME,
      InfluxAuditSchema.ACTOR, InfluxAuditSchema.RESOURCE_TYPE, InfluxAuditSchema.RESOURCE_ID)
      .map(column -> "\"" + column + "\"")
      .collect(Collectors.joining(","));
  private static final String SUMMARY_QUERY = "SELECT %s FROM \"%s\" WHERE %s ORDER BY time DESC LIMIT %d";
  private static final String DETAILS_QUERY = "SELECT %s,\"%s\" FROM \"%s\" WHERE time = %d%s%s LIMIT %d";
  private final InfluxQueryTransport transport;
  private final ObjectMapper mapper = new ObjectMapper();

  InfluxAuditEventReader(InfluxQueryTransport transport) {
    this.transport = transport;
  }

  @Override
  public AuditPage query(AuditQuery query) {
    long lower = nanos(query.from());
    long upper = nanos(query.to());
    String fingerprint = fingerprint(query);
    if (query.cursor() != null && !query.cursor().isBlank()) {
      String[] parts = decode(query.cursor()).split(TOKEN_SEPARATOR, -1);
      if (parts.length != 3 || !parts[0].equals(CURSOR_VERSION) || !parts[2].equals(fingerprint)) {
        throw new IllegalArgumentException("Audit cursor does not match filters");
      }
      upper = Long.parseLong(parts[1]);
      if (upper <= lower || upper > nanos(query.to())) {
        throw new IllegalArgumentException("Invalid audit cursor range");
      }
    }
    String where = "time >= " + lower + " AND time < " + upper
        + filter(InfluxAuditSchema.EVENT_TYPE, query.eventType())
        + filter(InfluxAuditSchema.ACTOR, query.actor())
        + filter(InfluxAuditSchema.OUTCOME, query.outcome());
    var rows = read(SUMMARY_QUERY.formatted(PROJECTION, InfluxAuditSchema.MEASUREMENT, where, query.limit() + 1),
        MAXIMUM_QUERY_ROWS);
    rows.sort(Comparator.comparingLong(Row::timestamp).reversed());
    boolean more = rows.size() > query.limit();
    var page = rows.stream().limit(query.limit()).map(this::summary).toList();
    String next = more ? encodeToken(rows.get(query.limit() - 1).timestamp(), fingerprint) : null;
    return new AuditPage(page, next);
  }

  @Override
  public Optional<AuditEntryDetails> find(UUID eventId, String locator) {
    if (locator == null || locator.length() > MAXIMUM_LOCATOR_LENGTH) {
      throw new IllegalArgumentException("Invalid audit locator");
    }
    String[] parts = decode(locator).split(TOKEN_SEPARATOR, 3);
    if (parts.length != 3 || !parts[0].equals(CURSOR_VERSION)
        || !EVENT_TYPE_PATTERN.matcher(parts[2]).matches()) {
      throw new IllegalArgumentException("Invalid audit locator");
    }
    long time = Long.parseLong(parts[1]);
    if (time < 0) {
      throw new IllegalArgumentException("Invalid audit timestamp");
    }
    var rows = read(DETAILS_QUERY.formatted(PROJECTION, InfluxAuditSchema.DETAILS, InfluxAuditSchema.MEASUREMENT,
        time, filter(InfluxAuditSchema.EVENT_ID, eventId.toString()), filter(InfluxAuditSchema.EVENT_TYPE, parts[2]),
        IDENTITY_LOOKUP_LIMIT), IDENTITY_LOOKUP_LIMIT);
    if (rows.isEmpty()) {
      return Optional.empty();
    }
    var row = rows.getFirst();
    if (rows.size() != 1 || !eventId.toString().equals(row.values().get(InfluxAuditSchema.EVENT_ID))) {
      throw new IllegalStateException("Invalid audit identity response");
    }
    var entry = summary(row);
    Map<String, Object> details = new HashMap<>();
    var json = row.values().get(InfluxAuditSchema.DETAILS);
    if (json != null) {
      try {
        String value = json.toString();
        if (value.length() > MAXIMUM_DETAILS_LENGTH) {
          throw new IllegalStateException("Audit details exceed read limit");
        }
        details = mapper.readValue(value, new TypeReference<Map<String, Object>>() { });
      } catch (java.io.IOException e) {
        throw new IllegalStateException("Invalid stored audit details");
      }
    }
    var recordedAt = details.remove(InfluxAuditSchema.RECORDED_AT);
    return Optional.of(new AuditEntryDetails(entry,
        recordedAt == null ? entry.storedAt() : Instant.parse(recordedAt.toString()).toString(), details));
  }

  private List<Row> read(String statement, int maximumRows) {
    var rows = new ArrayList<Row>();
    try (var cursor = transport.open(new Query(statement), QueryTimestampFormat.EPOCH_NANOS)) {
      while (cursor.hasNext()) {
        var batch = cursor.next();
        for (var values : batch.rows()) {
          if (rows.size() >= maximumRows) {
            throw new IllegalStateException("Audit read exceeds row budget");
          }
          var mapped = new HashMap<String, Object>();
          for (int i = 0; i < batch.columns().size(); i++) {
            mapped.put(batch.columns().get(i), values.get(i));
          }
          if (batch.tags() != null) {
            batch.tags().forEach(mapped::putIfAbsent);
          }
          if (!(mapped.get(InfluxAuditSchema.TIME) instanceof Number time)) {
            throw new IllegalStateException("Missing audit timestamp");
          }
          rows.add(new Row(time.longValue(), mapped));
        }
      }
    }
    return rows;
  }

  private AuditEntry summary(Row row) {
    var values = row.values();
    String type = text(values, InfluxAuditSchema.EVENT_TYPE);
    return new AuditEntry(text(values, InfluxAuditSchema.EVENT_ID),
        Instant.ofEpochSecond(0, row.timestamp()).toString(), type,
        text(values, InfluxAuditSchema.OUTCOME), text(values, InfluxAuditSchema.ACTOR),
        text(values, InfluxAuditSchema.RESOURCE_TYPE), text(values, InfluxAuditSchema.RESOURCE_ID),
        encodeToken(row.timestamp(), type));
  }

  private String text(Map<String, Object> values, String name) {
    var value = values.get(name);
    return value == null ? null : value.toString();
  }

  private String filter(String column, String value) {
    return value == null ? "" : " AND \"" + column + "\" = '" + value.replace("\\", "\\\\").replace("'", "\\'") + "'";
  }

  private String fingerprint(AuditQuery query) {
    try {
      byte[] value = mapper.writeValueAsBytes(new Object[]{query.from().toString(), query.to().toString(),
          query.eventType(), query.actor(), query.outcome(), query.limit()});
      return HexFormat.of().formatHex(MessageDigest.getInstance(FINGERPRINT_ALGORITHM).digest(value));
    } catch (NoSuchAlgorithmException | com.fasterxml.jackson.core.JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  private long nanos(Instant time) {
    return Math.addExact(Math.multiplyExact(time.getEpochSecond(), 1_000_000_000L), time.getNano());
  }

  private String encodeToken(long timestamp, String discriminator) {
    String value = CURSOR_VERSION + TOKEN_SEPARATOR + timestamp + TOKEN_SEPARATOR + discriminator;
    return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }

  private String decode(String value) {
    return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
  }

  private record Row(long timestamp, Map<String, Object> values) { }
}
