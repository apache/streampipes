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

import org.apache.streampipes.serializers.json.JacksonSerializer;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MsSqlValueConverterTest {

  @Test
  void normalizesDriverSpecificDecimalValuesToJsonNumbers() throws Exception {
    MsSqlColumn column = new MsSqlColumn("MENGE", Types.DECIMAL, "decimal", 18, 3, false);

    Object converted = MsSqlValueConverter.convert(new NumericValue("108.000"), column);

    assertEquals(new BigDecimal("108.000"), converted);
    var jsonValue = JacksonSerializer.getObjectMapper()
        .readTree(JacksonSerializer.getObjectMapper().writeValueAsString(Map.of("MENGE", converted)))
        .get("MENGE");
    assertTrue(jsonValue.isNumber());
    assertEquals(0, new BigDecimal("108.000").compareTo(jsonValue.decimalValue()));
  }

  @Test
  void rejectsUnsupportedJdbcObjectsWithColumnAndJavaType() {
    MsSqlColumn column = new MsSqlColumn("MENGE", Types.OTHER, "sql_variant", 0, 0, false);

    var exception = assertThrows(SQLException.class, () -> MsSqlValueConverter.convert(new Object(), column));

    assertTrue(exception.getMessage().contains("MENGE"));
    assertTrue(exception.getMessage().contains(Object.class.getName()));
  }

  private record NumericValue(String value) {
    @Override
    public String toString() {
      return value;
    }
  }
}
