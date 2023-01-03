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
package org.apache.streampipes.wrapper.siddhi.query;

import java.util.StringJoiner;

public class SiddhiQuery {

  private String fromClause;
  private String selectClause;
  private String groupByClause;
  private String havingClause;
  private String orderByClause;
  private String limitClause;
  private String insertIntoClause;
  private String offsetClause;

  private boolean hasSelectClause;
  private boolean hasGroupByClause;
  private boolean hasHavingClause;
  private boolean hasOrderByClause;
  private boolean hasLimitClause;
  private boolean hasOffsetClause;

  public String getFromClause() {
    return fromClause;
  }

  public void setFromClause(String fromClause) {
    this.fromClause = fromClause;
  }

  public String getSelectClause() {
    return selectClause;
  }

  public void setSelectClause(String selectClause) {
    this.selectClause = selectClause;
    this.hasSelectClause = true;
  }

  public String getOffsetClause() {
    return offsetClause;
  }

  public void setOffsetClause(String offsetClause) {
    this.offsetClause = offsetClause;
    this.hasOffsetClause = true;
  }

  public String getGroupByClause() {
    return groupByClause;
  }

  public void setGroupByClause(String groupByClause) {
    this.groupByClause = groupByClause;
    this.hasGroupByClause = true;
  }

  public String getHavingClause() {
    return havingClause;
  }

  public void setHavingClause(String havingClause) {
    this.havingClause = havingClause;
    this.hasHavingClause = true;
  }

  public String getOrderByClause() {
    return orderByClause;
  }

  public void setOrderByClause(String orderByClause) {
    this.orderByClause = orderByClause;
    this.hasOrderByClause = true;
  }

  public String getInsertIntoClause() {
    return insertIntoClause;
  }

  public void setInsertIntoClause(String insertIntoClause) {
    this.insertIntoClause = insertIntoClause;
  }

  public String getLimitClause() {
    return limitClause;
  }

  public void setLimitClause(String limitClause) {
    this.limitClause = limitClause;
    this.hasLimitClause = true;
  }

  public String toSiddhiEpl() {
    StringJoiner joiner = new StringJoiner("\n").add(fromClause);

    if (hasSelectClause) {
      joiner.add(selectClause);
    }

    if (hasGroupByClause) {
      joiner.add(groupByClause);
    }

    if (hasHavingClause) {
      joiner.add(havingClause);
    }

    if (hasOrderByClause) {
      joiner.add(orderByClause);
    }

    if (hasLimitClause) {
      joiner.add(limitClause);
    }

    if (hasOffsetClause) {
      joiner.add(offsetClause);
    }

    joiner.add(insertIntoClause);

    return joiner + ";";
  }
}
