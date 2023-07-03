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
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sinks.brokers.jvm.bufferrest.buffer.BufferListener;
import org.apache.streampipes.sinks.brokers.jvm.bufferrest.buffer.MessageBuffer;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import org.apache.commons.io.Charsets;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BufferRestPublisherSink extends StreamPipesDataSink implements BufferListener {

  private static final Logger LOG = LoggerFactory.getLogger(BufferRestPublisherSink.class);

  private static final String KEY = "bufferrest";
  private static final String URI = ".uri";
  private static final String COUNT = ".count";
  private static final String FIELDS = ".fields-to-send";

  private List<String> fieldsToSend;
  private SpDataFormatDefinition dataFormatDefinition;
  private String restEndpointURI;
  private MessageBuffer buffer;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.bufferrest")
        .category(DataSinkType.NOTIFICATION)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithNaryMapping(EpRequirements.anyProperty(), Labels.withId(
                KEY + FIELDS), PropertyScope.NONE)
            .build())
        .requiredTextParameter(Labels.from(KEY + URI, "REST Endpoint URI", "REST Endpoint URI"))
        .requiredIntegerParameter(Labels.from(KEY + COUNT, "Buffered Event Count",
                "Number (1 <= x <= 1000000) of incoming events before sending data on to the given REST endpoint"),
            1, 1000000, 1)
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

    var extractor = parameters.extractor();
    fieldsToSend = extractor.mappingPropertyValues(KEY + FIELDS);
    restEndpointURI = extractor.singleValueParameter(KEY + URI, String.class);
    int bufferSize = Integer.parseInt(extractor.singleValueParameter(KEY + COUNT, String.class));
    this.dataFormatDefinition = new JsonDataFormatDefinition();

    this.buffer = new MessageBuffer(bufferSize);
    this.buffer.addListener(this);
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    Map<String, Object> outEventMap = event.getSubset(fieldsToSend).getRaw();
    try {
      String json = new String(dataFormatDefinition.fromMap(outEventMap));
      this.buffer.addMessage(json);
    } catch (SpRuntimeException e) {
      LOG.error("Could not parse incoming event");
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    buffer.removeListener(this);
  }

  @Override
  public void bufferFull(String messagesJsonArray) {
    try {
      Request.Post(restEndpointURI).body(new StringEntity(messagesJsonArray, Charsets.UTF_8)).execute();
    } catch (IOException e) {
      LOG.error("Could not reach endpoint at {}", restEndpointURI);
    }
  }
}
