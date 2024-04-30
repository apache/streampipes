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

import org.apache.streampipes.dataexplorer.api.IDataLakeQueryBuilder;
import org.apache.streampipes.dataexplorer.param.model.FillClauseParams;
import org.apache.streampipes.dataexplorer.param.model.GroupByTagsClauseParams;
import org.apache.streampipes.dataexplorer.param.model.GroupByTimeClauseParams;
import org.apache.streampipes.dataexplorer.param.model.LimitClauseParams;
import org.apache.streampipes.dataexplorer.param.model.OffsetClauseParams;
import org.apache.streampipes.dataexplorer.param.model.OrderByClauseParams;
import org.apache.streampipes.dataexplorer.param.model.SelectClauseParams;
import org.apache.streampipes.dataexplorer.param.model.WhereClauseParams;

import java.util.Objects;

public class SelectQueryParams {

  private SelectClauseParams selectParams;

  private WhereClauseParams whereParams;

  private GroupByTagsClauseParams groupByTagsClauseParams;
  private GroupByTimeClauseParams groupByTimeClauseParams;

  private OrderByClauseParams orderByClauseParams;
  private LimitClauseParams limitParams;

  private OffsetClauseParams offsetClauseParams;

  private FillClauseParams fillClauseParams;

  private final String index;

  public SelectQueryParams(String index) {
    this.index = index;
  }

  public String getIndex() {
    return index;
  }

  public void withSelectParams(SelectClauseParams params) {
    this.selectParams = params;
  }

  public void withLimitParams(LimitClauseParams params) {
    this.limitParams = params;
  }

  public void withGroupByTagsParams(GroupByTagsClauseParams params) {
    this.groupByTagsClauseParams = params;
  }

  public void withGroupByTimeParams(GroupByTimeClauseParams params) {
    this.groupByTimeClauseParams = params;
  }

  public void withFillParams(FillClauseParams params) {
    this.fillClauseParams = params;
  }

  public void withWhereParams(WhereClauseParams params) {
    this.whereParams = params;
  }

  public void withOffsetParams(OffsetClauseParams params) {
    this.offsetClauseParams = params;
  }

  public void withOrderByParams(OrderByClauseParams params) {
    this.orderByClauseParams = params;
  }

  public <T> T toQuery(IDataLakeQueryBuilder<T> builder) {
    this.selectParams.buildStatement(builder);
    prepareBuilder(builder);
    return builder.build();
  }

  public int getLimit() {
    if (Objects.nonNull(limitParams)) {
      return limitParams.limit();
    } else {
      return Integer.MIN_VALUE;
    }
  }

  private <T> void prepareBuilder(IDataLakeQueryBuilder<T> builder) {
    if (Objects.nonNull(this.whereParams)) {
      this.whereParams.buildStatement(builder);
    }

    if (Objects.nonNull(this.groupByTimeClauseParams)) {
      this.groupByTimeClauseParams.buildStatement(builder);
    }

    if (Objects.nonNull(this.groupByTagsClauseParams)) {
      this.groupByTagsClauseParams.buildStatement(builder);
    }

    if (Objects.nonNull(this.orderByClauseParams)) {
      this.orderByClauseParams.buildStatement(builder);
    }

    if (Objects.nonNull(this.limitParams)) {
      this.limitParams.buildStatement(builder);
    }

    if (Objects.nonNull(this.offsetClauseParams)) {
      this.offsetClauseParams.buildStatement(builder);
    }

    if (Objects.nonNull(this.fillClauseParams)) {
      this.fillClauseParams.buildStatement(builder);
    }
  }

}
