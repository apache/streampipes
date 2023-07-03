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

package org.apache.streampipes.sinks.brokers.jvm.rest;


import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RestSink extends StreamPipesDataSink {

  private static final Logger LOG = LoggerFactory.getLogger(RestSink.class);

  private static final String URL_KEY = "url-key";

  private String url;
  private JsonDataFormatDefinition jsonDataFormatDefinition;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.rest")
        .category(DataSinkType.FORWARD)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredTextParameter(Labels.withId(URL_KEY),
            false, false)
        .build();
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    jsonDataFormatDefinition = new JsonDataFormatDefinition();
    url = parameters.extractor().singleValueParameter(URL_KEY, String.class);
  }

  @Override
  public void onEvent(Event inputEvent) throws SpRuntimeException {
    byte[] json = null;
    try {
      json = jsonDataFormatDefinition.fromMap(inputEvent.getRaw());
    } catch (SpRuntimeException e) {
      LOG.error("Error while serializing event: " + inputEvent.getSourceInfo().getSourceId() + " Exception: "
          + e);
    }

    try {
      Request.Post(url)
          .bodyByteArray(json, ContentType.APPLICATION_JSON)
          .connectTimeout(1000)
          .socketTimeout(100000)
          .execute().returnContent().asString();
    } catch (IOException e) {
      LOG.error("Error while sending data to endpoint: " + url + " Exception: " + e);
    }
  }
}
