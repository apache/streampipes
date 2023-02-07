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

package org.apache.streampipes.client.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StreamPipesApiPathTest {
  Map<String, String> queryParameters = new HashMap();

  StreamPipesApiPath streamPipesApiPath = StreamPipesApiPath
      .fromBaseApiPath();

  String baseRoute = streamPipesApiPath.toString();

  @Test
  public void testWithEmptyQueryParameters() {

    var result = streamPipesApiPath
        .withQueryParameters(queryParameters)
        .toString();

    assertEquals(baseRoute, result);
  }

  @Test
  public void testWithOneQueryParameter() {
    queryParameters.put("one", "v1");

    var result = streamPipesApiPath
        .withQueryParameters(queryParameters)
        .toString();

    assertEquals(baseRoute + "?one=v1", result);
  }

  @Test
  public void testWithMultipleQueryParameters() {
    queryParameters.put("one", "v1");
    queryParameters.put("two", "v2");

    var result = streamPipesApiPath
        .withQueryParameters(queryParameters)
        .toString();

    assertEquals(baseRoute + "?two=v2&one=v1", result);
  }

  @Test
  public void testEncodeParameters() {
    queryParameters.put("one", "[v1]");

    var result = streamPipesApiPath
        .withQueryParameters(queryParameters)
        .toString();

    assertEquals(baseRoute + "?one=%5Bv1%5D", result);
  }

}