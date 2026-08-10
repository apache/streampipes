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

package org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling;

import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.model.monitoring.SpLogMessage;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MsSqlTablePollerTest {

  private static final List<MsSqlColumn> SCHEMA = List.of(
      new MsSqlColumn("sequence_id", Types.DECIMAL, "decimal", 38, 0, false),
      new MsSqlColumn("value", Types.VARCHAR, "varchar", 100, 0, true)
  );

  @Test
  void emitsOnlyRowsAddedAfterStartupWatermark() throws Exception {
    TestRowSource source = new TestRowSource(new ArrayList<>(List.of(row("10", "existing"))));
    List<Map<String, Object>> emitted = new ArrayList<>();
    MsSqlTablePoller poller = poller(Optional.of(new BigDecimal("10")), source, emitted::add);

    source.rows.add(row("11", "new"));
    poller.poll();

    assertEquals(List.of("new"), values(emitted));
  }

  @Test
  void emptyTableAtStartupEmitsItsFirstRow() throws Exception {
    TestRowSource source = new TestRowSource(new ArrayList<>());
    List<Map<String, Object>> emitted = new ArrayList<>();
    MsSqlTablePoller poller = poller(Optional.empty(), source, emitted::add);

    source.rows.add(row("1", "first"));
    poller.poll();

    assertEquals(List.of("first"), values(emitted));
  }

  @Test
  void advancesInMemoryCursorAcrossBatchesAndPolls() throws Exception {
    TestRowSource source = new TestRowSource(List.of(
        row("1", "one"), row("2", "two"), row("3", "three"), row("4", "four"), row("5", "five")
    ));
    List<Map<String, Object>> emitted = new ArrayList<>();
    MsSqlTablePoller poller = poller(Optional.empty(), new MsSqlPollingSettings(2, 4), source, emitted::add);

    poller.poll();
    assertEquals(List.of("one", "two", "three", "four"), values(emitted));
    poller.poll();

    assertEquals(List.of("one", "two", "three", "four", "five"), values(emitted));
    assertEquals(2, source.openCount);
    assertEquals(2, source.closeCount);
  }

  @Test
  void collectorFailureDoesNotAdvanceTheInMemoryCursor() throws Exception {
    TestRowSource source = new TestRowSource(List.of(row("1", "one"), row("2", "two")));
    AtomicBoolean failOnce = new AtomicBoolean(true);
    List<Map<String, Object>> emitted = new ArrayList<>();
    MsSqlTablePoller poller = poller(Optional.empty(), source, event -> {
      emitted.add(event);
      if (failOnce.compareAndSet(true, false)) {
        throw new IllegalStateException("collector failed");
      }
    });

    assertThrows(IllegalStateException.class, poller::poll);
    poller.poll();

    assertEquals(List.of("one", "one", "two"), values(emitted));
  }

  @Test
  void schemaMismatchIsSuppressedUntilDistinctAndReportsRecovery() throws Exception {
    TestRowSource source = new TestRowSource(List.of(row("1", "one")));
    TestLogger logger = new TestLogger();
    MsSqlTablePoller poller = new MsSqlTablePoller(
        new MsSqlPollingSettings(1, 10), SCHEMA, "sequence_id", Optional.empty(), source, event -> { }, logger
    );
    source.schema = List.of(SCHEMA.get(0));

    poller.poll();
    poller.poll();
    source.schema = List.of(SCHEMA.get(1), SCHEMA.get(0));
    poller.poll();
    source.schema = SCHEMA;
    poller.poll();

    assertEquals(2, logger.errors.size());
    assertEquals(1, logger.infos.size());
  }

  private static MsSqlTablePoller poller(Optional<BigDecimal> startupCursor,
                                         TestRowSource source,
                                         java.util.function.Consumer<Map<String, Object>> collector) {
    return poller(startupCursor, new MsSqlPollingSettings(2, 10), source, collector);
  }

  private static MsSqlTablePoller poller(Optional<BigDecimal> startupCursor,
                                         MsSqlPollingSettings settings,
                                         TestRowSource source,
                                         java.util.function.Consumer<Map<String, Object>> collector) {
    return new MsSqlTablePoller(
        settings, SCHEMA, "sequence_id", startupCursor, source, collector, new TestLogger()
    );
  }

  private static List<Object> values(List<Map<String, Object>> events) {
    return events.stream().map(event -> event.get("value")).toList();
  }

  private static MsSqlRow row(String sequence, String value) {
    Map<String, Object> event = new LinkedHashMap<>();
    event.put("sequence_id", new BigDecimal(sequence));
    event.put("value", value);
    return new MsSqlRow(new BigDecimal(sequence), event);
  }

  private static class TestRowSource implements MsSqlPollingRowSource {
    private final List<MsSqlRow> rows;
    private List<MsSqlColumn> schema = SCHEMA;
    private int openCount;
    private int closeCount;

    private TestRowSource(List<MsSqlRow> rows) {
      this.rows = rows;
    }

    @Override
    public PollSession openSession() {
      openCount++;
      return new PollSession() {
        @Override
        public List<MsSqlColumn> currentSchema() {
          return schema;
        }

        @Override
        public Optional<BigDecimal> maximumSequence() {
          return rows.stream().map(MsSqlRow::sequence).max(BigDecimal::compareTo);
        }

        @Override
        public List<MsSqlRow> readAfter(Optional<BigDecimal> cursor, int limit) {
          return rows.stream()
              .filter(row -> cursor.isEmpty() || row.sequence().compareTo(cursor.orElseThrow()) > 0)
              .limit(limit)
              .toList();
        }

        @Override
        public void close() {
          closeCount++;
        }
      };
    }
  }

  private static class TestLogger implements IExtensionsLogger {
    private final List<String> errors = new ArrayList<>();
    private final List<String> infos = new ArrayList<>();

    @Override
    public void log(SpLogMessage logMessage) {
    }

    @Override
    public void error(Exception exception) {
      errors.add(exception.getMessage());
    }

    @Override
    public void error(String title, Exception exception) {
      errors.add(title + exception.getMessage());
    }

    @Override
    public void info(String title, String description) {
      infos.add(title + description);
    }

    @Override
    public void warn(String title, String description) {
    }
  }
}
