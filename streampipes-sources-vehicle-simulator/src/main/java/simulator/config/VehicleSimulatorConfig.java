/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package simulator.config;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum VehicleSimulatorConfig implements PeConfig {
  INSTANCE;


  /*
     TUTORIAL:
     The service ID MUST be unique. As a convention use the package name with the prefix "pe/" for processing element.
   */
  private final static String SERVICE_ID = "pe/org.streampipes.pe.sources.examples";


  private SpConfig config;
  public static String serverUrl;
  public static String iconBaseUrl;

  VehicleSimulatorConfig() {
    config = SpConfig.getSpConfig(SERVICE_ID);

    /*
      TUTORIAL:
      The second parameter is the default value for the configuration property.
      This value is set in Consul when the parameter does not exist.
      Important. Changes here are not effective if the configuration parameter is already set in consul. In
      such cases the value has to be changed in consul directly.
    */
    config.register(ConfigKeys.HOST, "pe-examples-sources", "Hostname for the examples-sources project");
    config.register(ConfigKeys.PORT, 8090, "Port of the sources project");
    config.register(ConfigKeys.KAFKA_HOST, "kafka", "Host for kafka of the pe demonstrator project");
    config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for kafka of the pe demonstrator project");
    config.register(ConfigKeys.ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe demonstrator project");
    config.register(ConfigKeys.ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe demonstrator project");
    config.register(ConfigKeys.ICON_HOST, "backend", "Hostname for the icon host");
    config.register(ConfigKeys.ICON_PORT, 80, "Port for the icons in nginx");
    config.register(ConfigKeys.SERVICE_NAME, "Example Sources", "StreamPipes example sources");
  }

  static {
    serverUrl = VehicleSimulatorConfig.INSTANCE.getHost() + ":" + VehicleSimulatorConfig.INSTANCE.getPort();

    iconBaseUrl = "http://" + VehicleSimulatorConfig.INSTANCE.getIconHost() + ":" + VehicleSimulatorConfig.INSTANCE.getIconPort() + "/assets/img/pe_icons";
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
