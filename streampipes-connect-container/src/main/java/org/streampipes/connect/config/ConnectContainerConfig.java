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
import org.streampipes.connect.init.Config;

public enum ConnectContainerConfig {
  INSTANCE;

  private SpConfig config;

  ConnectContainerConfig() {
    config = SpConfig.getSpConfig("connect-container");

    config.register(ConfigKeys.BACKEND_HOST, "backend", "Hostname for backend");
    config.register(ConfigKeys.BACKEND_PORT, 8030, "Port for backend");

    config.register(ConfigKeys.KAFKA_HOST, "kafka", "Hostname for backend service for kafka");
    config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for backend service for kafka");

    config.register(ConfigKeys.CONNECT_CONTAINER_MASTER_PORT, Config.MASTER_PORT, "The port of the connect container");
    config.register(ConfigKeys.CONNECT_CONTAINER_MASTER_HOST, "connect-master", "The hostname of the connect container");

    config.register(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT, Config.WORKER_PORT, "The port of the connect container");
    config.register(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST, "connect-worker", "The hostname of the connect container");

    config.register(ConfigKeys.DATA_LOCATION,"/data/", "Folder that stores all the uploaded data");

  }

  public String getBackendApiUrl() {
    return config.getString(ConfigKeys.BACKEND_HOST) + ":" + config.getInteger(ConfigKeys.BACKEND_PORT) + "/streampipes-backend/";
  }

  public String getConnectContainerWorkerUrl() {
    return "http://" + config.getString(ConfigKeys.CONNECT_CONTAINER_WORKER_HOST) + ":" + config.getInteger(ConfigKeys.CONNECT_CONTAINER_WORKER_PORT) + "/";
  }

  public String getConnectContainerMasterUrl() {
    return "http://" + getConnectContainerMasterHost() + ":" + getConnectContainerMasterPort() + "/";
  }

  public String getBackendHost() {
    return config.getString(ConfigKeys.BACKEND_HOST);
  }

  public int getBackendPort() {
    return config.getInteger(ConfigKeys.BACKEND_PORT);
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

  public String getConnectContainerMasterHost() {
    return config.getString(ConfigKeys.CONNECT_CONTAINER_MASTER_HOST);
  }

  public Integer getConnectContainerMasterPort() {
    return config.getInteger(ConfigKeys.CONNECT_CONTAINER_MASTER_PORT);
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
