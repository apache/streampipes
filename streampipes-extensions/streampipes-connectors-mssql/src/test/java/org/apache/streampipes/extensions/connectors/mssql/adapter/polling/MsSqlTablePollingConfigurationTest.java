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

package org.apache.streampipes.extensions.connectors.mssql.adapter.polling;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MsSqlTablePollingConfigurationTest {

  @Test
  void intervalAtAndAboveAdministrativeMinimumIsAccepted() {
    assertDoesNotThrow(() -> config(2, 10, 10).validate(2));
    assertDoesNotThrow(() -> config(3, 10, 10).validate(2));
  }

  @Test
  void intervalBelowAdministrativeMinimumIsRejected() {
    assertThrows(IllegalArgumentException.class, () ->
        config(1, 10, 10).validate(2)
    );
  }

  @Test
  void invalidBatchLimitsAreRejected() {
    assertThrows(IllegalArgumentException.class, () ->
        config(2, 0, 10).validate(1)
    );
    assertThrows(IllegalArgumentException.class, () ->
        config(2, 11, 10).validate(1)
    );
  }

  @Test
  void sqlServerIdentifiersEscapeClosingBrackets() {
    assertEquals("[measurements]]]", MsSqlTablePollingClient.quoteIdentifier("measurements]"));
  }

  @Test
  void tableSelectionPreservesDotsAndClosingBracketsInBothIdentifiers() {
    MsSqlTableIdentifier table = new MsSqlTableIdentifier("sales.v2", "orders].current");

    assertEquals(table, MsSqlTableIdentifier.decode(table.encode()));
    assertEquals("[sales.v2].[orders]].current]", table.displayName());
  }

  @Test
  void tableSelectionAcceptsLegacySchemaDotTableValue() {
    assertEquals(
        new MsSqlTableIdentifier("dbo", "measurements"),
        MsSqlTableIdentifier.decode("dbo.measurements")
    );
  }

  private static MsSqlTablePollingConfig config(int interval,
                                                int batchSize,
                                                int maxRows) {
    return new MsSqlTablePollingConfig(
        "localhost",
        1433,
        "database",
        "user",
        "password",
        true,
        false,
        "UTC",
        new MsSqlTableIdentifier("dbo", "measurements"),
        "sequence_id",
        interval,
        batchSize,
        maxRows
    );
  }

}
