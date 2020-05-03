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

package org.apache.streampipes.connect.adapters.mysql;

import org.apache.streampipes.sdk.utils.Datatypes;

class Column {
  private String name;
  private Datatypes type;
  private Object def;

  Column(String name, String dataType, String columnType) {
    this.name = name;
    switch (dataType) {
      case "tinyint":
      case "smallint":
      case "mediumint":
      case "int":
      case "bit":
        this.type = Datatypes.Integer;
        def = 0;
        break;
      case "bigint":
        this.type = Datatypes.Long;
        def = 0L;
        break;
      case "float":
      case "decimal":   // Watch out for loss of precision
      case "double":
        this.type = Datatypes.Float;
        def = 0.0f;
        break;
      case "text":
      case "varchar":
      case "char":
        this.type = Datatypes.String;
        def = "";
        break;
      default:
        // date, datetime, time, timestamp, year
        throw new IllegalArgumentException("Type " + type + " not supported.");
    }
    if (columnType.equals("tinyint(1)") || columnType.equals("bit(1)")) {
      this.type = Datatypes.Boolean;
      def = Boolean.FALSE;
    }
    System.out.println("Found column: " + name + ", type: " + this.type + " (sql-type: "
        + dataType + ", column-tpye: " + columnType + ")");
  }

  public String getName() {
    return name;
  }
  public Datatypes getType() {
    return type;
  }
  public Object getDefault() {
    return def;
  }
}
