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
package org.apache.streampipes.wrapper.siddhi;

import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.GroupByClause;
import org.apache.streampipes.wrapper.siddhi.query.HavingClause;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.LimitClause;
import org.apache.streampipes.wrapper.siddhi.query.OffsetClause;
import org.apache.streampipes.wrapper.siddhi.query.OrderByClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.SiddhiQuery;

public class SiddhiQueryBuilder {

  private final SiddhiQuery siddhiQuery;

  public static SiddhiQueryBuilder create(FromClause fromClause, InsertIntoClause insertIntoClause) {
    return new SiddhiQueryBuilder(fromClause.toSiddhiEpl(), insertIntoClause.toSiddhiEpl());
  }

  public static SiddhiQueryBuilder create(String fromClause, InsertIntoClause insertIntoClause) {
    return new SiddhiQueryBuilder(fromClause, insertIntoClause.toSiddhiEpl());
  }

  public static SiddhiQueryBuilder create(String fromClause, String insertIntoClause) {
    return new SiddhiQueryBuilder(fromClause, insertIntoClause);
  }

  public static SiddhiQueryBuilder create(FromClause fromClause, String insertIntoClause) {
    return new SiddhiQueryBuilder(fromClause.toSiddhiEpl(), insertIntoClause);
  }

  private SiddhiQueryBuilder(String fromClause, String insertIntoClause) {
    this.siddhiQuery = new SiddhiQuery();
    this.siddhiQuery.setFromClause(fromClause);
    this.siddhiQuery.setInsertIntoClause(insertIntoClause);
  }

  public SiddhiQueryBuilder withSelectClause(SelectClause selectClause) {
    return withSelectClause(selectClause.toSiddhiEpl());
  }

  public SiddhiQueryBuilder withSelectClause(String selectClause) {
    this.siddhiQuery.setSelectClause(selectClause);
    return this;
  }

  public SiddhiQueryBuilder withGroupByClause(GroupByClause groupByClause) {
    return withGroupByClause(groupByClause.toSiddhiEpl());
  }

  public SiddhiQueryBuilder withGroupByClause(String groupByClause) {
    this.siddhiQuery.setGroupByClause(groupByClause);
    return this;
  }

  public SiddhiQueryBuilder withHavingClause(HavingClause havingClause) {
    return withHavingClause(havingClause.toSiddhiEpl());
  }

  public SiddhiQueryBuilder withHavingClause(String havingClause) {
    this.siddhiQuery.setHavingClause(havingClause);
    return this;
  }

  public SiddhiQueryBuilder withOrderByClause(OrderByClause orderByClause) {
    return this.withOrderByClause(orderByClause.toSiddhiEpl());
  }

  public SiddhiQueryBuilder withOrderByClause(String orderByClause) {
    this.siddhiQuery.setOrderByClause(orderByClause);
    return this;
  }

  public SiddhiQueryBuilder withLimitClause(LimitClause limitClause) {
    return this.withLimitClause(limitClause.toSiddhiEpl());
  }

  public SiddhiQueryBuilder withLimitClause(String limitClause) {
    this.siddhiQuery.setLimitClause(limitClause);
    return this;
  }

  public SiddhiQueryBuilder withOffsetClause(OffsetClause offsetClause) {
    return this.withOffsetClause(offsetClause.toSiddhiEpl());
  }

  public SiddhiQueryBuilder withOffsetClause(String offsetClause) {
    this.siddhiQuery.setOffsetClause(offsetClause);
    return this;
  }

  public SiddhiQuery build() {
    return this.siddhiQuery;
  }
}
