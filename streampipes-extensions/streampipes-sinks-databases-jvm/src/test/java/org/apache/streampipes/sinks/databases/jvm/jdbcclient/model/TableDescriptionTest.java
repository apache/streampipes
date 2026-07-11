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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link TableDescription}.
 */
class TableDescriptionTest {

  /**
   * Builds a table description to validate. The stream has four fields (timestamp, sensorId,
   * mass_flow, volume_flow).
   *
   * @param existingColumns a map of columns the table contains.
   * @return a table description to validate.
   */
  private TableDescription buildTableDescription(HashMap<String, DbDataTypes> existingColumns) {
    List<EventProperty> streamFields = new ArrayList<>();
    streamFields.add(
        new EventPropertyPrimitive(XSD.LONG.toString(), "timestamp", "", null));
    streamFields.add(
        new EventPropertyPrimitive(XSD.STRING.toString(), "sensorId", "", null));
    streamFields.add(
        new EventPropertyPrimitive(XSD.FLOAT.toString(), "mass_flow", "", null));
    streamFields.add(
        new EventPropertyPrimitive(XSD.FLOAT.toString(), "volume_flow", "", null));

    TableDescription table = new TableDescription("sensor_data", new EventSchema(streamFields));
    table.setDataTypesHashMap(existingColumns);
    return table;
  }

  /**
   * The columns of a table that matches the stream.
   */
  private HashMap<String, DbDataTypes> matchingColumns() {
    HashMap<String, DbDataTypes> columns = new HashMap<>();
    columns.put("timestamp", DbDataTypes.BIGINT);
    columns.put("sensorId", DbDataTypes.VAR_CHAR);
    columns.put("mass_flow", DbDataTypes.REAL);
    columns.put("volume_flow", DbDataTypes.REAL);
    return columns;
  }

  /**
   * Asserts that every expected text fragment appears in the actual message.
   *
   * @param actual the message produced by {@link TableDescription#validateTable()}.
   * @param expected the column names and type names the message must contain.
   */
  private void assertContainsAll(String actual, List<String> expected) {
    for (String fragment : expected) {
      assertTrue(actual.contains(fragment),
          "expected the message to contain \"" + fragment + "\", but was: " + actual);
    }
  }

  @Test
  void testValidateTable_tableMatchesStream_doesNotThrow() {
    TableDescription table = buildTableDescription(matchingColumns());
    assertDoesNotThrow(table::validateTable);
  }

  @Test
  void testValidateTable_oneColumnMissing_reportsThatColumn() {
    HashMap<String, DbDataTypes> columns = matchingColumns();
    columns.remove("volume_flow");
    TableDescription table = buildTableDescription(columns);

    SpRuntimeException thrown = assertThrows(SpRuntimeException.class, table::validateTable);
    String actual = thrown.getMessage();
    List<String> expected = List.of("Column 'volume_flow' is missing");

    assertContainsAll(actual, expected);
  }

  @Test
  void testValidateTable_twoColumnsMissing_reportsBothColumns() {
    HashMap<String, DbDataTypes> columns = matchingColumns();
    columns.remove("mass_flow");
    columns.remove("volume_flow");
    TableDescription table = buildTableDescription(columns);

    SpRuntimeException thrown = assertThrows(SpRuntimeException.class, table::validateTable);
    String actual = thrown.getMessage();
    List<String> expected = List.of("'mass_flow'", "'volume_flow'");

    assertContainsAll(actual, expected);
  }

  @Test
  void testValidateTable_threeColumnsMissing_reportsAllColumns() {
    HashMap<String, DbDataTypes> columns = matchingColumns();
    columns.remove("timestamp");
    columns.remove("sensorId");
    columns.remove("mass_flow");
    TableDescription table = buildTableDescription(columns);

    SpRuntimeException thrown = assertThrows(SpRuntimeException.class, table::validateTable);
    String actual = thrown.getMessage();
    List<String> expected = List.of("'timestamp'", "'sensorId'", "'mass_flow'");

    assertContainsAll(actual, expected);
  }

  @Test
  void testValidateTable_oneColumnHasWrongType_reportsTypes() {
    HashMap<String, DbDataTypes> columns = matchingColumns();
    columns.put("mass_flow", DbDataTypes.BIGINT);
    TableDescription table = buildTableDescription(columns);

    SpRuntimeException thrown = assertThrows(SpRuntimeException.class, table::validateTable);
    String actual = thrown.getMessage();
    // The column, the type the stream sends ("float"), and the type the table has ("BIGINT")
    List<String> expected = List.of("Type mismatch", "'mass_flow'", "float", "BIGINT");

    assertContainsAll(actual, expected);
  }

  @Test
  void testValidateTable_twoColumnsHaveWrongType_reportsBothTypes() {
    HashMap<String, DbDataTypes> columns = matchingColumns();
    columns.put("mass_flow", DbDataTypes.BIGINT);
    columns.put("volume_flow", DbDataTypes.BOOLEAN);
    TableDescription table = buildTableDescription(columns);

    SpRuntimeException thrown = assertThrows(SpRuntimeException.class, table::validateTable);
    String actual = thrown.getMessage();
    List<String> expected = List.of("Type mismatch", "'mass_flow'", "BIGINT", "'volume_flow'", "BOOLEAN");

    assertContainsAll(actual, expected);
  }
}