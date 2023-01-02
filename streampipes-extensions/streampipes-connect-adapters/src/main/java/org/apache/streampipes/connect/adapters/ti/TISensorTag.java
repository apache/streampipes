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

package org.apache.streampipes.connect.adapters.ti;

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
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.SpecificDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.SPSensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.sdk.helpers.EpProperties.booleanEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.doubleEp;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class TISensorTag extends SpecificDataStreamAdapter {

  private Logger logger = LoggerFactory.getLogger(TISensorTag.class);

  public static final String ID = "org.apache.streampipes.connect.adapters.ti";

  private static final String TIMESTAMP = "timestamp";
  private static final String AMBIENT_TEMP = "ambientTemp";
  private static final String OBJECT_TEMP = "objectTemp";
  private static final String HUMIDITY = "humidity";
  private static final String ACCELERATION_X = "accelX";
  private static final String ACCELERATION_Y = "accelY";
  private static final String ACCELERATION_Z = "accelZ";
  private static final String GYROSCOPE_X = "gyroX";
  private static final String GYROSCOPE_Y = "gyroY";
  private static final String GYROSCOPE_Z = "gyroZ";
  private static final String MAGNETOMETER_X = "magX";
  private static final String MAGNETOMETER_Y = "magY";
  private static final String MAGNETOMETER_Z = "magZ";
  private static final String LIGHT = "light";
  private static final String KEY_1 = "key1";
  private static final String KEY_2 = "key2";

  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;
  private Thread thread;

  public TISensorTag() {
    super();
  }

  public TISensorTag(SpecificAdapterStreamDescription adapterDescription, MqttConfig mqttConfig) {
    super(adapterDescription);
    this.mqttConfig = mqttConfig;
  }

  @Override
  public SpecificAdapterStreamDescription declareModel() {

    SpecificAdapterStreamDescription description = SpecificDataStreamAdapterBuilder.create(ID)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(AdapterType.Environment, AdapterType.OpenData)
        .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
        .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(),
            MqttConnectUtils.getAlternativesTwo())
        .requiredTextParameter(MqttConnectUtils.getTopicLabel())
        .build();

    description.setAppId(ID);
    return description;
  }

  @Override
  public void startAdapter() throws AdapterException {
    this.mqttConsumer = new MqttConsumer(this.mqttConfig, new EventProcessor(adapterPipeline));

    thread = new Thread(this.mqttConsumer);
    thread.start();
  }

  @Override
  public void stopAdapter() throws AdapterException {
    this.mqttConsumer.close();
  }

  @Override
  public Adapter getInstance(SpecificAdapterStreamDescription adapterDescription) {
    MqttConfig mqttConfig;
    StaticPropertyExtractor extractor =
        StaticPropertyExtractor.from(adapterDescription.getConfig(), new ArrayList<>());

    mqttConfig = MqttConnectUtils.getMqttConfig(extractor);

    return new TISensorTag(adapterDescription, mqttConfig);
  }

  @Override
  public GuessSchema getSchema(SpecificAdapterStreamDescription adapterDescription) {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(TIMESTAMP))
        .property(doubleEp(Labels.from(AMBIENT_TEMP, "Ambient Temperature", ""),
            AMBIENT_TEMP, SO.NUMBER))
        .property(doubleEp(Labels.from(OBJECT_TEMP, "Object Temperature", ""),
            OBJECT_TEMP, SO.NUMBER))
        .property(doubleEp(Labels.from(HUMIDITY, "Humidity", ""),
            HUMIDITY, SO.NUMBER))
        .property(doubleEp(Labels.from(ACCELERATION_X, "Acceleration X", ""),
            ACCELERATION_X, SPSensor.ACCELERATION_X))
        .property(doubleEp(Labels.from(ACCELERATION_Y, "Acceleration Y", ""),
            ACCELERATION_Y, SPSensor.ACCELERATION_Y))
        .property(doubleEp(Labels.from(ACCELERATION_Z, "Acceleration Z", ""),
            ACCELERATION_Z, SPSensor.ACCELERATION_Z))
        .property(doubleEp(Labels.from(GYROSCOPE_X, "Gyroscope X", ""),
            GYROSCOPE_X, SO.NUMBER))
        .property(doubleEp(Labels.from(GYROSCOPE_Y, "Gyroscope Y", ""),
            GYROSCOPE_Y, SO.NUMBER))
        .property(doubleEp(Labels.from(GYROSCOPE_Z, "Gyroscope Z", ""),
            GYROSCOPE_Z, SO.NUMBER))
        .property(doubleEp(Labels.from(MAGNETOMETER_X, "Magnetometer X", ""),
            MAGNETOMETER_X, SO.NUMBER))
        .property(doubleEp(Labels.from(MAGNETOMETER_Y, "Magnetometer Y", ""),
            MAGNETOMETER_Y, SO.NUMBER))
        .property(doubleEp(Labels.from(MAGNETOMETER_Z, "Magnetometer Z", ""),
            MAGNETOMETER_Z, SO.NUMBER))
        .property(doubleEp(Labels.from(LIGHT, "Light", ""),
            LIGHT, SO.NUMBER))
        .property(booleanEp(Labels.from(KEY_1, "Key 1", ""),
            KEY_1, SO.BOOLEAN))
        .property(booleanEp(Labels.from(KEY_2, "Key 2", ""),
            KEY_2, SO.BOOLEAN))
        .build();
  }

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
      Map<String, Object> result = parseEvent(new String(payload));
      adapterPipeline.process(result);
    }
  }

  /**
   * This specific parser is required since the messages on mqtt produced by the app is not always valid JSON
   *
   * @param s
   * @return
   */
  public static Map<String, Object> parseEvent(String s) {
    Map<String, Object> result = new HashMap<>();
    String[] lines = s.split("\n");
    for (String line : lines) {
      if (line.startsWith("\"")) {
        line = line.replaceAll(",", "").replaceAll("\"", "");
        String[] keyValue = line.split(":");

        // convert keys to boolean, other sensor values are doubles
        if (keyValue[0].startsWith("key")) {
          result.put(keyValue[0], Double.parseDouble(keyValue[1]) == 1.0);
        } else {
          result.put(keyValue[0], Double.parseDouble(keyValue[1]));
        }
      }
    }

    if (!result.containsKey("key1") || !result.containsKey("key2")) {
      result.put("key1", false);
      result.put("key2", false);
    }

    result.put(TIMESTAMP, System.currentTimeMillis());

    return result;
  }
}
