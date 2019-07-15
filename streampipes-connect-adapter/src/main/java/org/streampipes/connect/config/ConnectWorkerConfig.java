/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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
import org.streampipes.connect.init.Config;

import java.util.UUID;

public enum ConnectWorkerConfig {
  INSTANCE;

  private SpConfig config;

  ConnectWorkerConfig() {
    config = SpConfig.getSpConfig("worker-connect-container");

    config.register(ConfigKeys.KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
    config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for backend service for kafka");

    String defaultName = "connect-worker." + UUID.randomUUID();
    config.register(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT, Config.WORKER_PORT, "The port of the connect container");
    config.register(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST, defaultName, "The hostname of the connect container");

    config.register(ConfigKeys.DATA_LOCATION,"/data/", "Folder that stores all the uploaded data");

  }

  public String getConnectContainerWorkerUrl() {
    return "http://" + config.getString(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST) + ":" + config.getInteger(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT) + "/";
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


  public String getConnectContainerWorkerHost() {
    return config.getString(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST);
  }

  public Integer getConnectContainerWorkerPort() {
    return config.getInteger(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT);
  }

  public String getDataLocation() {
    return config.getString(ConfigKeys.DATA_LOCATION);
  }




}
