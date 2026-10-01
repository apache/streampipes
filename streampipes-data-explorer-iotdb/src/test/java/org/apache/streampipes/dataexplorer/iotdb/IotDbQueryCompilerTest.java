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

package org.apache.streampipes.dataexplorer.iotdb;

import org.apache.streampipes.dataexplorer.api.query.UnsupportedQueryException;
import org.apache.streampipes.dataexplorer.param.RestQuerySpecMapper;
import org.apache.streampipes.model.dataset.param.ProvidedRestQueryParams;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_COLUMNS;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_END_DATE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILTER;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_GROUP_BY;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_LIMIT;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_ORDER;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_PAGE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_START_DATE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_TIME_INTERVAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IotDbQueryCompilerTest {
  @Test
  void rawQueryUsesMillisecondsAndPreservesExclusiveBounds() {
    assertEquals("SELECT `value` FROM root.streampipes.`machine` "
        + "WHERE (time < 200 AND time > 100 AND `enabled` = true) ORDER BY TIME DESC LIMIT 10 OFFSET 20;",
        compile(Map.of(QP_COLUMNS, "value", QP_START_DATE, "100", QP_END_DATE, "200",
            QP_FILTER, "[enabled;=;true]", QP_ORDER, "DESC", QP_LIMIT, "10", QP_PAGE, "2")));
  }

  @Test
  void aggregatesHaveProviderSpecificNames() {
    assertEquals("SELECT AVG(`value`) AS `mean_value`,MAX_VALUE(`value`) AS `max_value` "
        + "FROM root.streampipes.`machine`;",
        compile(Map.of(QP_COLUMNS, "[value;MEAN],[value;MAX]")));
  }

  @Test
  void unsupportedFeaturesFailInsteadOfReturningEmptyResults() {
    assertThrows(UnsupportedQueryException.class, () -> compile(Map.of(QP_TIME_INTERVAL, "1h")));
    assertThrows(UnsupportedQueryException.class, () -> compile(Map.of(QP_GROUP_BY, "machine")));
    assertThrows(UnsupportedQueryException.class, () -> compile(Map.of(QP_COLUMNS, "[value;MEDIAN]")));
    assertThrows(UnsupportedQueryException.class, () -> compile(Map.of(QP_COLUMNS, "value,[value;MEAN]")));
    assertThrows(UnsupportedQueryException.class, () -> compile(Map.of(QP_FILTER, "[value;=~;abc]")));
  }

  @Test
  void identifiersCannotChangeTheQueryStructure() {
    assertEquals("root.streampipes.`device``name`", IotDbQueryCompiler.datasetPath("device`name"));
    assertEquals("SELECT `temperature.a` FROM root.streampipes.`machine`;",
        compile(Map.of(QP_COLUMNS, "temperature.a")));
  }

  private String compile(Map<String, String> values) {
    var spec = RestQuerySpecMapper.parse(
        new ProvidedRestQueryParams("machine", values));
    return new IotDbQueryCompiler().compile(spec, "machine");
  }
}
