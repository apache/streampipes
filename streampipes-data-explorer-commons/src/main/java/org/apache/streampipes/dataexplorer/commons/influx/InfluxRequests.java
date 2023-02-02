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

package org.apache.streampipes.dataexplorer.commons.influx;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.List;

public class InfluxRequests {

  /**
   * Checks whether the given database exists.
   *
   * @param influxDb The InfluxDB client instance
   * @param dbName The name of the database, the method should look for
   * @return True if the database exists, false otherwise
   */
  public static boolean databaseExists(InfluxDB influxDb,
                                   String dbName) {
    QueryResult queryResult = influxDb.query(new Query("SHOW DATABASES", ""));
    for (List<Object> a : queryResult.getResults().get(0).getSeries().get(0).getValues()) {
      if (a.get(0).equals(dbName)) {
        return true;
      }
    }
    return false;
  }
}
