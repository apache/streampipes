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
package org.apache.streampipes.connect.protocol.stream;

import static org.apache.streampipes.sdk.helpers.EpProperties.stringEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

import org.apache.streampipes.connect.adapter.Adapter;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.SO;

import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketProtocol extends SpecificDataStreamAdapter {

  public static final String ID = "org.apache.streampipes.connect.protocol.stream.turtlebot";

  private WebsocketClient websocketClient;

  public WebsocketProtocol() {
  }

  public WebsocketProtocol(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);

    //getConfigurations(adapterDescription);

    //this.jsonObjectParser = new JsonObjectParser();
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID,
            "Turtlebot Map", "")
            .iconUrl("ros.png")
            .category(AdapterType.Manufacturing).build();
    description.setAppId(ID);


    return description;
  }

  @Override
  public void startAdapter() throws AdapterException {
    try {
      this.websocketClient = new WebsocketClient(adapterPipeline, new URI("ws://192.168.178" +
              ".40:9090"));
      this.websocketClient.connect();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stopAdapter() throws AdapterException {
    this.websocketClient.close();
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new WebsocketProtocol(adapterDescription);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException, ParseException {
    return GuessSchemaBuilder.create()
            .property(timestampProperty("timestamp"))
            .property(stringEp(Labels.from("data", "image", ""), "data", SO.Image))
            .build();
  }

  @Override
  public String getId() {
    return ID;
  }
}

