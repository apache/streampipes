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
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Datatypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

/**
 * Adapter to connect to an Open Industry 4.0 (OI4) compatible device.
 *
 * @see <a href="https://openindustry4.com/fileadmin/Dateien/Downloads/OEC_Development_Guideline_V1.1.1.pdf"
 * >Open Industry 4.0 Development Guide</a>
 */
public class Oi4Adapter implements StreamPipesAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.oi4";

  private static final Logger LOG = LoggerFactory.getLogger(Oi4Adapter.class);
  private static final long ReceiveSchemaSleepTime = 100;
  private static final long ReceiveSchemaMaxTimeout = 5000;

  // Information about the topic structure can be found at page 57 of the above-mentioned development guide
  // The app id (missing here) needs to be provided by the user
  private static final String TOPIC_TEMPLATE = "Oi4/OTConnector/%s/Pub/Data";
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
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
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
        .requiredTextParameter(Labels.withId(OI4AdapterLabels.LABEL_KEY_APP_ID))
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
            payload.forEach(collector::collect);
          } catch (ParseException e) {
            LOG.debug("Message parsing failed - this might be caused by messages from a different sensor type");
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

    String appId = extractor.textParameter(OI4AdapterLabels.LABEL_KEY_APP_ID);

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
      mqttConfig = MqttConnectUtils.getMqttConfig(extractor, TOPIC_TEMPLATE.formatted(appId));
    } else {
      throw new AdapterException("Erroneous configuration provided - Configuration ia IODD is not yet available.");
    }
  }

  @Override
  public void onAdapterStopped(
      IAdapterParameterExtractor extractor,
      IAdapterRuntimeContext adapterRuntimeContext
  ) {
    mqttConsumer.close();
  }

  @Override
  public GuessSchema onSchemaRequested(
      IAdapterParameterExtractor extractor,
      IAdapterGuessSchemaContext adapterGuessSchemaContext
  ) throws AdapterException {
    try {
      this.applyConfiguration(extractor.getStaticPropertyExtractor());

      var sampleMessage = getSampleMessage();
      var guessSchema = guessSchemaFromSampleMessage(sampleMessage);
      updateTimestampPropertyIfExists(guessSchema);

      return guessSchema;
    } catch (RuntimeException e) {
      throw new AdapterException(e.getMessage(), e);
    }
  }

  /**
   * Updates the timestamp property in the given GuessSchema if it exists as it is not correctly guessed.
   * If the timestamp property exists, it is replaced with a proper timestamp property.
   *
   * @param guessSchema The GuessSchema to update.
   */
  private void updateTimestampPropertyIfExists(GuessSchema guessSchema) {
    var eventProperties = guessSchema.getEventSchema()
        .getEventProperties();

    var timestampPropertyOpt = eventProperties.stream()
        .filter(eventProperty ->
            eventProperty.getRuntimeName()
                .equals(OI4AdapterLabels.EVENT_KEY_TIMESTAMP)
        )
        .findFirst();

    var newTimestampProperty = timestampProperty(OI4AdapterLabels.EVENT_KEY_TIMESTAMP);

    // If the timestamp property exists, replace it with the new timestamp property
    timestampPropertyOpt.ifPresent(prop -> {
      eventProperties.removeIf(eventProperty -> eventProperty.getRuntimeName()
          .equals(OI4AdapterLabels.EVENT_KEY_TIMESTAMP));
      eventProperties.add(newTimestampProperty);
    });

    guessSchema.setEventSchema(new EventSchema(eventProperties));
  }

  private GuessSchema guessSchemaFromSampleMessage(byte[] sampleMessage) {
    try {
      var networkMessage = mapper.readValue(sampleMessage, NetworkMessage.class);
      var payload = extractPayload(networkMessage);

      String plainPayload = mapper.writeValueAsString(payload.get(0));
      return new JsonParsers(new JsonObjectParser())
          .getGuessSchema(convertByte(plainPayload.getBytes(StandardCharsets.UTF_8)));
    } catch (IOException e) {
      LOG.error("Error while reading sample message: {}", sampleMessage);
      throw new RuntimeException(e);
    }
  }

  private byte[] getSampleMessage() throws AdapterException {
    List<byte[]> sampleMessages = new ArrayList<>();
    long timeElapsed = 0;
    AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
    MqttConsumer guessConsumer = getGuessMqttConsumer(sampleMessages);

    var thread = new Thread(guessConsumer);
    thread.setUncaughtExceptionHandler((t, e) -> exceptionRef.set(e.getCause()));
    thread.start();

    while (sampleMessages.isEmpty() && exceptionRef.get() == null && timeElapsed < ReceiveSchemaMaxTimeout) {
      try {
        TimeUnit.MILLISECONDS.sleep(ReceiveSchemaSleepTime);
        timeElapsed += ReceiveSchemaSleepTime;
      } catch (InterruptedException e) {
        LOG.error("Schema guessing failed during waiting for an incoming event: {}", e.getMessage());
        break;
      }
    }
    guessConsumer.close();

    Throwable threadException = exceptionRef.get();
    if (threadException != null) {
      throw new AdapterException(threadException.getMessage(), threadException);
    }

    if (!sampleMessages.isEmpty()) {
      return sampleMessages.get(0);
    } else {
      throw new AdapterException("No messages received");
    }
  }

  /**
   * Obtain a specialized MQTT consumer designed to infer the event schema based on provided sampleMessages.
   *
   * @param sampleMessages A list of byte arrays representing MQTT message payloads.
   * @return A customized MqttConsumer instance.
   */
  private MqttConsumer getGuessMqttConsumer(List<byte[]> sampleMessages) {
    // Define a specialized event processor that adds an event to the sampleMessages array
    // only if it meets certain expectations, as verified by extractPayload.
    InternalEventProcessor<byte[]> eventProcessor = event -> {
      InputStream in = convertByte(event);
      NetworkMessage networkMessage;
      try {
        networkMessage = mapper.readValue(in, NetworkMessage.class);
      } catch (IOException e) {
        LOG.error("Error during parsing of incoming MQTT event.");
        throw new RuntimeException(e);
      }
      // Attempt to extract payload from the NetworkMessage
      extractPayload(networkMessage);
      // If successful, add the event to the sampleMessages array
      sampleMessages.add(event);
    };
    return new MqttConsumer(this.mqttConfig, eventProcessor);
  }

  private List<Map<String, Object>> extractPayload(NetworkMessage message) throws ParseException {

    var dataMessages = findProcessDataInputMessage(message);
    var result = new ArrayList<Map<String, Object>>();

    if (!dataMessages.isEmpty()) {

      for (var dataMessage : dataMessages) {

        var sensorId = getSensorIdFromSource(dataMessage.source());

        // Verify that the message corresponds to the designated sensor type.
        // This validation relies on the assumption that the source information includes the sensor type.
        if (dataMessage.source()
            .contains(givenSensorType)) {

          // an empty list of selected sensors means that we want to collect data from all sensors available
          if (selectedSensors.isEmpty() || selectedSensors.contains(sensorId)) {
            result.add(extractAndEnrichMessagePayload(dataMessage, sensorId));
          }
        }
      }
    }
    if (!result.isEmpty()) {
      return result;
    } else {
      throw new ParseException(String.format("No sensor of type %s found in message", givenSensorType));
    }
  }

  private List<DataSetMessage> findProcessDataInputMessage(NetworkMessage message) {
    return message.messages()
        .stream()
        .filter(msg -> msg.filter()
            .equals(OI4AdapterLabels.MESSAGE_VALUE_FILTER))
        .toList();
  }

  private Map<String, Object> extractAndEnrichMessagePayload(DataSetMessage dataSetMessage, String sensorId) {
    var payload = dataSetMessage
        .payload();
    try {
      payload.put(
          OI4AdapterLabels.EVENT_KEY_TIMESTAMP,
          parseDate(dataSetMessage.timestamp())
      );
    } catch (DateTimeParseException e) {
      throw new RuntimeException(e);
    }
    payload.put(
        OI4AdapterLabels.EVENT_KEY_SENSOR_ID,
        sensorId
    );

    return replaceSpecialChars(payload);
  }

  private Map<String, Object> replaceSpecialChars(Map<String, Object> originalMap) {
    return originalMap.entrySet()
        .stream()
        .collect(Collectors.toMap(
            entry -> entry.getKey().replace("-", ""),
            Map.Entry::getValue,
            (oldValue, newValue) -> oldValue)
        );
  }

  private long parseDate(String timestamp) throws DateTimeParseException {
    return Instant.parse(timestamp).toEpochMilli();
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
