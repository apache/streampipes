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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class StreamPipesApiPath {

  private static final List<String> BaseApiPathV2 = Arrays.asList("streampipes-backend", "api", "v2");
  private final List<String> pathItems;
  private final Map<String, String> queryParameters;

  private StreamPipesApiPath(List<String> initialPathItems) {
    this.pathItems = initialPathItems;
    this.queryParameters = new HashMap<>();
  }

  public static StreamPipesApiPath fromStreamPipesBasePath() {
    List<String> path = new ArrayList<>(Collections.singletonList("streampipes-backend"));
    return new StreamPipesApiPath(path);
  }

  public static StreamPipesApiPath fromBaseApiPath() {
    List<String> initialPaths = new ArrayList<>(BaseApiPathV2);
    return new StreamPipesApiPath(initialPaths);
  }

  public static StreamPipesApiPath fromStreamPipesBasePath(String allSubPaths) {
    return fromStreamPipesBasePath().addToPath(allSubPaths);
  }

  public StreamPipesApiPath addToPath(String pathItem) {
    this.pathItems.add(pathItem);
    return this;
  }

  public StreamPipesApiPath withQueryParameters(Map<String, String> queryParameters) {
    this.queryParameters.putAll(queryParameters);
    return this;
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner("/");
    pathItems.forEach(joiner::add);
    return appendQueryParameters(joiner.toString());
  }

  private String appendQueryParameters(String input) {

    StringJoiner joiner = new StringJoiner("&");
    for (Map.Entry<String, String> parameter : queryParameters.entrySet()) {
      joiner.add(applyEncoding(parameter.getKey()) + "=" + applyEncoding(parameter.getValue()));
    }

    if (joiner.length() > 0) {
      return input + "?" + joiner;
    } else {
      return input;
    }
  }

  private String applyEncoding(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
