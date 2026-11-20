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

package org.apache.streampipes.storage.couchdb.compat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Compares JSON documents as trees, ignoring key order but keeping numbers exactly as written ({@code 2} differs
 * from {@code 2.0}, {@code 1.10} from {@code 1.1}).
 */
final class JsonTrees {

  static final ObjectMapper MAPPER = JsonMapper.builder()
      .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
      .enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
      .configure(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES, false)
      .build();

  private static final int MAX_DIFFERENCES = 10;

  private JsonTrees() {
  }

  static JsonNode parse(String json) {
    try {
      return MAPPER.readTree(json);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static JsonNode parse(byte[] json) {
    return parse(new String(json, StandardCharsets.UTF_8));
  }

  static JsonNode without(JsonNode node, String... fields) {
    ObjectNode copy = node.deepCopy();
    copy.remove(List.of(fields));
    return copy;
  }

  /**
   * Differences as JSON pointers with both values; empty if the documents are equal.
   */
  static List<String> differences(JsonNode expected, JsonNode actual) {
    var result = new ArrayList<String>();
    compare("", expected, actual, result);
    return result;
  }

  /**
   * Number of scalar values, to check that a fixture is not trivially empty.
   */
  static int leafCount(JsonNode node) {
    if (node.isContainerNode()) {
      int count = 0;
      for (JsonNode child : node) {
        count += leafCount(child);
      }
      return count;
    }
    return 1;
  }

  private static void compare(String path, JsonNode expected, JsonNode actual, List<String> result) {
    if (result.size() >= MAX_DIFFERENCES) {
      return;
    }
    if (expected == null || actual == null || expected.getNodeType() != actual.getNodeType()) {
      result.add(path + ": " + expected + " vs " + actual);
    } else if (expected.isObject()) {
      Set<String> names = new TreeSet<>();
      expected.fieldNames().forEachRemaining(names::add);
      actual.fieldNames().forEachRemaining(names::add);
      for (String name : names) {
        compare(path + "/" + name, expected.get(name), actual.get(name), result);
      }
    } else if (expected.isArray()) {
      if (expected.size() != actual.size()) {
        result.add(path + ": " + expected.size() + " elements vs " + actual.size());
        return;
      }
      for (int i = 0; i < expected.size(); i++) {
        compare(path + "/" + i, expected.get(i), actual.get(i), result);
      }
    } else if (expected.isNumber()) {
      if (!expected.numberValue().toString().equals(actual.numberValue().toString())) {
        result.add(path + ": " + expected + " vs " + actual);
      }
    } else if (!expected.equals(actual)) {
      result.add(path + ": " + expected + " vs " + actual);
    }
  }
}
