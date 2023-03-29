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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TsModel
public class DataSeries {

  private int total;
  private List<List<Object>> rows;
  private Map<String, String> tags;
  private List<String> headers;

  public DataSeries() {
    this.total = 0;
    this.tags = new HashMap<>();
  }

  public DataSeries(int total, List<List<Object>> rows, List<String> headers, Map<String, String> tags) {
    this.total = total;
    this.rows = rows;
    this.headers = headers;
    this.tags = tags;
  }

  public int getTotal() {
    return total;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public List<String> getHeaders() {
    return headers;
  }

  public List<List<Object>> getRows() {
    return rows;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void setRows(List<List<Object>> rows) {
    this.rows = rows;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }

  public void setHeaders(List<String> headers) {
    this.headers = headers;
  }
}
