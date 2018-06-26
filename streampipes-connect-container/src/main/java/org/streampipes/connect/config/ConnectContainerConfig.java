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

  ConnectContainerConfig() {
    config = SpConfig.getSpConfig("connect-container");

    config.register(ConfigKeys.BACKEND_HOST, "backend", "Hostname for backend");
    config.register(ConfigKeys.BACKEND_PORT, 8030, "Port for backend");

    config.register(ConfigKeys.KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
    config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for backend service for kafka");

  }

  public String getBackendApiUrl() {
    return config.getString(ConfigKeys.BACKEND_HOST) + ":" + config.getInteger(ConfigKeys.BACKEND_PORT) + "/streampipes-backend/";
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

  public void setKafkaHost(String s) {
    config.setString(ConfigKeys.KAFKA_HOST, s);
  }

}
