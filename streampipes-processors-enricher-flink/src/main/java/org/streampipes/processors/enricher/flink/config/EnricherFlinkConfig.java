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

package org.streampipes.processors.enricher.flink.config;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum EnricherFlinkConfig implements PeConfig {
  INSTANCE;

  private SpConfig config;
  public static final String JAR_FILE = "./streampipes-examples-flink.jar";

  private final static String service_id = "pe/org.streampipes.processors.enricher.flink";
  private final static String service_name = "Processors Enricher Flink";
  private final static String service_container_name = "processors-enricher-flink";
  EnricherFlinkConfig() {
    config = SpConfig.getSpConfig(service_id);

    config.register(ConfigKeys.HOST, service_container_name, "Hostname for the pe mixed flink component");
    config.register(ConfigKeys.PORT, 8090, "Port for the pe mixed flink component");
    config.register(ConfigKeys.FLINK_HOST, "jobmanager", "Host for the flink cluster");
    config.register(ConfigKeys.FLINK_PORT, 6123, "Port for the flink cluster");
    config.register(ConfigKeys.ELASTIC_HOST, "elasticsearch", "Elastic search host address");
    config.register(ConfigKeys.ELASTIC_PORT, 9300, "Elasitc search port");
    config.register(ConfigKeys.ELASTIC_PORT_REST, 9200, "Elasitc search rest port");

    config.register(ConfigKeys.ICON_HOST, "backend", "Hostname for the icon host");
    config.register(ConfigKeys.ICON_PORT, 80, "Port for the icons in nginx");

    config.register(ConfigKeys.DEBUG, false, "When set to true programs are not deployed to cluster, but executed locally");

    config.register(ConfigKeys.SERVICE_NAME, service_name, "The name of the service");

  }

  @Override
  public String getHost() {
    return config.getString(ConfigKeys.HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(ConfigKeys.PORT);
  }

  public String getFlinkHost() {
    return config.getString(ConfigKeys.FLINK_HOST);
  }

  public int getFlinkPort() {
    return config.getInteger(ConfigKeys.FLINK_PORT);
  }

  public String getElasticsearchHost() {
    return config.getString(ConfigKeys.ELASTIC_HOST);
  }

  public int getElasticsearchPort() {
    return config.getInteger(ConfigKeys.ELASTIC_PORT);
  }


  public static final String iconBaseUrl = "http://" + EnricherFlinkConfig.INSTANCE.getIconHost() + ":" + EnricherFlinkConfig.INSTANCE.getIconPort() + "/assets/img/pe_icons";

  public static final String getIconUrl(String pictureName) {
    return iconBaseUrl + "/" + pictureName + ".png";
  }

  public String getIconHost() {
    return config.getString(ConfigKeys.ICON_HOST);
  }

  public int getIconPort() {
    return config.getInteger(ConfigKeys.ICON_PORT);
  }

  public int getElasticsearchPortRest() {
    return config.getInteger(ConfigKeys.ELASTIC_PORT_REST);
  }

  public boolean getDebug() {
    return config.getBoolean(ConfigKeys.DEBUG);
  }

  @Override
  public String getId() {
    return service_id;
  }

  @Override
  public String getName() {
    return config.getString(ConfigKeys.SERVICE_NAME);
  }
}
