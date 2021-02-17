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
package org.apache.streampipes.dataexplorer.param;

public class GroupedQueryParams extends TimeBoundQueryParams {

  private final String groupingTag;

  public static GroupedQueryParams from(String index,
                                        long startDate,
                                        long endDate,
                                        String groupingTag) {
    return new GroupedQueryParams(index, startDate, endDate, groupingTag);
  }

  protected GroupedQueryParams(String index,
                               long startDate,
                               long endDate,
                               String groupingTag) {
    super(index, startDate, endDate);
    this.groupingTag = groupingTag;
  }

  public String getGroupingTag() {
    return groupingTag;
  }
}
