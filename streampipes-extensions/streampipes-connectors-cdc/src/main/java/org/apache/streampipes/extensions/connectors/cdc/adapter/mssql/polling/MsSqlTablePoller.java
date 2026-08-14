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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class MsSqlTablePoller {

  private final MsSqlPollingSettings settings;
  private final List<MsSqlColumn> expectedSchema;
  private final String sequenceColumn;
  private final MsSqlPollingRowSource rowSource;
  private final Consumer<Map<String, Object>> collector;
  private final IExtensionsLogger logger;

  private Optional<BigDecimal> cursor;
  private String reportedSchemaMismatch;

  public MsSqlTablePoller(MsSqlPollingSettings settings,
                          List<MsSqlColumn> expectedSchema,
                          String sequenceColumn,
                          Optional<BigDecimal> startupCursor,
                          MsSqlPollingRowSource rowSource,
                          Consumer<Map<String, Object>> collector,
                          IExtensionsLogger logger) {
    this.settings = settings;
    this.expectedSchema = List.copyOf(expectedSchema);
    this.sequenceColumn = sequenceColumn;
    this.rowSource = rowSource;
    this.collector = collector;
    this.logger = logger;
    this.cursor = startupCursor;
  }

  public void poll() throws Exception {
    try (MsSqlPollingRowSource.PollSession session = rowSource.openSession()) {
      String mismatch = describeSchemaMismatch(expectedSchema, session.currentSchema());
      if (mismatch != null) {
        reportSchemaMismatch(mismatch);
        return;
      }
      reportSchemaRecovery();

      int rowsProcessed = 0;
      while (rowsProcessed < settings.maxRowsPerPoll()) {
        int requested = Math.min(settings.batchSize(), settings.maxRowsPerPoll() - rowsProcessed);
        List<MsSqlRow> batch = session.readAfter(cursor, requested);
        if (batch.isEmpty()) {
          return;
        }

        validateBatch(batch, cursor);
        for (MsSqlRow row : batch) {
          collector.accept(row.event());
        }

        BigDecimal highestSequence = batch.get(batch.size() - 1).sequence();
        cursor = Optional.of(highestSequence);
        rowsProcessed += batch.size();

        if (batch.size() < requested) {
          return;
        }
      }
    }
  }

  private void validateBatch(List<MsSqlRow> batch, Optional<BigDecimal> cursor) {
    BigDecimal previous = cursor.orElse(null);
    for (MsSqlRow row : batch) {
      if (!row.event().containsKey(sequenceColumn)) {
        throw new IllegalStateException("Polled row does not contain sequence column " + sequenceColumn);
      }
      if (previous != null && row.sequence().compareTo(previous) <= 0) {
        throw new IllegalStateException("SQL Server returned a non-increasing sequence batch.");
      }
      previous = row.sequence();
    }
  }

  static String describeSchemaMismatch(List<MsSqlColumn> expected, List<MsSqlColumn> actual) {
    if (expected.equals(actual)) {
      return null;
    }

    return "Expected " + describeSchema(expected) + " but found " + describeSchema(actual);
  }

  private static String describeSchema(List<MsSqlColumn> schema) {
    return schema.stream()
        .map(column -> column.name() + " " + column.typeName() + "(" + column.jdbcType() + ")")
        .toList()
        .toString();
  }

  private void reportSchemaMismatch(String mismatch) {
    if (!mismatch.equals(reportedSchemaMismatch)) {
      logger.error("MSSQL table schema mismatch: " + mismatch, new IllegalStateException(mismatch));
      reportedSchemaMismatch = mismatch;
    }
  }

  private void reportSchemaRecovery() {
    if (reportedSchemaMismatch != null) {
      logger.info("MSSQL table schema restored", "Polling has resumed after the expected schema was restored.");
      reportedSchemaMismatch = null;
    }
  }

}
