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
package org.apache.streampipes.sinks.brokers.jvm.websocket;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

public class WebsocketServerSink implements IStreamPipesDataSink {

  private static final String PORT_KEY = "port";

  private SocketServer server;

  @Override
  public IDataSinkConfiguration declareConfig() {
    return DataSinkConfiguration.create(WebsocketServerSink::new, DataSinkBuilder
            .create("org.apache.streampipes.sinks.brokers.jvm.websocket", 0).category(DataSinkType.MESSAGING)
            .withLocales(Locales.EN).withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .requiredStream(StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
            .requiredIntegerParameter(Labels.withId(PORT_KEY)).build());
  }

  @Override
  public void onPipelineStarted(IDataSinkParameters params, EventSinkRuntimeContext runtimeContext) {
    var port = params.extractor().singleValueParameter(PORT_KEY, Integer.class);
    server = new SocketServer(port);
    server.setReuseAddr(true);
    server.start();
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    server.onEvent(event);
  }

  @Override
  public void onPipelineStopped() {
    try {
      server.stop(0);
    } catch (InterruptedException e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }
}
