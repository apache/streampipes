/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sources.random.config;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum SourcesConfig implements PeConfig {
  INSTANCE;

  private SpConfig config;

  public final static String serverUrl;
  public final static String iconBaseUrl;

  private final static String SERVICE_ID = "pe/org.streampipes.sources.random";

  SourcesConfig() {
    config = SpConfig.getSpConfig(SERVICE_ID);
    config.register(ConfigKeys.HOST, "sources-random-data-generator", "Hostname for the pe " +
            "sources samples");
    config.register(ConfigKeys.PORT, 8090, "Port for the pe sources samples");
    config.register(ConfigKeys.KAFKA_HOST, "kafka", "Host for kafka of the pe sources samples project");
    config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for kafka of the pe sources samples project");
    config.register(ConfigKeys.ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe sources samples project");
    config.register(ConfigKeys.ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe sources samples project");
    config.register(ConfigKeys.JMS_HOST, "activemq", "Hostname for pe sources samples service for active mq");
    config.register(ConfigKeys.JMS_PORT, 9092, "Port for pe sources samples service for active mq");
    config.register(ConfigKeys.SIMULATION_DELAY_MS, 10, "Time for the simulation in milliseconds");
    config.register(ConfigKeys.SIMULATION_DELAY_NS, 0, "Time for the simulation in nanoseconds");
    config.register(ConfigKeys.SIMULATION_MAX_EVENTS, 105000, "Amount of maximum events");
    config.register(ConfigKeys.SIMULATION_WAIT_EVERY, 1, "Time to wait interval");
    config.register(ConfigKeys.SIMULATION_WAIT_FOR, 800, "Time for the simulation to wait for in milliseconds");

    config.register(ConfigKeys.ICON_HOST, "backend", "Hostname for the icon host");
    config.register(ConfigKeys.ICON_PORT, 80, "Port for the icons in nginx");

    config.register(ConfigKeys.SERVICE_NAME, "Sources samples", "The name of the service");

  }


  static {
    serverUrl = SourcesConfig.INSTANCE.getHost() + ":" + SourcesConfig.INSTANCE.getPort();
    iconBaseUrl = "http://" + SourcesConfig.INSTANCE.getIconHost() + ":" + SourcesConfig.INSTANCE.getIconPort() + "/assets/img/pe_icons";
  }

  @Override
  public String getHost() {
    return config.getString(ConfigKeys.HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(ConfigKeys.PORT);
  }

  public String getKafkaHost() {
    return config.getString(ConfigKeys.KAFKA_HOST);
  }

  public int getKafkaPort() {
    return config.getInteger(ConfigKeys.KAFKA_PORT);
  }

  public String getKafkaUrl() {
    return getKafkaHost() + ":" + getKafkaPort();
  }

  public String getZookeeperHost() {
    return config.getString(ConfigKeys.ZOOKEEPER_HOST);
  }

  public int getZookeeperPort() {
    return config.getInteger(ConfigKeys.ZOOKEEPER_PORT);
  }

  public String getJmsHost() {
    return config.getString(ConfigKeys.JMS_HOST);
  }

  public int getJmsPort() {
    return config.getInteger(ConfigKeys.JMS_PORT);
  }

  public int getSimulaitonDelayMs() {
    return config.getInteger(ConfigKeys.SIMULATION_DELAY_MS);
  }

  public int getSimulaitonDelayNs() {
    return config.getInteger(ConfigKeys.SIMULATION_DELAY_NS);
  }

  public int getMaxEvents() {
    return config.getInteger(ConfigKeys.SIMULATION_MAX_EVENTS);
  }

  public int getSimulationWaitEvery() {
    return config.getInteger(ConfigKeys.SIMULATION_WAIT_EVERY);
  }

  public int getSimulationWaitFor() {
    return config.getInteger(ConfigKeys.SIMULATION_WAIT_FOR);
  }

  public String getIconHost() {
    return config.getString(ConfigKeys.ICON_HOST);
  }

  public int getIconPort() {
    return config.getInteger(ConfigKeys.ICON_PORT);
  }

  @Override
  public String getId() {
    return SERVICE_ID;
  }

  @Override
  public String getName() {
    return config.getString(ConfigKeys.SERVICE_NAME);
  }

}
