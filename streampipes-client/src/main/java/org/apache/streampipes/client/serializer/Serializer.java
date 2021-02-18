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
package org.apache.streampipes.client.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.serializers.json.JacksonSerializer;

public abstract class Serializer<SO, DSO, DT> {

  protected ObjectMapper objectMapper;

  public Serializer() {
    this.objectMapper = JacksonSerializer.getObjectMapper();
  }

  public String serialize(SO object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException(e.getCause());
    }
  }

  public abstract DT deserialize(String response, Class<DSO> targetClass);
}
