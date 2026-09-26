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
  private static final String MEASUREMENT = "audit_events";
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
    this(url, database, token, "sp");
  }

  public InfluxAuditEventStore(String url, String database, String token, String organization) {
    this(url, database, token, organization, Clock.systemUTC());
  }

  InfluxAuditEventStore(String url, String database, String token, Clock clock) {
    this(url, database, token, "sp", clock);
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
    if (lastTimestamp > Math.addExact(toNanos(clock.instant()), TimeUnit.SECONDS.toNanos(1))) {
      throw new IllegalStateException("Audit timestamp watermark is ahead of the clock");
    }
    long timestamp = Math.max(recordedAt, Math.addExact(lastTimestamp, 1));
    var point = Point.measurement(MEASUREMENT)
        .time(timestamp, TimeUnit.NANOSECONDS)
        .tag("event_type", event.definition().id())
        .addField("event_id", event.eventId().toString())
        .addField("outcome", event.outcome().name().toLowerCase(Locale.ROOT))
        .addField("actor", event.actor());
    if (event.resourceType() != null) {
      point.addField("resource_type", event.resourceType());
    }
    if (event.resourceId() != null) {
      point.addField("resource_id", event.resourceId());
    }
    ObjectNode details = event.details() == null ? mapper.createObjectNode() : mapper.valueToTree(event.details());
    if (timestamp != recordedAt) {
      if (details.has("recorded_at")) {
        throw new IllegalArgumentException("Reserved audit details property");
      }
      details.put("recorded_at", event.recordedAt().toString());
    }
    if (!details.isEmpty()) {
      point.addField("details", details.toString());
    }
    String line = point.build().lineProtocol();
    if (line.getBytes(StandardCharsets.UTF_8).length > 32 * 1024) {
      throw new IllegalArgumentException("Audit event exceeds size limit");
    }
    // Reserve before I/O; even an uncertain write must not let a later event reuse the timestamp.
    lastTimestamp = timestamp;
    for (int attempt = 0; attempt < 2; attempt++) {
      try {
        int status = writer.write(line);
        if (status == 204) {
          return;
        }
        if (attempt == 0 && (status == 429 || status >= 500)) {
          continue;
        }
        throw new IllegalStateException("Audit write failed with HTTP " + status);
      } catch (IOException e) {
        if (Thread.currentThread().isInterrupted()) {
          throw new IllegalStateException("Audit write interrupted");
        }
        if (attempt == 1) {
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
    try (var cursor = queries.open(new Query("SELECT LAST(\"event_id\") FROM \"audit_events\""),
        QueryTimestampFormat.EPOCH_NANOS)) {
      while (cursor.hasNext()) {
        var batch = cursor.next();
        int time = batch.columns().indexOf("time");
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
