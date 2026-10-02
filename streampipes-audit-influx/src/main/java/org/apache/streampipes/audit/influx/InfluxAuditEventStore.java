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

import org.apache.streampipes.audit.api.AuditEntryDetails;
import org.apache.streampipes.audit.api.AuditEvent;
import org.apache.streampipes.audit.api.AuditEventReader;
import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.audit.api.AuditPage;
import org.apache.streampipes.audit.api.AuditQuery;
import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;
import org.apache.streampipes.dataexplorer.influx.InfluxQueryTransport;
import org.apache.streampipes.dataexplorer.influx.InfluxWriteTransport;
import org.apache.streampipes.dataexplorer.influx.client.InfluxConnectionSettings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Single-writer append adapter with independent bounded reads. Retention is not implemented. */
public final class InfluxAuditEventStore implements AuditEventStore, AuditEventReader {
  private static final String DEFAULT_ORGANIZATION = "sp";
  private static final String WATERMARK_QUERY = "SELECT LAST(\"%s\") FROM \"%s\""
      .formatted(InfluxAuditSchema.EVENT_ID, InfluxAuditSchema.MEASUREMENT);
  private static final int MAXIMUM_EVENT_BYTES = 32 * 1024;
  private static final int MAXIMUM_WRITE_ATTEMPTS = 2;
  private static final int HTTP_NO_CONTENT = 204;
  private static final int HTTP_TOO_MANY_REQUESTS = 429;
  private static final int HTTP_SERVER_ERROR_MINIMUM = 500;
  private static final long CLOCK_SKEW_TOLERANCE_NANOS = TimeUnit.SECONDS.toNanos(1);
  private static final Duration TIMEOUT = Duration.ofSeconds(5);
  private final InfluxQueryTransport queries;
  private final InfluxWriteTransport writer;
  private final Clock clock;
  private final InfluxAuditDatabaseProvisioner provisioner;
  private boolean provisioned;
  private final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
  private long lastTimestamp;
  private boolean initialized;
  private boolean closed;

  public InfluxAuditEventStore(String url, String database, String token) {
    this(url, database, token, DEFAULT_ORGANIZATION);
  }

  public InfluxAuditEventStore(String url, String database, String token, String organization) {
    this(url, database, token, organization, Clock.systemUTC());
  }

  InfluxAuditEventStore(String url, String database, String token, Clock clock) {
    this(url, database, token, DEFAULT_ORGANIZATION, clock);
  }

  private InfluxAuditEventStore(String url, String database, String token, String organization, Clock clock) {
    this.clock = clock;
    var endpoint = URI.create(url);
    if (!("http".equals(endpoint.getScheme()) || "https".equals(endpoint.getScheme()))
        || endpoint.getHost() == null || endpoint.getUserInfo() != null
        || endpoint.getRawQuery() != null || endpoint.getFragment() != null
        || endpoint.getPath() != null && !endpoint.getPath().isEmpty() && !"/".equals(endpoint.getPath())
        || database == null || database.isBlank() || token == null || token.isBlank()) {
      throw new IllegalArgumentException("Invalid audit Influx connection configuration");
    }
    int port = endpoint.getPort() == -1 ? ("https".equals(endpoint.getScheme()) ? 443 : 80) : endpoint.getPort();
    var settings = InfluxConnectionSettings.from(endpoint.getScheme(), endpoint.getHost(), port, database, token);
    this.provisioner = new InfluxAuditDatabaseProvisioner(settings, organization, TIMEOUT);
    this.queries = new InfluxQueryTransport(settings, TIMEOUT);
    this.writer = new InfluxWriteTransport(settings, TIMEOUT);
  }

  @Override
  public synchronized void initialize() {
    if (closed) {
      throw new IllegalStateException("Audit store closed");
    }
    if (!provisioned) {
      provisioner.ensureDatabase();
      provisioned = true;
    }
  }

  @Override
  public synchronized void append(AuditEvent<?> event) {
    if (closed) {
      throw new IllegalStateException("Audit store closed");
    }
    initialize();
    if (!initialized) {
      initializeWatermark();
    }
    long recordedAt = toNanos(event.recordedAt());
    if (lastTimestamp > Math.addExact(toNanos(clock.instant()), CLOCK_SKEW_TOLERANCE_NANOS)) {
      throw new IllegalStateException("Audit timestamp watermark is ahead of the clock");
    }
    long timestamp = Math.max(recordedAt, Math.addExact(lastTimestamp, 1));
    var point = Point.measurement(InfluxAuditSchema.MEASUREMENT)
        .time(timestamp, TimeUnit.NANOSECONDS)
        .tag(InfluxAuditSchema.EVENT_TYPE, event.definition().id())
        .addField(InfluxAuditSchema.EVENT_ID, event.eventId().toString())
        .addField(InfluxAuditSchema.OUTCOME, event.outcome().name().toLowerCase(Locale.ROOT))
        .addField(InfluxAuditSchema.ACTOR, event.actor());
    if (event.resourceType() != null) {
      point.addField(InfluxAuditSchema.RESOURCE_TYPE, event.resourceType());
    }
    if (event.resourceId() != null) {
      point.addField(InfluxAuditSchema.RESOURCE_ID, event.resourceId());
    }
    ObjectNode details = event.details() == null ? mapper.createObjectNode() : mapper.valueToTree(event.details());
    if (timestamp != recordedAt) {
      if (details.has(InfluxAuditSchema.RECORDED_AT)) {
        throw new IllegalArgumentException("Reserved audit details property");
      }
      details.put(InfluxAuditSchema.RECORDED_AT, event.recordedAt().toString());
    }
    if (!details.isEmpty()) {
      point.addField(InfluxAuditSchema.DETAILS, details.toString());
    }
    String line = point.build().lineProtocol();
    if (line.getBytes(StandardCharsets.UTF_8).length > MAXIMUM_EVENT_BYTES) {
      throw new IllegalArgumentException("Audit event exceeds size limit");
    }
    // Reserve before I/O; even an uncertain write must not let a later event reuse the timestamp.
    lastTimestamp = timestamp;
    for (int attempt = 0; attempt < MAXIMUM_WRITE_ATTEMPTS; attempt++) {
      try {
        int status = writer.write(line);
        if (status == HTTP_NO_CONTENT) {
          return;
        }
        if (attempt < MAXIMUM_WRITE_ATTEMPTS - 1
            && (status == HTTP_TOO_MANY_REQUESTS || status >= HTTP_SERVER_ERROR_MINIMUM)) {
          continue;
        }
        throw new IllegalStateException("Audit write failed with HTTP " + status);
      } catch (IOException e) {
        if (Thread.currentThread().isInterrupted()) {
          throw new IllegalStateException("Audit write interrupted");
        }
        if (attempt == MAXIMUM_WRITE_ATTEMPTS - 1) {
          throw new IllegalStateException("Audit write unavailable");
        }
      }
    }
  }

  @Override
  public AuditPage query(AuditQuery query) {
    return new InfluxAuditEventReader(queries).query(query);
  }

  @Override
  public Optional<AuditEntryDetails> find(UUID eventId, String locator) {
    return new InfluxAuditEventReader(queries).find(eventId, locator);
  }

  private void initializeWatermark() {
    long maximum = 0;
    // No current-time upper bound: clock rollback must not conceal a stored future watermark.
    try (var cursor = queries.open(new Query(WATERMARK_QUERY),
        QueryTimestampFormat.EPOCH_NANOS)) {
      while (cursor.hasNext()) {
        var batch = cursor.next();
        int time = batch.columns().indexOf(InfluxAuditSchema.TIME);
        if (time < 0 && !batch.rows().isEmpty()) {
          throw new IllegalStateException("Audit watermark response has no timestamp");
        }
        for (var row : batch.rows()) {
          if (!(row.get(time) instanceof Number value)) {
            throw new IllegalStateException("Invalid audit watermark");
          }
          maximum = Math.max(maximum, value.longValue());
        }
      }
    }
    lastTimestamp = maximum;
    initialized = true;
  }

  private long toNanos(Instant timestamp) {
    return Math.addExact(Math.multiplyExact(timestamp.getEpochSecond(), 1_000_000_000L), timestamp.getNano());
  }

  @Override
  public synchronized void close() {
    closed = true;
    provisioner.close();
    queries.close();
    writer.close();
  }
}
