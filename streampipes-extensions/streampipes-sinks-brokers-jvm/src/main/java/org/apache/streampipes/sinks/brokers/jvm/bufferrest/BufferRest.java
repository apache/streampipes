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

package org.apache.streampipes.sinks.brokers.jvm.bufferrest;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.SpDataFormatDefinition;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sinks.brokers.jvm.bufferrest.buffer.BufferListener;
import org.apache.streampipes.sinks.brokers.jvm.bufferrest.buffer.MessageBuffer;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import org.apache.commons.io.Charsets;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class BufferRest implements EventSink<BufferRestParameters>, BufferListener {

  private static Logger log;
  private List<String> fieldsToSend;
  private SpDataFormatDefinition dataFormatDefinition;
  private String restEndpointURI;
  private MessageBuffer buffer;

  public BufferRest() {
    this.dataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onInvocation(BufferRestParameters parameters, EventSinkRuntimeContext runtimeContext) {
    this.fieldsToSend = parameters.getFieldsToSend();
    this.restEndpointURI = parameters.getRestEndpointURI();
    this.buffer = new MessageBuffer(parameters.getBufferSize());
    this.buffer.addListener(this);
  }

  @Override
  public void onEvent(Event event) {
    Map<String, Object> outEventMap = event.getSubset(fieldsToSend).getRaw();
    try {
      String json = new String(dataFormatDefinition.fromMap(outEventMap));
      this.buffer.addMessage(json);
    } catch (SpRuntimeException e) {
      log.error("Could not parse incoming event");
    }
  }

  @Override
  public void onDetach() {
    buffer.removeListener(this);
  }

  @Override
  public void bufferFull(String messagesJsonArray) {
    try {
      Request.Post(restEndpointURI).body(new StringEntity(messagesJsonArray, Charsets.UTF_8)).execute();
    } catch (IOException e) {
      log.error("Could not reach endpoint at {}", restEndpointURI);
    }
  }
}
