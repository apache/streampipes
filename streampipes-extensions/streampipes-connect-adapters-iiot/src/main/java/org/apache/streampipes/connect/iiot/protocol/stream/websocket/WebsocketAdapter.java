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

package org.apache.streampipes.connect.iiot.protocol.stream.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.connect.api.IAdapter;
import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.api.exception.ParseException;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebsocketAdapter extends SpecificDataStreamAdapter {

  public static final Logger LOG = LoggerFactory.getLogger(WebsocketAdapter.class);
  public static final String BROKER_URL = "wss://cubebroker.dashboard.inocube.inovex.io/ws?subscribe_to=";

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.websocket";

  public static final String DEVICE_ID = "device-id";
  public static final String AUTH_KEY = "auth-key";
  public static final String MESSAGE_TYPE = "message-type";
  public static final String TEMPERATURE = "temperature-alternative";
  public static final String EULER = "euler-alternative";


  private String authToken;
  private String deviceId;
  private String messageType;

  private SpWebSocketClient webSocketClient;


  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
  };

  public WebsocketAdapter() {
  }

  public WebsocketAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {
    return SpecificDataStreamAdapterBuilder.create(ID)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(AdapterType.Generic, AdapterType.Manufacturing)
        .requiredTextParameter(Labels.withId(DEVICE_ID))
        .requiredSecret(Labels.withId(AUTH_KEY))
        .requiredAlternatives(Labels.withId(MESSAGE_TYPE),
            Alternatives.from(Labels.withId(TEMPERATURE)),
            Alternatives.from(Labels.withId(EULER))).build();
  }


  private void getConfigurations(SpecificAdapterStreamDescription adapterDescription) {
    StaticPropertyExtractor extractor = StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());
    this.deviceId = extractor.singleValueParameter(DEVICE_ID, String.class);
    this.authToken = extractor.secretValue(AUTH_KEY);
    this.messageType = extractor.selectedAlternativeInternalId(MESSAGE_TYPE);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription)
      throws AdapterException, ParseException {

    this.getConfigurations(adapterDescription);

    if (this.messageType.equals(TEMPERATURE)) {
      return WebsocketSchemaUtils.getTemperatureSchema();
    } else {
      return WebsocketSchemaUtils.getEulerSchema();
    }
  }


  @Override
  public void startAdapter() {

    URI connectionUri = URI.create(BROKER_URL + this.deviceId);
    HashMap<String, String> httpHeader = new HashMap<>();
    httpHeader.put("Authorization", "Bearer " + this.authToken);

    this.webSocketClient =
        new SpWebSocketClient(
            connectionUri,
            httpHeader,
            this);

    this.webSocketClient.connect();

  }

  @Override
  public void stopAdapter() {
    this.webSocketClient.close();
  }

  public void onEvent(String eventPayload) {

    if (eventPayload.contains(this.messageType)) {
      try {
        Map<String, Object> event = objectMapper.readValue(eventPayload, typeRef);

        if (this.messageType.equals(TEMPERATURE)) {
          event = WebsocketSchemaUtils.prepareTemperatureEvent(event);
        } else if (this.messageType.equals(EULER)) {
          event = WebsocketSchemaUtils.prepareEulerEvent(event);
        }

        adapterPipeline.process(event);
      } catch (JsonProcessingException e) {
        LOG.error("Event e: {} of websocket adapter could not be parsed.", eventPayload, e);
      }
    }
  }

  @Override
  public IAdapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new WebsocketAdapter();
  }

  @Override
  public String getId() {
    return WebsocketAdapter.ID;
  }
}
