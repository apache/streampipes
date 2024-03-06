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

package org.apache.streampipes.connect.iiot.adapters.oi4;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.iiot.adapters.oi4.model.DataSetMessage;
import org.apache.streampipes.connect.iiot.adapters.oi4.model.NetworkMessage;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
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
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Adapter to connect to an Open Industry 4.0 (OI4) compatible device.
 *
 * @see <a href="https://openindustry4.com/fileadmin/Dateien/Downloads/OEC_Development_Guideline_V1.1.1.pdf"
 * >Open Industry 4.0 Development Guide</a>
 */
public class Oi4Adapter implements StreamPipesAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.oi4";

  private static final Logger LOG = LoggerFactory.getLogger(Oi4Adapter.class);
  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  // Information about the topic structure can be found at page 57 of the above-mentioned development guide
  private static final String TOPIC_TEMPLATE = "Oi4/OTConnector/hilscher.com/netFIELD,20App,20OPC,20UA,20IO-Link,"
      + "20Adapter/1917.011/netfield-app-opc-ua-io-link-adapter/Pub/Data";
  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;

  private List<String> selectedSensors;
  private String givenSensorType;
  protected final ObjectMapper mapper;

  public Oi4Adapter() {
    mapper = new ObjectMapper();
  }

  @Override
  public IAdapterConfiguration declareConfig() {

    return AdapterConfigurationBuilder
        .create(ID, 0, Oi4Adapter::new)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Generic, AdapterType.Manufacturing)
        .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
        .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(),
                              MqttConnectUtils.getAlternativesTwo()
        )
        .requiredAlternatives(
            Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSOR_DESCRIPTION),
            Alternatives.from(
                Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSOR_TYPE_ALTERNATIVE),
                StaticProperties.freeTextProperty(
                    Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSOR_TYPE_INPUT),
                    Datatypes.String
                )
            ),
            Alternatives.from(
                Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSOR_IODD_ALTERNATIVE),
                StaticProperties.fileProperty(Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSOR_IODD_INPUT))
            )
        )
        .requiredAlternatives(
            Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSORS_ALTERNATIVES),
            Alternatives.from(Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSORS_ALL_ALTERNATIVE)),
            Alternatives.from(
                Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSORS_LIST_ALTERNATIVE),
                StaticProperties.stringFreeTextProperty(Labels.withId(OI4AdapterLabels.LABEL_KEY_SENSORS_LIST_INPUT))
            )

        )
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(
      IAdapterParameterExtractor extractor,
      IEventCollector collector,
      IAdapterRuntimeContext adapterRuntimeContext
  ) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());

    this.mqttConsumer = new MqttConsumer(
        this.mqttConfig,
        (mqttEvent) -> {
          try {
            InputStream in = convertByte(mqttEvent);
            var networkMessage = mapper.readValue(in, NetworkMessage.class);
            var payload = extractPayload(networkMessage);
            collector.collect(payload);
          } catch (ParseException e) {
            LOG.error("Error during parsing of JSON message body: {}", mqttEvent);
          } catch (IOException e) {
            LOG.error("Error during reading the MQTT Event: {}", e.getMessage());
          }
        }
    );

    Thread thread = new Thread(this.mqttConsumer);
    thread.start();

  }

  private InputStream convertByte(byte[] event) {
    return IOUtils.toInputStream(new String(event), StandardCharsets.UTF_8);
  }

  private void applyConfiguration(IStaticPropertyExtractor extractor) throws AdapterException {
    String selectedAlternativeSensorDescription = extractor.selectedAlternativeInternalId(
        OI4AdapterLabels.LABEL_KEY_SENSOR_DESCRIPTION
    );
    String selectedAlternativeSelectedSensors = extractor.selectedAlternativeInternalId(
        OI4AdapterLabels.LABEL_KEY_SENSORS_ALTERNATIVES
    );

    if (selectedAlternativeSelectedSensors.equals(OI4AdapterLabels.LABEL_KEY_SENSORS_ALL_ALTERNATIVE)) {
      // An empty list is used to indicated that messages from all sensors should be collected
      selectedSensors = List.of();
    } else {
      var selectedSensorsText = extractor.textParameter(OI4AdapterLabels.LABEL_KEY_SENSORS_LIST_INPUT);
      selectedSensors = Arrays.stream(selectedSensorsText.split(","))
                              .toList();
    }

    if (selectedAlternativeSensorDescription.equals(OI4AdapterLabels.LABEL_KEY_SENSOR_TYPE_ALTERNATIVE)) {
      givenSensorType = extractor.textParameter(OI4AdapterLabels.LABEL_KEY_SENSOR_TYPE_INPUT);
      mqttConfig = MqttConnectUtils.getMqttConfig(extractor, TOPIC_TEMPLATE);
    } else {
      throw new AdapterException("Erroneous configuration provided - Configuration ia IODD is not yet available.");
    }
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

    List<byte[]> elements = new ArrayList<>();
    InternalEventProcessor<byte[]> eventProcessor = elements::add;

    MqttConsumer guessConsumer = new MqttConsumer(this.mqttConfig, eventProcessor);

    Thread thread = new Thread(guessConsumer);
    thread.start();

    while (guessConsumer.getMessageCount() < 1) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        LOG.error("Schema guessing failed during waiting for an incoming event: {}", e.getMessage());
      }
    }

    try {
      var networkMessage = mapper.readValue(elements.get(0), NetworkMessage.class);
      var payload = extractPayload(networkMessage);

      String plainPayload = mapper.writeValueAsString(payload);
      return new JsonParsers(new JsonObjectParser())
          .getGuessSchema(convertByte(plainPayload.getBytes(StandardCharsets.UTF_8)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Map<String, Object> extractPayload(NetworkMessage message) {

    var dataMessageOpt = findProcessDataInputMessage(message);

    if (dataMessageOpt.isPresent()) {

      var dataMessage = dataMessageOpt.get();

      var sensorId = getSensorIdFromSource(dataMessage.source());

      // Verify that the message corresponds to the designated sensor type.
      // This validation relies on the assumption that the source information includes the sensor type.
      if (dataMessage.source()
                     .contains(givenSensorType)) {

        // an empty list of selected sensors means that we want to collect data from all sensors available
        if (selectedSensors.isEmpty() || selectedSensors.contains(sensorId)) {

          return extractAndEnrichMessagePayload(dataMessage, sensorId);
        }
      }
    }
    throw new ParseException("No process data message");

  }

  private Optional<DataSetMessage> findProcessDataInputMessage(NetworkMessage message) {
    return message.messages()
                  .stream()
                  .filter(msg -> msg.filter()
                                    .equals(OI4AdapterLabels.MESSAGE_VALUE_FILTER))
                  .findFirst();
  }

  private Map<String, Object> extractAndEnrichMessagePayload(DataSetMessage dataSetMessage, String sensorId) {
    var payload = dataSetMessage
        .payload();
    try {
      payload.put(
          OI4AdapterLabels.EVENT_KEY_TIMESTAMP,
          new SimpleDateFormat(DATE_FORMAT).parse(dataSetMessage.timestamp())
                                           .getTime()
      );
    } catch (java.text.ParseException e) {
      throw new RuntimeException(e);
    }
    payload.put(
        OI4AdapterLabels.EVENT_KEY_SENSOR_ID,
        sensorId
    );

    return payload;
  }

  /**
   * Extracts the sensor ID from the provided source string.
   * It assumes that the sensor ID is located after the last occurrence of "/" in the source string.
   *
   * @param source The source string
   * @return The extracted sensor ID.
   */
  private String getSensorIdFromSource(String source) {
    return source.substring(source.lastIndexOf("/") + 1);
  }
}
