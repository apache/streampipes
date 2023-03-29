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

import static org.junit.Assert.assertEquals;

public class SpQueryResultBuilderTest {

  List<String> headers = List.of("h1", "h2");
  @Test
  public void withHeadersTest() {
    var result = SpQueryResultBuilder.create(headers)
        .build();

    assertEquals(2, result.getHeaders().size());
    assertEquals(headers, result.getHeaders());
  }

  @Test
  public void withSourceIndexTest() {
    var sourceIndex = 1;
    var result = SpQueryResultBuilder.create(headers)
        .withSourceIndex(sourceIndex)
        .build();

    assertEquals(sourceIndex, result.getSourceIndex());
  }

  @Test
  public void withQueryStatusDefaultTest() {
    var result = SpQueryResultBuilder.create(headers)
        .build();

    assertEquals(SpQueryStatus.OK, result.getSpQueryStatus());
  }

  @Test
  public void withQueryStatusTooMuchDataTest() {
    var result = SpQueryResultBuilder.create(headers)
        .withSpQueryStatus(SpQueryStatus.TOO_MUCH_DATA)
        .build();

    assertEquals(SpQueryStatus.TOO_MUCH_DATA, result.getSpQueryStatus());
  }

  @Test
  public void withForId() {
    var forId = "id";
    var result = SpQueryResultBuilder.create(headers)
        .withForId(forId)
        .build();

    assertEquals(forId, result.getForId());
  }

  @Test
  public void withDataSeriesTest() {
    List<Object> row = List.of("v1", 1);

    var result = SpQueryResultBuilder.create(headers)
        .withDataSeries(
            DataSeriesBuilder.create()
                .withRow(row)
                .build()
        )
        .build();

    assertEquals(1, result.getAllDataSeries().size());
    assertEquals(1, result.getAllDataSeries().get(0).getRows().size());
    assertEquals(row, result.getAllDataSeries().get(0).getRows().get(0));
  }

  @Test
  public void completeExampleTest() {

    List<String> headers = List.of("timestamp", "id", "value");
    List<List<Object>> rows = List.of(
        List.of(1234L, "one", 1.1),
        List.of(1235L, "two", 1.0)
    );

    var spQueryResult = SpQueryResultBuilder.create(headers)
        .withDataSeries(
            DataSeriesBuilder.create()
                .withRows(rows)
                .build()
        )
        .build();

    assertEquals(1, spQueryResult.getTotal());
    assertEquals(headers, spQueryResult.getAllDataSeries().get(0).getHeaders());
    assertEquals(2, spQueryResult.getAllDataSeries().get(0).getRows().size());
    assertEquals(rows, spQueryResult.getAllDataSeries().get(0).getRows());
  }
}