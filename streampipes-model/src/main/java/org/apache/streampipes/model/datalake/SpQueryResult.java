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

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class SpQueryResult {

  private int total;
  private List<String> headers;
  private List<DataSeries> allDataSeries;
  private int sourceIndex;
  private SpQueryStatus spQueryStatus;
  private String forId;

  public SpQueryResult() {
    this.total = 0;
    this.allDataSeries = new ArrayList<>();
    this.spQueryStatus = SpQueryStatus.OK;
  }

  public SpQueryResult(int total, List<String> headers, List<DataSeries> allDataSeries) {
    this.total = total;
    this.allDataSeries = allDataSeries;
    this.headers = headers;
    this.spQueryStatus = SpQueryStatus.OK;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void addDataResult(DataSeries dataSeries) {
    if (this.allDataSeries == null) {
      this.allDataSeries = new ArrayList<>();
    }

    this.allDataSeries.add(dataSeries);
  }

  public List<DataSeries> getAllDataSeries() {
    return allDataSeries;
  }

  public void setAllDataSeries(List<DataSeries> allDataSeries) {
    this.allDataSeries = allDataSeries;
  }

  public List<String> getHeaders() {
    return headers;
  }

  public void setHeaders(List<String> headers) {
    this.headers = headers;
  }

  public int getSourceIndex() {
    return sourceIndex;
  }

  public void setSourceIndex(int sourceIndex) {
    this.sourceIndex = sourceIndex;
  }

  public SpQueryStatus getSpQueryStatus() {
    return spQueryStatus;
  }

  public void setSpQueryStatus(SpQueryStatus spQueryStatus) {
    this.spQueryStatus = spQueryStatus;
  }

  public String getForId() {
    return forId;
  }

  public void setForId(String forId) {
    this.forId = forId;
  }
}
