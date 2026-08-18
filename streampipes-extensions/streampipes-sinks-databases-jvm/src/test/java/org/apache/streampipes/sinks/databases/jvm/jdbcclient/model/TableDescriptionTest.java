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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for the implementation of the {@link TableDescription} class.
 */
class TableDescriptionTest {

  private static final String TABLE_NAME = "sensor_data";

  private static final List<EventProperty> SENSOR_DATA_STREAM = List.of(
      new EventPropertyPrimitive(XSD.LONG.toString(), "timestamp", "", null),
      new EventPropertyPrimitive(XSD.STRING.toString(), "sensorId", "", null),
      new EventPropertyPrimitive(XSD.FLOAT.toString(), "mass_flow", "", null),
      new EventPropertyPrimitive(XSD.FLOAT.toString(), "volume_flow", "", null)
  );

  /**
   * Set up a table description for test cases.
   *
   * @param name the table name in the database.
   * @param dataStream the fields the sink receives from the data stream.
   * @param cols the columns the table contains.
   * @return the defined table description.
   */
  TableDescription newTable(String name, List<EventProperty> dataStream, Map<String, DbDataTypes> cols) {
    TableDescription table = new TableDescription(name, new EventSchema(new ArrayList<>(dataStream)));
    table.setDataTypesHashMap(new HashMap<>(cols));
    return table;
  }

  @Test
  void testValidateTable_tableMatchesStream_throwsNoException() {
    Map<String, DbDataTypes> cols = Map.of(
        "timestamp", DbDataTypes.BIGINT,
        "sensorId", DbDataTypes.VAR_CHAR,
        "mass_flow", DbDataTypes.REAL,
        "volume_flow", DbDataTypes.REAL
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
    } catch (SpRuntimeException e) {
      fail("Exception on #validateTable: " + e.getMessage());
    }
  }

  @Test
  void testValidateTable_oneColMissing_reportsMissingCol() {
    Map<String, DbDataTypes> cols = Map.of(
        "timestamp", DbDataTypes.BIGINT,
        "sensorId", DbDataTypes.VAR_CHAR,
        "volume_flow", DbDataTypes.REAL
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected = "Column 'mass_flow' is missing in table '" + TABLE_NAME + "'";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testValidateTable_twoColsMissing_reportsMissingCols() {
    Map<String, DbDataTypes> cols = Map.of(
        "sensorId", DbDataTypes.VAR_CHAR,
        "volume_flow", DbDataTypes.REAL
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected = "Columns 'timestamp' and 'mass_flow' are missing in table '" + TABLE_NAME + "'";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testValidateTable_threeColsMissing_reportsMissingCols() {
    Map<String, DbDataTypes> cols = Map.of(
        "volume_flow", DbDataTypes.REAL
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected = "Columns 'timestamp', 'sensorId', and 'mass_flow' are missing in table '" + TABLE_NAME + "'";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testValidateTable_allColsMissing_reportsMissingCols() {
    Map<String, DbDataTypes> cols = Map.of(
        "order_id", DbDataTypes.BIGINT,
        "customer", DbDataTypes.VAR_CHAR
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected =
          "Columns 'timestamp', 'sensorId', 'mass_flow', and 'volume_flow' are missing in table '" + TABLE_NAME + "'";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testValidateTable_oneTypeMismatch_reportsExpectedType() {
    Map<String, DbDataTypes> cols = Map.of(
        "timestamp", DbDataTypes.BIGINT,
        "sensorId", DbDataTypes.VAR_CHAR,
        "mass_flow", DbDataTypes.BIGINT,
        "volume_flow", DbDataTypes.REAL
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected =
          "Type mismatch in table '" + TABLE_NAME + "' for column "
              + "'mass_flow' (the data stream provides float but the table column is BIGINT)";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testValidateTable_twoTypeMismatches_reportsExpectedTypes() {
    Map<String, DbDataTypes> cols = Map.of(
        "timestamp", DbDataTypes.BIGINT,
        "sensorId", DbDataTypes.VAR_CHAR,
        "mass_flow", DbDataTypes.BIGINT,
        "volume_flow", DbDataTypes.BOOLEAN
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected =
          "Type mismatch in table '" + TABLE_NAME + "' for columns "
              + "'mass_flow' (the data stream provides float but the table column is BIGINT) "
              + "and 'volume_flow' (the data stream provides float but the table column is BOOLEAN)";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testValidateTable_threeTypeMismatches_reportsExpectedTypes() {
    Map<String, DbDataTypes> cols = Map.of(
        "timestamp", DbDataTypes.BOOLEAN,
        "sensorId", DbDataTypes.VAR_CHAR,
        "mass_flow", DbDataTypes.BIGINT,
        "volume_flow", DbDataTypes.BOOLEAN
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected =
          "Type mismatch in table '" + TABLE_NAME + "' for columns "
              + "'timestamp' (the data stream provides long but the table column is BOOLEAN), "
              + "'mass_flow' (the data stream provides float but the table column is BIGINT), "
              + "and 'volume_flow' (the data stream provides float but the table column is BOOLEAN)";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testValidateTable_noTypeMatch_reportsExpectedTypes() {
    Map<String, DbDataTypes> cols = Map.of(
        "timestamp", DbDataTypes.BOOLEAN,
        "sensorId", DbDataTypes.BIGINT,
        "mass_flow", DbDataTypes.BIGINT,
        "volume_flow", DbDataTypes.BOOLEAN
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected =
          "Type mismatch in table '" + TABLE_NAME + "' for columns "
              + "'timestamp' (the data stream provides long but the table column is BOOLEAN), "
              + "'sensorId' (the data stream provides string but the table column is BIGINT), "
              + "'mass_flow' (the data stream provides float but the table column is BIGINT), "
              + "and 'volume_flow' (the data stream provides float but the table column is BOOLEAN)";
      assertEquals(expected, e.getMessage());
    }
  }

  @Test
  void testValidateTable_colMissingAndTypeMismatch_reportsMissingCol() {
    Map<String, DbDataTypes> cols = Map.of(
        "timestamp", DbDataTypes.BIGINT,
        "sensorId", DbDataTypes.VAR_CHAR,
        "mass_flow", DbDataTypes.BIGINT
    );
    TableDescription table = newTable(TABLE_NAME, SENSOR_DATA_STREAM, cols);

    try {
      table.validateTable();
      fail("No exception on #validateTable");
    } catch (SpRuntimeException e) {
      String expected = "Column 'volume_flow' is missing in table '" + TABLE_NAME + "'";
      assertEquals(expected, e.getMessage());
    }
  }
}
