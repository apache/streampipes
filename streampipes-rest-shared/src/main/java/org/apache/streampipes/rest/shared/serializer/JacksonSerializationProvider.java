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
package org.apache.streampipes.rest.shared.serializer;

import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonSerializationProvider extends JsonJerseyProvider {

  private static final ObjectMapper mapper = JacksonSerializer.getObjectMapper();

  public JacksonSerializationProvider() {
    super();
  }


  @Override
  protected boolean requiredAnnotationsPresent(Annotation[] annotations) {
    return Arrays.stream(annotations).anyMatch(a -> a.annotationType().equals(JacksonSerialized.class));
  }

  @Override
  protected void serialize(Object t, Type type, Writer writer) throws IOException {
    mapper.writeValue(writer, t);
  }

  @Override
  protected Object deserialize(InputStreamReader reader, Type type) throws IOException {
    return mapper.readValue(reader, TypeFactory.rawClass(type));
  }
}
