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


package org.apache.streampipes.dataexplorer.v4.query.writer.item;

import com.google.gson.Gson;

public class JsonItemWriter extends ItemGenerator {

  private static final String BEGIN_OBJECT = "{";
  private static final String END_OBJECT = "}";

  private final Gson gson;

  public JsonItemWriter(Gson gson) {
    super(COMMA_SEPARATOR);
    this.gson = gson;
  }

  @Override
  protected String makeItemString(String key,
                                  Object value) {
    var stringValue = value != null ? gson.toJson(value) : null;
    return "\""
        + key
        + "\": "
        + stringValue;
  }

  @Override
  protected String finalizeItem(String item) {
    return BEGIN_OBJECT + item + END_OBJECT;
  }
}
