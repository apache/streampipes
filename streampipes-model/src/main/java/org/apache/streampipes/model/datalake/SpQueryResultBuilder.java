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

public class SpQueryResultBuilder {

  private final SpQueryResult spQueryResult;
  private final List<DataSeries> allDataSeries;

  private SpQueryResultBuilder(List<String> headers) {
    spQueryResult = new SpQueryResult();
    allDataSeries = new ArrayList<>();
    spQueryResult.setHeaders(headers);
  }

  public static SpQueryResultBuilder create(List<String> headers) {
    return new SpQueryResultBuilder(headers);
  }

  public SpQueryResult build() {
    // set the header of all data series
    allDataSeries
        .forEach(series -> series.setHeaders(spQueryResult.getHeaders()));
    spQueryResult.setAllDataSeries(allDataSeries);
    spQueryResult.setTotal(allDataSeries.size());
    return spQueryResult;
  }

  public SpQueryResultBuilder withDataSeries(List<DataSeries> allDataSeries) {
    this.allDataSeries.addAll(allDataSeries);
    return this;
  }

  public SpQueryResultBuilder withDataSeries(DataSeries dataSeries) {
    this.allDataSeries.add(dataSeries);
    return this;
  }

  public SpQueryResultBuilder withSourceIndex(int sourceIndex) {
    spQueryResult.setSourceIndex(sourceIndex);
    return this;
  }

  public SpQueryResultBuilder withSpQueryStatus(SpQueryStatus spQueryStatus) {
    spQueryResult.setSpQueryStatus(spQueryStatus);
    return this;
  }

  public SpQueryResultBuilder withForId(String forId) {
    spQueryResult.setForId(forId);
    return this;
  }

}
