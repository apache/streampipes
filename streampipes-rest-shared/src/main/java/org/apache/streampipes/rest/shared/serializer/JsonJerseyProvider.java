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

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


public abstract class JsonJerseyProvider implements MessageBodyWriter<Object>,
    MessageBodyReader<Object> {

  private final String utf8 = "UTF-8";


  @Override
  public boolean isReadable(Class<?> type, Type genericType,
                            Annotation[] annotations, MediaType mediaType) {
    return jsonSerialized(mediaType) && requiredAnnotationsPresent(annotations);
  }

  @Override
  public Object readFrom(Class<Object> type, Type genericType,
                         Annotation[] annotations, MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
      throws IOException, WebApplicationException {

    InputStreamReader streamReader = new InputStreamReader(entityStream, utf8);

    try {
      Type jsonType;
      if (type.equals(genericType)) {
        jsonType = type;
      } else {
        jsonType = genericType;
      }

      return deserialize(streamReader, jsonType);
    } finally {
      streamReader.close();
    }

  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType) {
    return jsonSerialized(mediaType) && requiredAnnotationsPresent(annotations);
  }

  @Override
  public long getSize(Object t, Class<?> type, Type genericType,
                      Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Object t, Class<?> type, Type genericType,
                      Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream) throws IOException,
      WebApplicationException {

    OutputStreamWriter writer = new OutputStreamWriter(entityStream, utf8);

    try {
      Type jsonType;
      if (type.equals(genericType)) {
        jsonType = type;
      } else {
        jsonType = genericType;
      }
      serialize(t, jsonType, writer);
    } finally {
      writer.close();
    }
  }

  protected boolean jsonSerialized(MediaType mediaType) {
    return mediaType.getType().equals(MediaType.APPLICATION_JSON_TYPE.getType())
        && mediaType.getSubtype().equals(MediaType.APPLICATION_JSON_TYPE.getSubtype());
  }

  protected abstract boolean requiredAnnotationsPresent(Annotation[] annotations);

  protected abstract void serialize(Object t, Type type, Writer writer) throws IOException;

  protected abstract Object deserialize(InputStreamReader reader, Type type) throws IOException;
}
