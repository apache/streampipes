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

package org.apache.streampipes.connect.adapters.flic;

import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.model.specific.SpecificDataStreamAdapter;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConfig;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConnectUtils;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConsumer;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class FlicMQTTAdapter extends SpecificDataStreamAdapter {

  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;
  private Thread thread;

  /**
   * A unique id to identify the adapter type
   */
  public static final String ID = "org.apache.streampipes.connect.adapters.flic.mqtt";

  /**
   * Empty constructor and a constructor with SpecificAdapterStreamDescription are mandatory
   */
  public FlicMQTTAdapter() {
  }

  public FlicMQTTAdapter(SpecificAdapterStreamDescription adapterDescription) {
    super(adapterDescription);
  }


  /**
   * Describe the adapter adapter and define what user inputs are required.
   *
   * @return
   */
  @Override
  public SpecificAdapterStreamDescription declareModel() {

    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(AdapterType.Energy)
        .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
        .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(),
            MqttConnectUtils.getAlternativesTwo())
        .requiredTextParameter(MqttConnectUtils.getTopicLabel())
        .build();
    description.setAppId(ID);

    return description;
  }

  /**
   * Takes the user input and creates the event schema. The event schema describes the properties of the event stream.
   *
   * @param adapterDescription
   * @return
   */
  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) throws AdapterException {
    return FlicUtils.getFlicSchema();
  }

  @Override
  public void startAdapter() throws AdapterException {
    StaticPropertyExtractor extractor =
        StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());

    this.mqttConfig = MqttConnectUtils.getMqttConfig(extractor);
    this.mqttConsumer = new MqttConsumer(this.mqttConfig, new EventProcessor(adapterPipeline));

    thread = new Thread(this.mqttConsumer);
    thread.start();
  }

  @Override
  public void stopAdapter() throws AdapterException {
    this.mqttConsumer.close();
  }

  /**
   * Required by StreamPipes return a new adapter instance by calling the constructor
   * with SpecificAdapterStreamDescription
   *
   * @param adapterDescription
   * @return
   */
  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    return new FlicMQTTAdapter(adapterDescription);
  }


  /**
   * Required by StreamPipes. Return the id of the adapter
   *
   * @return
   */
  @Override
  public String getId() {
    return ID;
  }

  private class EventProcessor implements InternalEventProcessor<byte[]> {
    private AdapterPipeline adapterPipeline;

    public EventProcessor(AdapterPipeline adapterpipeline) {
      this.adapterPipeline = adapterpipeline;
    }

    @Override
    public void onEvent(byte[] payload) {
      String s = new String(payload);
      FlicOutput output = new Gson().fromJson(s, FlicOutput.class);
      Map<String, Object> event = FlicUtils.getEvent(output);
      adapterPipeline.process(event);
    }
  }
}
