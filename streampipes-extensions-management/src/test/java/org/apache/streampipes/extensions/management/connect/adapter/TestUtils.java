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
package org.apache.streampipes.extensions.management.connect.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Arrays;

public class TestUtils {

  public static String getJsonArrayWithThreeElements() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("key0", makeJsonArray(
        makeJsonObject("one", 1),
        makeJsonObject("one", 2),
        makeJsonObject("one", 3)));
    return jsonObject.toString();
  }

  public static JsonObject makeJsonObject(String key, Integer value) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add(key, new JsonPrimitive(value));
    return jsonObject;
  }

  public static String makeNestedJsonObject() {
    return "{\"device\": "
        + "{"
        + "\"uuid\": \"uuid\", "
        + "\"name\": null, "
        + "\"version\": \"1.2.3\"} "
        + "}";
  }

  public static JsonArray makeJsonArray(JsonObject... jsonObjects) {
    JsonArray jsonArray = new JsonArray();
    Arrays.stream(jsonObjects).forEach(jsonArray::add);
    return jsonArray;
  }
}
