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

package org.apache.streampipes.model.datalake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSeriesBuilder {
  private final DataSeries dataSeries;
  private final List<List<Object>> rows;

  private DataSeriesBuilder() {
    this.dataSeries = new DataSeries();
    this.rows = new ArrayList<>();
  }

  public static DataSeriesBuilder create() {
    return new DataSeriesBuilder();
  }

  public DataSeries build() {
    dataSeries.setRows(rows);
    dataSeries.setTotal(rows.size());
    return dataSeries;
  }

  public DataSeriesBuilder withRow(List<Object> row) {
    this.rows.add(row);
    return this;
  }

  public DataSeriesBuilder withRows(List<List<Object>> rows) {
    this.rows.addAll(rows);
    return this;
  }

  public DataSeriesBuilder withHeaders(List<String> headers) {
    dataSeries.setHeaders(headers);
    return this;
  }

  public DataSeriesBuilder withTags(Map<String, String> tags) {
    dataSeries.setTags(tags);
    return this;
  }
}
