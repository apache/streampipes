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

package org.apache.streampipes.extensions.connectors.kafka.shared.kafka;

import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;

import java.util.List;

/**
 * Turns the two configurations an adapter or sink used to hold a broker in into a single broker
 * list. A stored sink holding {@code host=broker1} and {@code port=9094} ends up holding
 * {@code bootstrap-servers=broker1:9094} instead, with both former configurations gone.
 */
public class KafkaBootstrapServersMerger {

  /*
   * The names of the former configurations were stored under.
   * Stored adapters and sinks may still refer to them.
   */
  private static final String OLD_HOST_KEY = "host";
  private static final String OLD_PORT_KEY = "port";

  private static final String DEFAULT_PORT = "9092";

  private KafkaBootstrapServersMerger() {}

  /**
   * Replace the former host and port with a single configuration holding both, written as
   * {@code host:port}.
   *
   * @param staticProperties the configurations of a stored adapter or sink.
   * @return {@code true} if the former host was found and both configurations were merged.
   * Otherwise, {@code false}.
   */
  public static boolean merge(List<StaticProperty> staticProperties) {
    int hostIndex = indexOfFormerHost(staticProperties);
    var isMerged = hostIndex >= 0;

    if (isMerged) {
      var host = readConfiguredValue(staticProperties, OLD_HOST_KEY, "");
      var port = readConfiguredValue(staticProperties, OLD_PORT_KEY, DEFAULT_PORT);

      staticProperties.removeIf(KafkaBootstrapServersMerger::isFormerBrokerConfiguration);

      staticProperties.add(hostIndex, StaticProperties.stringFreeTextProperty(
          KafkaConfigProvider.getBootstrapServersLabel(),
          host + ":" + port));
    }

    return isMerged;
  }

  /**
   * Look up where the former host sits, so that the merged broker can take its place.
   *
   * @param staticProperties the configurations of a stored adapter or sink.
   * @return the position of the configuration, or {@code -1} if it does not exist.
   */
  private static int indexOfFormerHost(List<StaticProperty> staticProperties) {
    for (int i = 0; i < staticProperties.size(); i++) {
      if (OLD_HOST_KEY.equals(staticProperties.get(i).getInternalName())) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Read what a user had entered for a configuration.
   *
   * @param staticProperties the configurations of a stored adapter or sink.
   * @param internalName the name of the configuration to read.
   * @param defaultValue the value to fall back to if nothing was entered.
   * @return the entered value without surrounding whitespace, or the default value.
   */
  private static String readConfiguredValue(List<StaticProperty> staticProperties,
                                            String internalName,
                                            String defaultValue) {
    return staticProperties
        .stream()
        .filter(staticProperty -> internalName.equals(staticProperty.getInternalName()))
        .filter(FreeTextStaticProperty.class::isInstance)
        .map(staticProperty -> ((FreeTextStaticProperty) staticProperty).getValue())
        .filter(value -> value != null && !value.isBlank())
        .map(String::trim)
        .findFirst()
        .orElse(defaultValue);
  }

  /**
   * Check whether a configuration held part of the former broker, either its host or its port.
   *
   * @param staticProperty a single configuration of a stored adapter or sink.
   * @return {@code true} if the configuration is the former host or port. Otherwise, {@code false}.
   */
  private static boolean isFormerBrokerConfiguration(StaticProperty staticProperty) {
    return OLD_HOST_KEY.equals(staticProperty.getInternalName())
        || OLD_PORT_KEY.equals(staticProperty.getInternalName());
  }
}
