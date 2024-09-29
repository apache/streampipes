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
package org.apache.streampipes.dataexplorer.export.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonItemGenerator extends ItemGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(JsonItemGenerator.class);

  private static final String BEGIN_OBJECT = "{";
  private static final String END_OBJECT = "}";

  private final ObjectMapper objectMapper;

  public JsonItemGenerator(ObjectMapper objectMapper) {
    super(COMMA_SEPARATOR);
    this.objectMapper = objectMapper;
  }

  @Override
  protected String makeItemString(String key, Object value) {
    String valueJsonString = null;
    try {
      valueJsonString = value != null ? this.objectMapper.writeValueAsString(value) : null;
    } catch (JsonProcessingException e) {
      LOG.error("Error while converting value to JSON string: {}", e.getMessage());
    }
    return "\"%s\": %s".formatted(key, valueJsonString);
  }

  @Override
  protected String finalizeItem(String item) {
    return BEGIN_OBJECT + item + END_OBJECT;
  }
}
