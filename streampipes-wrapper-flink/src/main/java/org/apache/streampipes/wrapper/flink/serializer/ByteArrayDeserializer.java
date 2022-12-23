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
package org.apache.streampipes.wrapper.flink.serializer;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;

import java.io.IOException;
import java.util.Map;

public class ByteArrayDeserializer extends AbstractDeserializationSchema<Map<String, Object>> {

  private SpDataFormatDefinition spDataFormatDefinition;

  public ByteArrayDeserializer(SpDataFormatDefinition spDataFormatDefinition) {
    this.spDataFormatDefinition = spDataFormatDefinition;
  }

  @Override
  public Map<String, Object> deserialize(byte[] bytes) throws IOException {
    try {
      return spDataFormatDefinition.toMap(bytes);
    } catch (SpRuntimeException e) {
      throw new IOException(e);
    }
  }
}

