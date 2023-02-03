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
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.XSD;

public class DbDataTypeFactory {


  public static DbDataTypes getInteger(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
        return DbDataTypes.INT;
      case POSTGRESQL:
        return DbDataTypes.INTEGER;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support integer values.");
  }


  public static DbDataTypes getLong(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
      case POSTGRESQL:
        return DbDataTypes.BIGINT;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support long values.");
  }


  public static DbDataTypes getFloat(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
        return DbDataTypes.FLOAT;
      case POSTGRESQL:
        return DbDataTypes.REAL;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support float values.");
  }

  public static DbDataTypes getDouble(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
      case POSTGRESQL:
        return DbDataTypes.DOUBLE_PRECISION;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support double values.");
  }

  public static DbDataTypes getShortString(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
      case POSTGRESQL:
        return DbDataTypes.VAR_CHAR;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support short strings.");
  }

  public static DbDataTypes getLongString(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
      case POSTGRESQL:
        return DbDataTypes.TEXT;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support long strings.");
  }

  public static DbDataTypes getBoolean(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
      case POSTGRESQL:
        return DbDataTypes.BOOLEAN;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support boolean values.");
  }

  public static DbDataTypes getTimestamp(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
      case POSTGRESQL:
        return DbDataTypes.TIMESTAMP;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support timestamps.");
  }

  public static DbDataTypes getDate(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
      case POSTGRESQL:
        return DbDataTypes.DATE;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support dates.");
  }

  public static DbDataTypes getTime(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
      case POSTGRESQL:
        return DbDataTypes.TIME;
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support time.");
  }

  public static DbDataTypes getDatetime(SupportedDbEngines sqlEngine) throws SpRuntimeException {
    switch (sqlEngine) {
      case MY_SQL:
        return DbDataTypes.DATETIME;
      case POSTGRESQL:
    }
    throw new SpRuntimeException("Database engine " + sqlEngine + " does not support datetime.");
  }


  /**
   * Tries to identify the data type of the object {@code o}. In case it is not supported, it is
   * interpreted as a String (VARCHAR(255))
   *
   * @param o The object which should be identified
   * @return
   */
  public static DbDataTypes getFromObject(final Object o, SupportedDbEngines sqlEngine) {

    if (o instanceof Integer) {
      return getInteger(sqlEngine);
    } else if (o instanceof Long) {
      return getLong(sqlEngine);
    } else if (o instanceof Float) {
      return getFloat(sqlEngine);
    } else if (o instanceof Double) {
      return getDouble(sqlEngine);
    } else if (o instanceof Boolean) {
      return getBoolean(sqlEngine);
    } else {
      return getLongString(sqlEngine);
    }

  }

  public static DbDataTypes getFromUri(final String uri, SupportedDbEngines sqlEngine) {
    if (uri.equals(XSD.INTEGER.toString())) {
      return getInteger(sqlEngine);
    } else if (uri.equals(XSD.LONG.toString())) {
      return getLong(sqlEngine);
    } else if (uri.equals(XSD.FLOAT.toString())) {
      return getFloat(sqlEngine);
    } else if (uri.equals(XSD.DOUBLE.toString())) {
      return getDouble(sqlEngine);
    } else if (uri.equals(XSD.BOOLEAN.toString())) {
      return getBoolean(sqlEngine);
    } else {
      return getLongString(sqlEngine);
    }
  }

  public static Datatypes getDataType(DbDataTypes dbDataType) throws SpRuntimeException {

    switch (dbDataType) {
      case BOOLEAN:
        return Datatypes.Boolean;
      case TEXT:
      case VAR_CHAR:
      case TIMESTAMP:
      case DATE:
      case TIME:
        return Datatypes.String;
      case DOUBLE_PRECISION:
        return Datatypes.Double;
      case FLOAT:
      case REAL:
      case DATETIME:
        return Datatypes.Float;
      case BIGINT:
      case TINYINT:
        return Datatypes.Long;
      case INT:
      case INTEGER:
        return Datatypes.Integer;
      default:
        throw new SpRuntimeException("Unknown SQL datatype");
    }

  }
}
