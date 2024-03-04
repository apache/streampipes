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

package org.apache.streampipes.connect.iiot.adapters.iolink;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.iiot.adapters.iolink.sensor.IoLinkSensor;
import org.apache.streampipes.connect.iiot.adapters.iolink.sensor.SensorVVB001Hilscher;
import org.apache.streampipes.connect.iiot.adapters.iolink.sensor.SensorVVB001Ifm;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConfig;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConsumer;
import org.apache.streampipes.extensions.management.connect.adapter.parser.JsonParsers;
import org.apache.streampipes.extensions.management.connect.adapter.parser.json.JsonObjectParser;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HilscherMqttAdapter implements StreamPipesAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.hilscher";

  private static final Logger LOG = LoggerFactory.getLogger(HilscherMqttAdapter.class);

  private static final String FILTER_KEY = "Filter";
  private static final String MESSAGES_KEY = "Messages";
  private static final String PAYLOAD_KEY = "Payload";
  private static final String PROCESS_DATA_KEY = "processDataInput";
  private static final String SENSOR_TYPE = "sensor_type";

  private static final String SENSOR_VVB001_LABEL = "VVB001";
  private static final String SOURCE_KEY = "Source";
  private final IParser parser;
  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;

  private String selectedSensorName;

  public HilscherMqttAdapter() {
    parser = new JsonParsers(new JsonObjectParser());
  }

  @Override
  public IAdapterConfiguration declareConfig() {

    return AdapterConfigurationBuilder
        .create(ID, 0, HilscherMqttAdapter::new)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Generic, AdapterType.Manufacturing)
        .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
        .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(),
                              MqttConnectUtils.getAlternativesTwo()
        )
        .requiredSingleValueSelection(
            Labels.withId(SENSOR_TYPE),
            Options.from(SENSOR_VVB001_LABEL)
        )
        .requiredTextParameter(MqttConnectUtils.getTopicLabel())
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(
      IAdapterParameterExtractor extractor,
      IEventCollector collector,
      IAdapterRuntimeContext adapterRuntimeContext
  ) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());

    var sensor = new SensorVVB001Hilscher();
    this.mqttConsumer = new MqttConsumer(
        this.mqttConfig,
        (mqttEvent) -> {
          try {
            InputStream in = convertByte(mqttEvent);
            parser.parse(in, (event) -> {
                           var sensorValues = getProcessData(event);

                           var parsedEvent = sensor.parseEvent(sensorValues);

                           collector.collect(parsedEvent);

                         }
            );
          } catch (ParseException e) {
            LOG.error("Error during parsing of JSON message body: %s", mqttEvent);
          }
        }
    );

    Thread thread = new Thread(this.mqttConsumer);
    thread.start();

  }

  private InputStream convertByte(byte[] event) {
    return IOUtils.toInputStream(new String(event), StandardCharsets.UTF_8);
  }

  private void applyConfiguration(IStaticPropertyExtractor extractor) {
    mqttConfig = MqttConnectUtils.getMqttConfig(extractor);
    selectedSensorName = extractor.selectedSingleValue(SENSOR_TYPE, String.class);
  }

  @Override
  public void onAdapterStopped(
      IAdapterParameterExtractor extractor,
      IAdapterRuntimeContext adapterRuntimeContext
  ) throws AdapterException {
    mqttConsumer.close();
  }

  @Override
  public GuessSchema onSchemaRequested(
      IAdapterParameterExtractor extractor,
      IAdapterGuessSchemaContext adapterGuessSchemaContext
  ) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    if (selectedSensorName.equals(SENSOR_VVB001_LABEL)) {
      return new SensorVVB001Hilscher().getEventSchema();
    }
    throw new AdapterException("Invalid adapter configuration - selected adapter does not exist: %s".formatted(
        selectedSensorName));
  }

  private Map<String, Object> getProcessData(Map<String, Object> event) {

    // extract messages contained in the current event
    List<Map<String, Object>> messages;
    if (event.containsKey(MESSAGES_KEY)) {
      messages = (List<Map<String, Object>>) event.get(MESSAGES_KEY);
    } else {
      throw new ParseException("Current event does follow the expected structure - no key '%s' exists".formatted(
          MESSAGES_KEY));
    }

    var message =
        messages.stream()
                .filter(msg -> msg.getOrDefault(FILTER_KEY, "")
                                  .equals(PROCESS_DATA_KEY))
                .findFirst();
    if (message.isPresent()) {
      var data = (Map<String, Object>) message.get()
                                              .get(PAYLOAD_KEY);
      data.put(
          SensorVVB001Hilscher.TIMESTAMP_NAME_SP,
          message.get()
                 .get(SensorVVB001Hilscher.TIMESTAMP_NAME_SOURCE)
      );
      data.put(
          SensorVVB001Hilscher.SENSOR_ID_NAME,
          getSensorIdFromSource(message.get()
                                       .get(SOURCE_KEY)
                                       .toString())
      );
      return (Map<String, Object>) message.get()
                                          .get(PAYLOAD_KEY);
    }
    throw new ParseException("No process data message");

  }

  private String getSensorIdFromSource(String source) {
    return source.substring(source.lastIndexOf("/") + 1);
  }
}
