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


import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DataSeriesBuilderTest {

  private static final List<Object> rowOne = List.of(1, "v1");
  private static final List<Object> rowTwo = List.of(2, "v2");
  @Test
  public void withHeadersTest() {
    var headers = List.of("h1", "h2");
    var result = DataSeriesBuilder.create()
        .withHeaders(headers)
        .build();

    assertEquals(2, result.getHeaders().size());
    assertEquals(headers, result.getHeaders());
  }


  @Test
  public void withRowTest() {

    var result = DataSeriesBuilder.create()
        .withRow(rowOne)
        .build();

    assertEquals(1, result.getRows().size());
    assertEquals(1, result.getTotal());
    assertEquals(List.of(rowOne), result.getRows());
  }

  @Test
  public void withTwoRowsTest() {
    var result = DataSeriesBuilder.create()
        .withRow(rowOne)
        .withRow(rowTwo)
        .build();

    assertEquals(2, result.getRows().size());
    assertEquals(2, result.getTotal());
    assertEquals(List.of(rowOne, rowTwo), result.getRows());
  }

  @Test
  public void withRowsTest() {
    var result = DataSeriesBuilder.create()
        .withRows(List.of(rowOne, rowTwo))
        .build();

    assertEquals(2, result.getRows().size());
    assertEquals(2, result.getTotal());
    assertEquals(List.of(rowOne, rowTwo), result.getRows());
  }

  @Test
  public void withTagsTest() {
    Map<String, String> tags = Map.of("t1", "v1");

    var result = DataSeriesBuilder.create()
        .withTags(tags)
        .build();

    assertEquals(tags, result.getTags());
  }
}