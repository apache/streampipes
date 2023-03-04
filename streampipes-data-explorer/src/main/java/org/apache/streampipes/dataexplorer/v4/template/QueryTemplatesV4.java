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
package org.apache.streampipes.dataexplorer.v4.template;

public class QueryTemplatesV4 {

  public static String deleteFrom(String index) {
    return "DELETE FROM \"" + index + "\"";
  }

  public static String whereTimeWithin(long startDate, long endDate) {
    return "WHERE time > "
        + startDate * 1000000
        + " AND time < "
        + endDate * 1000000;
  }

  public static String whereTimeLeftBound(long startDate) {
    return "WHERE time > "
        + startDate * 1000000;
  }

  public static String whereTimeRightBound(long endDate) {
    return "WHERE time < "
        + endDate * 1000000;
  }

  public static String groupByTags(String tags) {
    return "GROUP BY " + tags;
  }

  public static String groupByTime(String timeInterval) {
    return "GROUP BY time(" + timeInterval + ")";
  }

  public static String orderByTime(String ordering) {
    return "ORDER BY time " + ordering.toUpperCase();
  }

  public static String limitItems(int limit) {
    return "LIMIT " + limit;
  }

  public static String offset(int offset) {
    return "OFFSET " + offset;
  }

}
