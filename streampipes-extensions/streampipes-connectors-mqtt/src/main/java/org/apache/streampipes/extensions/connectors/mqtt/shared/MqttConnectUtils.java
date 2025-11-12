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

package org.apache.streampipes.extensions.connectors.mqtt.shared;

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class MqttConnectUtils {

  /**
   * Keys of user configuration parameters
   */
  //Adapter
  public static final String ACCESS_MODE = "access-mode";
  public static final String ANONYMOUS_ACCESS = "anonymous-alternative";
  public static final String USERNAME_ACCESS = "username-alternative";
  public static final String CLIENT_CERT_ACCESS = "client-cert-alternative";
  public static final String USERNAME_GROUP = "username-group";
  public static final String CLIENT_CERT_GROUP = "client-cert-group";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String CLIENTCERT = "clientcert";
  public static final String CLIENTKEY = "clientkey";
  public static final String BROKER_URL = "broker_url";
  public static final String TOPIC = "topic";
//Pubisher
  public static final String QOS_LEVEL_KEY = "qos-level";
  public static final String CLEAN_SESSION_KEY = "clean-session";
  public static final String WILL_RETAIN = "will-retain";
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

  public static Label getAccessModeLabel() {
    return Labels.withId(ACCESS_MODE);
  }

  public static Label getBrokerUrlLabel() {
    return Labels.withId(BROKER_URL);
  }

  public static Label getTopicLabel() {
    return Labels.withId(TOPIC);
  }

  public static Label getQosLevelLabel() {
    return Labels.withId(QOS_LEVEL_KEY);
  }
    public static Label getRetainLabel() {
    return Labels.withId(RETAIN);
  }

    public static Label getCleanSessionLabel() {
    return Labels.withId(CLEAN_SESSION_KEY);
  }
    public static Label getReconnectPeriodLabel() {
    return Labels.withId(RECONNECT_PERIOD_IN_SEC);
  }

      public static Label getKeepAliveLabel() {
    return Labels.withId(KEEP_ALIVE_IN_SEC);
  }

      public static Label getMqttComplient() {
    return Labels.withId(MQTT_COMPLIANT);
  }

      public static Label getWillModeLabel() {
    return Labels.withId(WILL_MODE);
  }


  public static StaticPropertyAlternative getNoWillAlternative(){
    return Alternatives.from(Labels.withId(NO_WILL_ALTERNATIVE), true);
  }

  public static StaticPropertyAlternative getWillAlternative(){
    return Alternatives.from(Labels.withId(WILL_ALTERNATIVE),
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
                                                                new Option("2 - exactly-once", false)))))
  }

  public static StaticPropertyAlternative getAnonymousAccess() {
    return Alternatives.from(Labels.withId(ANONYMOUS_ACCESS));

  }

  public static StaticPropertyAlternative getAnonymousAccess(boolean selected) {
    return Alternatives.from(Labels.withId(ANONYMOUS_ACCESS), selected);

  }

  public static StaticPropertyAlternative getUsernameAccess() {
    return Alternatives.from(Labels.withId(USERNAME_ACCESS),
        StaticProperties.group(Labels.withId(USERNAME_GROUP),
            StaticProperties.stringFreeTextProperty(Labels.withId(USERNAME)),
            StaticProperties.secretValue(Labels.withId(PASSWORD))));

  }

  public static List<Option> getQOSLevelSelection(){
    return Arrays.asList(
                                        new Option("0 - at-most-once", false),
                                        new Option("1 - at-least-once", true),
                                        new Option("2 - exactly-once", false));
  }


  public static List<Option> getRetainSelection(){
    return Arrays.asList(
                                        new Option("Yes", false),
                                        new Option("No", true));
  }



  public static List<Option> getCleanSessionSelection(){
     return Arrays.asList(
                                        new Option("Yes", true),
                                        new Option("No", false));
  }


  public static List<Option> getMqttSelection(){
     return Arrays.asList(
                                        new Option("Yes", true),
                                        new Option("No", false));
  }
  

  public static StaticPropertyAlternative getClientCertAccess() {
    var group = StaticProperties.group(
        Labels.withId(CLIENT_CERT_GROUP),
        StaticProperties.stringFreeTextProperty(Labels.withId(CLIENTCERT), true, false),
        StaticProperties.secretValue(Labels.withId(CLIENTKEY)));
    group.setHorizontalRendering(false);
    return Alternatives.from(Labels.withId(CLIENT_CERT_ACCESS), group);

  }

  public static MqttConfig getMqttConfig(IParameterExtractor extractor) {
    return getMqttConfig(extractor, null);
  }

  public static String getProtocol(String brokeruri) {
    String protocol = null;

    try {
      URI uri = new URI(brokeruri);
      protocol = uri.getScheme();
    } catch (URISyntaxException e) {

    }

    return protocol;
  }

  public static boolean tlsEnabled(String protocol) {
    if (protocol == null) {
      return false;
    }
    String proto = protocol.toLowerCase();
    return proto.equals("ssl") || proto.equals("tls") || proto.equals("mqtts");
  }

  public static MqttConfig getMqttConfig(IParameterExtractor extractor, String topicInput) {
    MqttConfig mqttConfig;
    String brokerUrl = extractor.singleValueParameter(BROKER_URL, String.class);
    String protocol = getProtocol(brokerUrl);
    boolean tlsEnabled = tlsEnabled(protocol);

    String topic;
    if (topicInput == null) {
      topic = extractor.singleValueParameter(TOPIC, String.class);
    } else {
      topic = topicInput;
    }

    String selectedAlternative = extractor.selectedAlternativeInternalId(ACCESS_MODE);

    if (selectedAlternative.equals(ANONYMOUS_ACCESS)) {
      mqttConfig = new MqttConfig(brokerUrl, topic);
      if (tlsEnabled) {
        mqttConfig = new MqttConfig(brokerUrl, topic, true);
      }
    } else if (selectedAlternative.equals(CLIENT_CERT_ACCESS)) {

      String clientcert = extractor.singleValueParameter(CLIENTCERT, String.class);
      String clientkey = extractor.secretValue(CLIENTKEY);
      // TWO way auth so TLS needs to be enabled
      mqttConfig = new MqttConfig(brokerUrl, topic, true, clientcert, clientkey);

    } else {
      String username = extractor.singleValueParameter(USERNAME, String.class);
      String password = extractor.secretValue(PASSWORD);
      if (tlsEnabled) {
        mqttConfig = new MqttConfig(brokerUrl, topic, username, password, true);
      } else {
        mqttConfig = new MqttConfig(brokerUrl, topic, username, password);
      }
    }

    return mqttConfig;
  }

}
