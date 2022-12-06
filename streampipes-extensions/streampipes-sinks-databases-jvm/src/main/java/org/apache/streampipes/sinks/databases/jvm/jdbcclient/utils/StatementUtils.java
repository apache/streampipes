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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDataTypeFactory;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.ParameterInformation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementUtils {

  /**
   * Sets the value in the prepardStatement {@code ps}
   *
   * @param p     The needed info about the parameter (index and type)
   * @param value The value of the object, which should be filled in the
   * @param ps    The prepared statement, which will be filled
   * @throws SpRuntimeException When the data type in {@code p} is unknown
   * @throws SQLException       When the setters of the statement throw an
   *                            exception (e.g. {@code setInt()})
   */
  public static void setValue(ParameterInformation p, Object value, PreparedStatement ps)
      throws SQLException, SpRuntimeException {
    switch (DbDataTypeFactory.getDataType(p.getDataType())) {
      case Integer:
        ps.setInt(p.getIndex(), (Integer) value);
        break;
      case Long:
        ps.setLong(p.getIndex(), (Long) value);
        break;
      case Float:
        ps.setFloat(p.getIndex(), (Float) value);
        break;
      case Double:
        ps.setDouble(p.getIndex(), (Double) value);
        break;
      case Boolean:
        ps.setBoolean(p.getIndex(), (Boolean) value);
        break;
      case String:
        ps.setString(p.getIndex(), value.toString());
        break;
      case Number:
      case Sequence:
      default:
        throw new SpRuntimeException("Unknown SQL datatype");
    }
  }
}
