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
package org.apache.streampipes.sinks.brokers.jvm.mqtt.common;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import org.fusesource.mqtt.client.QoS;

import java.net.URI;

public class MqttUtils {

  private static final String TCP = "TCP";
  private static final String SSL_TLS = "SSL/TLS";
  private static final String TCP_PROTOCOL = "tcp://";
  private static final String SSL_PROTOCOL = "ssl://";
  private static final String COLON = ":";

  public MqttUtils() {
  }

  // remove non-digits
  public static QoS extractQoSFromString(String s) {
    int qos = Integer.parseInt(s.replaceAll("\\D+", ""));
    switch (qos) {
      case 0:
        return QoS.AT_MOST_ONCE;
      case 1:
        return QoS.AT_LEAST_ONCE;
      case 2:
        return QoS.EXACTLY_ONCE;
    }
    throw new SpRuntimeException("Could not retrieve QoS level: QoS " + qos);
  }

  public static String runningInstanceId(String elementId) {
    return elementId.substring(elementId.lastIndexOf(".") + 1);
  }

  public static URI makeMqttServerUri(String protocol, String host, int port) {
    if (protocol.contains(TCP)) {
      return URI.create(TCP_PROTOCOL + host + COLON + port);
    } else if (protocol.contains(SSL_TLS)) {
      return URI.create(SSL_PROTOCOL + host + COLON + port);
    }
    throw new SpRuntimeException("Connection protocol not supported! Use tcp:// or ssl://");
  }

  public static boolean extractBoolean(String s) {
    switch (s) {
      case "Yes":
        return true;
      case "No":
        return false;
    }
    throw new SpRuntimeException("Could not map string value to boolean: " + s);
  }

  public static long fromSecToMs(Long value) {
    return value * 1000;
  }
}
