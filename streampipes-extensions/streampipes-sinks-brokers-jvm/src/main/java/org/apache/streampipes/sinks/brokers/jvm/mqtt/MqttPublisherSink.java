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
package org.apache.streampipes.sinks.brokers.jvm.mqtt;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import java.util.Arrays;

public class MqttPublisherSink extends StreamPipesDataSink {

  private static final int DEFAULT_MQTT_PORT = 1883;
  private static final int DEFAULT_RECONNECT_PERIOD = 30;
  private static final int DEFAULT_KEEP_ALIVE = 30;

  public static final String TOPIC = "topic";
  public static final String HOST = "host";
  public static final String PORT = "port";
  public static final String AUTH_MODE = "auth-mode";
  public static final String NO_AUTH_ALTERNATIVE = "no-auth-alternative";
  public static final String AUTH_ALTERNATIVE = "basic-auth-alternative";
  public static final String USERNAME_GROUP = "username-group";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String QOS_LEVEL_KEY = "qos-level";
  public static final String CLEAN_SESSION_KEY = "clean-session";
  public static final String WILL_RETAIN = "will-retain";
  public static final String ENCRYPTION_MODE = "encryption-mode";
  public static final String RECONNECT_PERIOD_IN_SEC = "reconnect-period";
  public static final String WILL_MODE = "lwt-mode";
  public static final String NO_WILL_ALTERNATIVE = "no-lwt-alternative";
  public static final String WILL_ALTERNATIVE = "lwt-alternative";
  public static final String WILL_GROUP = "lwt-group";
  public static final String WILL_TOPIC = "lwt-topic";
  public static final String WILL_MESSAGE = "lwt-message";
  public static final String WILL_QOS = "lwt-qos-level";
  public static final String RETAIN = "retain";
  public static final String KEEP_ALIVE_IN_SEC = "keep-alive";
  public static final String MQTT_COMPLIANT = "mqtt-version-compliant";

  private MqttClient mqttClient;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.mqtt")
        .category(DataSinkType.MESSAGING)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.any())
        .requiredTextParameter(Labels.withId(TOPIC))
        .requiredTextParameter(Labels.withId(HOST))
        .requiredIntegerParameter(Labels.withId(PORT), DEFAULT_MQTT_PORT)
        .requiredAlternatives(
            Labels.withId(AUTH_MODE),
            Alternatives.from(Labels.withId(NO_AUTH_ALTERNATIVE), true),
            Alternatives.from(Labels.withId(AUTH_ALTERNATIVE),
                StaticProperties.group(Labels.withId(USERNAME_GROUP),
                    StaticProperties.stringFreeTextProperty(Labels.withId(USERNAME)),
                    StaticProperties.secretValue(Labels.withId(PASSWORD)))))
        .requiredSingleValueSelection(
            Labels.withId(ENCRYPTION_MODE),
            Arrays.asList(
                new Option("TCP", true),
                // SSL not yet supported
                new Option("SSL/TLS", false)))
        .requiredSingleValueSelection(
            Labels.withId(QOS_LEVEL_KEY),
            Arrays.asList(
                new Option("0 - at-most-once", false),
                new Option("1 - at-least-once", true),
                new Option("2 - exactly-once", false)))
        .requiredSingleValueSelection(
            Labels.withId(RETAIN),
            Arrays.asList(
                new Option("Yes", false),
                new Option("No", true)))
        .requiredSingleValueSelection(
            Labels.withId(CLEAN_SESSION_KEY),
            Arrays.asList(
                new Option("Yes", true),
                new Option("No", false)))
        .requiredIntegerParameter(Labels.withId(RECONNECT_PERIOD_IN_SEC), DEFAULT_RECONNECT_PERIOD)
        .requiredIntegerParameter(Labels.withId(KEEP_ALIVE_IN_SEC), DEFAULT_KEEP_ALIVE)
        .requiredSingleValueSelection(
            Labels.withId(MQTT_COMPLIANT),
            Arrays.asList(
                new Option("Yes", true),
                new Option("No", false)))
        .requiredAlternatives(
            Labels.withId(WILL_MODE),
            Alternatives.from(Labels.withId(NO_WILL_ALTERNATIVE), true),
            Alternatives.from(Labels.withId(WILL_ALTERNATIVE),
                StaticProperties.group(Labels.withId(WILL_GROUP),
                    StaticProperties.stringFreeTextProperty(Labels.withId(WILL_TOPIC)),
                    StaticProperties.stringFreeTextProperty(Labels.withId(WILL_MESSAGE)),
                    StaticProperties.singleValueSelection(Labels.withId(WILL_RETAIN),
                        Arrays.asList(
                            new Option("Yes", false),
                            new Option("No", true))),
                    StaticProperties.singleValueSelection(
                        Labels.withId(WILL_QOS),
                        Arrays.asList(
                            new Option("0 - at-most-once", true),
                            new Option("1 - at-least-once", false),
                            new Option("2 - exactly-once", false))))))
        .build();
  }


  @Override
  public void onInvocation(SinkParams params, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    this.mqttClient = new MqttClient(params);
    this.mqttClient.connect();
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    this.mqttClient.publish(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.mqttClient.disconnect();
  }
}
