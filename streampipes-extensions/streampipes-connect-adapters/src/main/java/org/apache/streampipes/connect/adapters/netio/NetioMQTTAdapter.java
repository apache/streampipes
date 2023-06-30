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

package org.apache.streampipes.connect.adapters.netio;

import org.apache.streampipes.connect.adapters.netio.model.NetioAllPowerOutputs;
import org.apache.streampipes.connect.adapters.netio.model.NetioPowerOutput;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConfig;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConnectUtils;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConsumer;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetioMQTTAdapter implements StreamPipesAdapter {

  private MqttConsumer mqttConsumer;

  /**
   * A unique id to identify the adapter type
   */
  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.netio.mqtt";

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, NetioMQTTAdapter::new)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Energy)
        .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
        .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(),
            MqttConnectUtils.getAlternativesTwo())
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) {

    MqttConfig mqttConfig = MqttConnectUtils.getMqttConfig(
        extractor.getStaticPropertyExtractor(), "devices/netio/messages/events/");
    this.mqttConsumer = new MqttConsumer(mqttConfig, new EventProcessor(collector));

    Thread thread = new Thread(this.mqttConsumer);
    thread.start();
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    this.mqttConsumer.close();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) {
    return NetioUtils.getNetioSchema();
  }

  private record EventProcessor(IEventCollector collector) implements InternalEventProcessor<byte[]> {

    @Override
    public void onEvent(byte[] payload) {
      List<Map<String, Object>> events = parseEvent(payload);

      for (Map<String, Object> event : events) {
        collector.collect(event);
      }
    }
  }

  public static List<Map<String, Object>> parseEvent(byte[] input) {
    List<Map<String, Object>> result = new ArrayList<>();

    String s = new String(input);

    NetioAllPowerOutputs allPowerOutputs = new Gson().fromJson(s, NetioAllPowerOutputs.class);

    for (NetioPowerOutput output : allPowerOutputs.getPowerOutputs()) {
      if (allPowerOutputs.getGobalMeasure() != null && output != null) {
        Map<String, Object> event = NetioUtils.getEvent(allPowerOutputs.getGobalMeasure(), output);
        result.add(event);
      }
    }

    return result;
  }

}
