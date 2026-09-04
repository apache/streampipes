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

import org.apache.streampipes.dataexplorer.param.model.WhereClauseParams;
import org.apache.streampipes.model.datalake.FilterCondition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhereStatementParamsTest {
  @Test
  public void filterNumber() {
    WhereClauseParams result = WhereClauseParams.from("[fieldName;=;6]");
    FilterCondition expected = new FilterCondition("fieldName", "=", 6.0);

    assertWhereCondition(result, expected);
  }

  @Test
  public void filterBoolean() {
    WhereClauseParams result = WhereClauseParams.from("[fieldName;=;true]");
    FilterCondition expected = new FilterCondition("fieldName", "=", true);

    assertWhereCondition(result, expected);
  }

  @Test
  public void filterString() {
    WhereClauseParams result = WhereClauseParams.from("[fieldName;=;a]");
    FilterCondition expected = new FilterCondition("fieldName", "=", "a");

    assertWhereCondition(result, expected);
  }

  @Test
  public void filterString2() {
    WhereClauseParams result = WhereClauseParams.from("[fieldName;=;3312476503F]");
    FilterCondition expected = new FilterCondition("fieldName", "=", "3312476503F");

    assertWhereCondition(result, expected);
  }

  private void assertWhereCondition(WhereClauseParams result, FilterCondition expected) {
    assertEquals(1, result.getWhereConditions().size());
    FilterCondition resultingFilterCondition = result.getWhereConditions().get(0);
    assertEquals(expected.field(), resultingFilterCondition.field());
    assertEquals(expected.operator(), resultingFilterCondition.operator());
    assertEquals(expected.condition(), resultingFilterCondition.condition());
  }


}
