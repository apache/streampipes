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

package org.streampipes.connect.config;


import org.streampipes.config.SpConfig;

public enum ConnectContainerConfig {
  INSTANCE;

  private SpConfig config;
  private final static String KAFKA_HOST = "kafka_host";
  private final static String KAFKA_PORT = "kafka_port";

  ConnectContainerConfig() {
    config = SpConfig.getSpConfig("connect-container");

    config.register(KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
    config.register(KAFKA_PORT, 9092, "Port for backend service for kafka");

  }


  public String getKafkaHost() {
    return config.getString(KAFKA_HOST);
  }

  public int getKafkaPort() {
    return config.getInteger(KAFKA_PORT);
  }

  public String getKafkaUrl() {
    return getKafkaHost() + ":" + getKafkaPort();
  }

  public void setKafkaHost(String s) {
    config.setString(KAFKA_HOST, s);
  }

}
