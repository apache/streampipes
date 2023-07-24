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
import org.apache.streampipes.connect.iiot.adapters.iolink.sensor.SensorVVB001;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.JsonParsers;
import org.apache.streampipes.extensions.management.connect.adapter.parser.json.JsonObjectParser;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConfig;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConnectUtils;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConsumer;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class IfmAlMqttAdapter implements StreamPipesAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.iolink";

  private static final Logger LOG = LoggerFactory.getLogger(IfmAlMqttAdapter.class);

  private static final String PORTS = "ports";
  private static final String SENSOR_TYPE = "sensor_type";

  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;

  private final IParser parser;

  private List<String> ports;

  public IfmAlMqttAdapter() {
    parser = new JsonParsers(new JsonObjectParser());
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, IfmAlMqttAdapter::new)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Generic, AdapterType.Manufacturing)
        .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
        .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(),
            MqttConnectUtils.getAlternativesTwo())
        .requiredMultiValueSelection(Labels.withId(PORTS),
            Options.from("Port 1", "Port 2", "Port 3", "Port 4"))
        .requiredSingleValueSelection(Labels.withId(SENSOR_TYPE),
            Options.from("VVB001"))
        .requiredTextParameter(MqttConnectUtils.getTopicLabel())
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    var sensor = new SensorVVB001();

    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.mqttConsumer = new MqttConsumer(
        this.mqttConfig,
        (mqttEvent) -> {
          try {
            InputStream in = convertByte(mqttEvent);
            parser.parse(in, (event) -> {

              var data = getMap(event, "data");
              var payload = getMap(data, "payload");

              var deviceInfo = getMap(payload, "/deviceinfo/serialnumber");
              var serialnumber = deviceInfo.get("data");

              for (int i = 0; i < ports.size(); i++) {

                var portResult = getMap(payload,
                    "/iolinkmaster/port[%s]/iolinkdevice/pdin".formatted(ports.get(i)));
                var eventData = (String) portResult.get("data");

                var parsedEvent = sensor.parseEvent(eventData);
                parsedEvent.put("timestamp", System.currentTimeMillis() + i);
                parsedEvent.put("port", "port" + ports.get(i));
                parsedEvent.put(SensorVVB001.IO_LINK_MASTER_SN, serialnumber);

                collector.collect(parsedEvent);
              }
            });
          } catch (Exception e) {
            adapterRuntimeContext
                .getLogger()
                .error(e);
            LOG.error("Could not parse event", e);
          }
        });

    Thread thread = new Thread(this.mqttConsumer);
    thread.start();
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.mqttConsumer.close();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());

    return new SensorVVB001().getEventSchema();
  }


  private void applyConfiguration(IStaticPropertyExtractor extractor) {
    mqttConfig = MqttConnectUtils.getMqttConfig(extractor);
    String sensorType = extractor.selectedSingleValue(SENSOR_TYPE, String.class);
    var selectedPorts = extractor.selectedMultiValues(PORTS, String.class);
    ports = selectedPorts.stream()
        .map(port -> port.substring(5))
        .toList();
  }

  private Map<String, Object> getMap(Map<String, Object> event, String key) {
    if (event.containsKey(key)) {
      var payload = event.get(key);
      if (payload instanceof Map) {
        return (Map<String, Object>) event.get(key);
      } else {
        throw new ParseException(getErrorMessage(key));
      }
    } else {
      throw new ParseException(getErrorMessage(key));
    }
  }

  private String getErrorMessage(String key) {
    return "The event does not contain key: %s. Please reconfigure the IOLink master to include this key".formatted(
        key);
  }

  private InputStream convertByte(byte[] event) {
    return IOUtils.toInputStream(new String(event), "UTF-8");
  }
}
