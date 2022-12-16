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
package org.apache.streampipes.dataexplorer.template;

public class QueryTemplates {

  public static String selectWildcardFrom(String index) {
    return "SELECT * FROM " + index;
  }

  public static String selectMeanFrom(String index) {
    return "SELECT mean(*) FROM " + index;
  }

  public static String selectCountFrom(String index) {
    return "SELECT count(*) FROM " + index;
  }

  public static String whereTimeWithin(long startDate, long endDate) {
    return "WHERE time > "
        + startDate * 1000000
        + " AND time < "
        + endDate * 1000000;
  }
}
