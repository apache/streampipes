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

import org.apache.streampipes.dataexplorer.api.query.QuerySpec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestProjectionParserTest {

  @Test
  public void acceptsPlainColumns() {
    List<QuerySpec.Projection> projections = RestProjectionParser.parse("value,temperature.a,sensor_id", null);
    assertEquals(3, projections.size());
    assertEquals("value", projections.get(0).field());
    assertEquals("temperature.a", projections.get(1).field());
    assertEquals("sensor_id", projections.get(2).field());
  }

  @Test
  public void acceptsWildcard() {
    assertDoesNotThrow(() -> RestProjectionParser.parse("*", null));
    assertDoesNotThrow(() -> RestProjectionParser.parse(null, null));
  }

  @Test
  public void acceptsAggregatedColumnWithExplicitAlias() {
    List<QuerySpec.Projection> projections = RestProjectionParser.parse("value;MEAN;mean_value", null);
    assertEquals(1, projections.size());
    assertEquals("value", projections.get(0).field());
    assertEquals("mean_value", projections.get(0).alias().orElseThrow());
  }

  @Test
  public void rejectsColumnThatBreaksOutOfTheIdentifier() {
    assertThrows(IllegalArgumentException.class,
        () -> RestProjectionParser.parse("salary\" FROM \"other\" --", null));
  }

  @Test
  public void rejectsColumnWithStatementSeparator() {
    assertThrows(IllegalArgumentException.class,
        () -> RestProjectionParser.parse("value) FROM \"other\"", null));
  }

  @Test
  public void rejectsExplicitAliasThatBreaksOutOfTheIdentifier() {
    assertThrows(IllegalArgumentException.class,
        () -> RestProjectionParser.parse("value;MEAN;alias\" FROM \"other\" --", null));
  }
}
