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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MsSqlTablePollerTest {

  private static final List<MsSqlColumn> SCHEMA = List.of(
      new MsSqlColumn("sequence_id", Types.DECIMAL, "decimal", 38, 0, false),
      new MsSqlColumn("value", Types.VARCHAR, "varchar", 100, 0, true)
  );

  @Test
  void allExistingEmitsRowsAndCheckpointsCompletedBatch() throws Exception {
    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    TestRowSource source = new TestRowSource(List.of(
        row("9223372036854775808", "first"),
        row("9223372036854775809", "second")
    ));
    List<Map<String, Object>> emitted = new ArrayList<>();
    MsSqlTablePoller poller = new MsSqlTablePoller(
        "adapter-1",
        new MsSqlPollingSettings(StartupMode.ALL_EXISTING, null, 2, 10),
        SCHEMA,
        "sequence_id",
        source,
        store,
        emitted::add,
        new TestLogger()
    );

    poller.poll();

    assertEquals(List.of("first", "second"), emitted.stream().map(event -> event.get("value")).toList());
    assertEquals(
        new BigDecimal("9223372036854775809"),
        store.load("adapter-1").cursor().orElseThrow()
    );
    assertEquals(1, source.openCount);
    assertEquals(1, source.closeCount);
  }

  @Test
  void newRowsStartsAtCurrentMaximumAndReadsRowsAddedLater() throws Exception {
    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    TestRowSource source = new TestRowSource(new ArrayList<>(List.of(row("10", "existing"))));
    List<Map<String, Object>> emitted = new ArrayList<>();
    MsSqlTablePoller poller = poller(
        new MsSqlPollingSettings(StartupMode.NEW_ROWS, null, 2, 10), source, store, emitted::add, new TestLogger()
    );

    poller.poll();
    source.rows.add(row("11", "new"));
    poller.poll();

    assertEquals(List.of("new"), emitted.stream().map(event -> event.get("value")).toList());
    assertEquals(new BigDecimal("11"), store.load("adapter-1").cursor().orElseThrow());
  }

  @Test
  void customSequenceIsExclusiveAndExactAboveLongRange() throws Exception {
    BigDecimal custom = new BigDecimal("9223372036854775808");
    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    TestRowSource source = new TestRowSource(List.of(
        row(custom.toPlainString(), "boundary"),
        row("9223372036854775809", "after")
    ));
    List<Map<String, Object>> emitted = new ArrayList<>();

    poller(
        new MsSqlPollingSettings(StartupMode.CUSTOM_SEQUENCE, custom, 1, 10),
        source,
        store,
        emitted::add,
        new TestLogger()
    ).poll();

    assertEquals(List.of("after"), emitted.stream().map(event -> event.get("value")).toList());
  }

  @Test
  void maxRowsPerPollContinuesBacklogOnNextInvocation() throws Exception {
    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    TestRowSource source = new TestRowSource(List.of(
        row("1", "one"), row("2", "two"), row("3", "three"), row("4", "four"), row("5", "five")
    ));
    List<Map<String, Object>> emitted = new ArrayList<>();
    MsSqlTablePoller poller = poller(
        new MsSqlPollingSettings(StartupMode.ALL_EXISTING, null, 2, 4), source, store, emitted::add, new TestLogger()
    );

    poller.poll();
    assertEquals(List.of("one", "two", "three", "four"), values(emitted));
    poller.poll();

    assertEquals(List.of("one", "two", "three", "four", "five"), values(emitted));
    assertEquals(2, source.openCount);
  }

  @Test
  void collectorFailureReplaysWholeUncheckpointedBatch() throws Exception {
    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    TestRowSource source = new TestRowSource(List.of(row("1", "one"), row("2", "two")));
    AtomicBoolean failOnce = new AtomicBoolean(true);
    List<Map<String, Object>> emitted = new ArrayList<>();
    MsSqlTablePoller poller = poller(
        new MsSqlPollingSettings(StartupMode.ALL_EXISTING, null, 2, 10),
        source,
        store,
        event -> {
          emitted.add(event);
          if (failOnce.compareAndSet(true, false)) {
            throw new IllegalStateException("collector failed");
          }
        },
        new TestLogger()
    );

    assertThrows(IllegalStateException.class, poller::poll);
    assertTrue(store.load("adapter-1").cursor().isEmpty());
    poller.poll();

    assertEquals(List.of("one", "one", "two"), values(emitted));
    assertEquals(new BigDecimal("2"), store.load("adapter-1").cursor().orElseThrow());
  }

  @Test
  void schemaMismatchIsSuppressedUntilDistinctAndReportsRecovery() throws Exception {
    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    TestRowSource source = new TestRowSource(List.of(row("1", "one")));
    TestLogger logger = new TestLogger();
    MsSqlTablePoller poller = poller(
        new MsSqlPollingSettings(StartupMode.ALL_EXISTING, null, 1, 10), source, store, event -> { }, logger
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

  @Test
  void sequenceRegressionPausesAndPreservesCheckpoint() throws Exception {
    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    CheckpointSnapshot saved = store.save("adapter-1", 0, Optional.of(new BigDecimal("100"))).orElseThrow();
    TestRowSource source = new TestRowSource(List.of(row("10", "old")));
    TestLogger logger = new TestLogger();
    MsSqlTablePoller poller = poller(
        new MsSqlPollingSettings(StartupMode.ALL_EXISTING, null, 1, 10), source, store, event -> { }, logger
    );

    poller.poll();
    poller.poll();

    assertEquals(1, logger.errors.size());
    assertEquals(saved, store.load("adapter-1"));
  }

  @Test
  void deletionRejectsStaleSaveAndReappliesStartupMode() throws Exception {
    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    TestRowSource source = new TestRowSource(List.of(row("1", "one")));
    List<Map<String, Object>> emitted = new ArrayList<>();
    AtomicBoolean deleted = new AtomicBoolean();
    MsSqlTablePoller poller = poller(
        new MsSqlPollingSettings(StartupMode.NEW_ROWS, null, 1, 10),
        source,
        store,
        event -> {
          emitted.add(event);
          if (deleted.compareAndSet(false, true)) {
            CheckpointSnapshot current = store.load("adapter-1");
            assertTrue(store.delete("adapter-1", current.revision()));
          }
        },
        new TestLogger()
    );
    CheckpointSnapshot initial = store.save("adapter-1", 0, Optional.empty()).orElseThrow();
    assertTrue(initial.present());

    poller.poll();
    assertFalse(store.load("adapter-1").present());
    poller.poll();

    assertEquals(List.of("one"), values(emitted));
    assertEquals(new BigDecimal("1"), store.load("adapter-1").cursor().orElseThrow());
  }

  private static MsSqlTablePoller poller(MsSqlPollingSettings settings,
                                         MsSqlPollingRowSource source,
                                         CheckpointStore store,
                                         java.util.function.Consumer<Map<String, Object>> collector,
                                         IExtensionsLogger logger) {
    return new MsSqlTablePoller(
        "adapter-1", settings, SCHEMA, "sequence_id", source, store, collector, logger
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
    public void error(Exception e) {
    }

    @Override
    public void error(String details, Exception e) {
      errors.add(details);
    }

    @Override
    public void info(String title, String details) {
      infos.add(details);
    }

    @Override
    public void warn(String title, String details) {
    }
  }
}
