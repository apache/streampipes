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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;

final class MsSqlValueConverter {

  private MsSqlValueConverter() {
  }

  static Object convert(Object value, MsSqlColumn column) throws SQLException {
    if (value == null) {
      return null;
    }

    try {
      return switch (column.jdbcType()) {
        case Types.DECIMAL, Types.NUMERIC -> new BigDecimal(value.toString());
        case Types.BIGINT -> new BigDecimal(value.toString()).longValueExact();
        case Types.INTEGER -> new BigDecimal(value.toString()).intValueExact();
        case Types.SMALLINT -> new BigDecimal(value.toString()).shortValueExact();
        case Types.TINYINT -> new BigDecimal(value.toString()).intValueExact();
        case Types.DOUBLE, Types.FLOAT, Types.REAL -> Double.parseDouble(value.toString());
        case Types.BIT, Types.BOOLEAN -> Boolean.parseBoolean(value.toString());
        default -> convertScalar(value, column);
      };
    } catch (NumberFormatException | ArithmeticException e) {
      throw new SQLException(
          "Could not convert SQL Server column " + column.name() + " with JDBC type " + column.typeName()
              + " to a primitive value: " + value,
          e
      );
    }
  }

  private static Object convertScalar(Object value, MsSqlColumn column) throws SQLException {
    if (value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character) {
      return value;
    }
    throw new SQLException(
        "Could not convert SQL Server column " + column.name() + " with JDBC type " + column.typeName()
            + " to a primitive value. JDBC returned " + value.getClass().getName() + "."
    );
  }
}
