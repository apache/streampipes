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
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;

public class RestPublisher implements EventSink<RestParameters> {
  private static Logger logger;

  private String url;
  private JsonDataFormatDefinition jsonDataFormatDefinition;

  @Override
  public void onInvocation(RestParameters params, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    this.url = params.getUrl();
    logger = params.getGraph().getLogger(RestPublisher.class);
    jsonDataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onEvent(Event inputEvent) {

    byte[] json = null;
    try {
      json = jsonDataFormatDefinition.fromMap(inputEvent.getRaw());
    } catch (SpRuntimeException e) {
      logger.error("Error while serializing event: " + inputEvent.getSourceInfo().getSourceId() + " Exception: "
          + e);
    }

    try {
      Request.Post(url)
          .bodyByteArray(json, ContentType.APPLICATION_JSON)
          .connectTimeout(1000)
          .socketTimeout(100000)
          .execute().returnContent().asString();
    } catch (IOException e) {
      logger.error("Error while sending data to endpoint: " + url + " Exception: " + e);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
  }
}
