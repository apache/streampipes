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

import java.util.List;
import java.util.Map;

@TsModel
@Deprecated(forRemoval = true, since = "0.92.0")
public class PageResult extends DataSeries {

  private int page;

  private int pageSum;

  public PageResult(int total, List<String> headers, List<List<Object>> rows, int page, int pageSum,
                    Map<String, String> tags) {
    super(total, rows, headers, tags);
    this.page = page;
    this.pageSum = pageSum;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getPageSum() {
    return pageSum;
  }

  public void setPageSum(int pageSum) {
    this.pageSum = pageSum;
  }
}
