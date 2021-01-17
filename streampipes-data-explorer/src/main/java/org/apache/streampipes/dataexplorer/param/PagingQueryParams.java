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

import javax.annotation.Nullable;

public class PagingQueryParams extends QueryParams {

  private final Integer itemsPerPage;
  private Integer page;

  private long startDate;
  private long endDate;

  private boolean filterByDate;

  public static PagingQueryParams from(String index, Integer itemsPerPage) {
    return new PagingQueryParams(index, itemsPerPage);
  }

  public static PagingQueryParams from(String index, Integer itemsPerPage, Integer page) {
    return new PagingQueryParams(index, itemsPerPage, page);
  }

  public static PagingQueryParams from(String index,
                                       Integer itemsPerPage,
                                       Integer page,
                                       @Nullable Long startDate,
                                       @Nullable Long endDate) {
    return new PagingQueryParams(index, itemsPerPage, page, startDate, endDate);
  }

  protected PagingQueryParams(String index, Integer itemsPerPage) {
    super(index);
    this.itemsPerPage = itemsPerPage;
    this.filterByDate = false;
  }

  protected PagingQueryParams(String index, Integer itemsPerPage, Integer page) {
    super(index);
    this.itemsPerPage = itemsPerPage;
    this.page = page;
    this.filterByDate = false;
  }

  protected PagingQueryParams(String index,
                              Integer itemsPerPage,
                              Integer page,
                              @Nullable Long startDate,
                              @Nullable Long endDate) {
    this(index, itemsPerPage, page);
    if (startDate == null || endDate == null) {
      this.filterByDate = false;
    } else {
      this.startDate = startDate;
      this.endDate = endDate;
      this.filterByDate = true;
    }
  }

  public Integer getItemsPerPage() {
    return itemsPerPage;
  }

  public Integer getPage() {
    return page;
  }

  public long getStartDate() {
    return startDate;
  }

  public long getEndDate() {
    return endDate;
  }

  public boolean isFilterByDate() {
    return filterByDate;
  }
}
