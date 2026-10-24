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

package org.apache.streampipes.extensions.connectors.plc.adapter.migration;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** Connection parameter and tag syntax changes introduced by PLC4X 1.0. */
public final class Plc4xV1Configuration {

  private static final Map<String, String> RENAMED_OPTIONS = Map.ofEntries(
      Map.entry("request-timeout", "request-timeout-ms"),
      Map.entry("timeout-request", "request-timeout-ms"),
      Map.entry("connect-timeout", "connect-timeout-ms"),
      Map.entry("read-timeout", "read-timeout-ms"),
      Map.entry("write-timeout", "write-timeout-ms"),
      Map.entry("session-timeout", "session-timeout-ms"),
      Map.entry("channel-lifetime", "channel-lifetime-ms"),
      Map.entry("min-channel-lifetime", "min-channel-lifetime-ms"),
      Map.entry("ha-heartbeat-interval", "ha-heartbeat-interval-ms"),
      Map.entry("ha-failover-timeout", "ha-failover-timeout-ms"),
      Map.entry("negotiation-timeout", "handshake-timeout-ms"),
      Map.entry("cotp-connection-timeout", "handshake-timeout-ms"),
      Map.entry("tcp-no-delay", "no-delay"),
      Map.entry("cotp-tpdu-size", "tpdu-size"),
      Map.entry("tls-version", "version"),
      Map.entry("verify-ssl", "verify"),
      Map.entry("key-store-file", "tls.keystore"),
      Map.entry("key-store-password", "tls.keystore-password"),
      Map.entry("key-store-type", "tls.keystore-type"),
      Map.entry("trust-store-file", "tls.trust-store"),
      Map.entry("trust-store-password", "tls.trust-store-password"),
      Map.entry("trust-store-type", "tls.trust-store-type"),
      Map.entry("insecure-certificate-verification", "tls.verify")
  );
  private static final Set<String> S7_COTP_OPTIONS = Set.of(
      "local-rack", "local-slot", "local-device-group", "remote-rack", "remote-slot", "remote-device-group",
      "local-tsap", "remote-tsap"
  );
  private static final Set<String> COUNT_ARRAY_PROTOCOLS = Set.of(
      "s7", "modbus-tcp", "modbus-rtu", "modbus-ascii", "ads", "slmp", "profinet", "profinet-ng", "simulated"
  );
  private static final Pattern COUNT_ARRAY = Pattern.compile("^(.+):([A-Z][A-Z0-9_]*(?:\\(\\d+\\))?)\\[(\\d+)\\]$");
  private static final Pattern UNTYPED_MODBUS_ARRAY = Pattern.compile("^((?:[a-z-]+:)?\\d+)\\[(\\d+)\\]$");
  private static final Pattern EIP_ARRAY = Pattern.compile("^(.+?)(?:\\[(\\d+)\\])?:([A-Z][A-Z0-9_]*):(\\d+)$");

  private Plc4xV1Configuration() {
  }

  public static String optionName(String protocol, String key) {
    if ("s7".equals(protocol) && S7_COTP_OPTIONS.contains(key)) {
      return "cotp." + key;
    }
    if ("s7".equals(protocol) && key.startsWith("tcp.")) {
      key = "cotp." + key.substring(4);
    }
    var dot = key.indexOf('.');
    var prefix = dot < 0 ? "" : key.substring(0, dot + 1);
    var name = dot < 0 ? key : key.substring(dot + 1);
    var renamed = RENAMED_OPTIONS.getOrDefault(name, name);
    return renamed.contains(".") ? renamed : prefix + renamed;
  }

  public static String optionValue(String key, String value) {
    if (key.endsWith("insecure-certificate-verification") && value != null && !value.isBlank()) {
      if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
        throw new IllegalArgumentException("Invalid certificate verification setting");
      }
      return Boolean.toString(!Boolean.parseBoolean(value));
    }
    return value;
  }

  public static String host(String protocol, String host) {
    var query = host.indexOf('?');
    if (query < 0) {
      return host;
    }
    var parameters = host.substring(query + 1).split("&", -1);
    for (int i = 0; i < parameters.length; i++) {
      var pair = parameters[i].split("=", 2);
      parameters[i] = optionName(protocol, pair[0])
          + (pair.length == 2 ? "=" + optionValue(pair[0], pair[1]) : "");
    }
    return host.substring(0, query + 1) + String.join("&", parameters);
  }

  public static String tag(String protocol, String address) {
    if (Set.of("modbus-tcp", "modbus-rtu", "modbus-ascii").contains(protocol)) {
      var matcher = UNTYPED_MODBUS_ARRAY.matcher(address);
      if (matcher.matches()) {
        long count = positiveCount(matcher.group(2));
        return matcher.group(1) + (count == 1 ? "" : "[0.." + (count - 1) + "]");
      }
    }
    if (COUNT_ARRAY_PROTOCOLS.contains(protocol)) {
      var matcher = COUNT_ARRAY.matcher(address);
      if (matcher.matches()) {
        long count = positiveCount(matcher.group(3));
        // The old count-one form yielded a scalar; a range of one now yields a list.
        return matcher.group(1) + (count == 1 ? "" : "[0.." + (count - 1) + "]") + ":" + matcher.group(2);
      }
    } else if ("eip".equals(protocol)) {
      var matcher = EIP_ARRAY.matcher(address);
      if (matcher.matches()) {
        long start = matcher.group(2) == null ? 0 : Long.parseLong(matcher.group(2));
        long count = positiveCount(matcher.group(4));
        var selection = count == 1 ? "[" + start + "]" : "[" + start + ".." + Math.addExact(start, count - 1) + "]";
        return matcher.group(1) + selection + ":" + matcher.group(3);
      }
    } else if ("firmata".equals(protocol)) {
      var matcher = Pattern.compile("^(.+)\\[(\\d+)\\]$").matcher(address);
      if (matcher.matches()) {
        long count = positiveCount(matcher.group(2));
        return matcher.group(1) + (count == 1 ? "[0]" : "[0.." + (count - 1) + "]");
      }
    }
    return address;
  }

  private static long positiveCount(String count) {
    long value = Long.parseLong(count);
    if (value < 1 || value > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Invalid PLC array element count");
    }
    return value;
  }

  public static String code(String protocol, String code) {
    if (code == null) {
      return null;
    }
    return Pattern.compile("(?m)^(\\s*\\w+\\s*=)([^\\r\\n]+)").matcher(code).replaceAll(match -> {
      String value = match.group(2);
      int comment = value.indexOf("//");
      String address = comment < 0 ? value : value.substring(0, comment);
      String migrated = tag(protocol, address.trim());
      return java.util.regex.Matcher.quoteReplacement(
          match.group(1) + value.replace(address.trim(), migrated)
      );
    });
  }
}
