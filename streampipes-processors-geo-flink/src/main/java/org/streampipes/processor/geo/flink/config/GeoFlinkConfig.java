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

package org.streampipes.processor.geo.flink.config;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum GeoFlinkConfig implements PeConfig {
  INSTANCE;

  private SpConfig config;
  public static final String JAR_FILE = "./streampipes-processing-element-container.jar";

  private final static String service_id = "pe/org.streampipes.processors.geo.flink";
  private final static String service_name = "Processors Geo Flink";
  private final static String service_container_name = "processors-geo-flink";
  GeoFlinkConfig() {
    config = SpConfig.getSpConfig(service_id);

    config.register(ConfigKeys.HOST, service_container_name, "Hostname for the geo flink component");
    config.register(ConfigKeys.PORT, 8090, "Port for the geo flink component");
    config.register(ConfigKeys.FLINK_HOST, "jobmanager", "Host for the flink cluster");
    config.register(ConfigKeys.FLINK_PORT, 8081, "Port for the flink cluster");
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

  public static final String iconBaseUrl = "http://" + GeoFlinkConfig.INSTANCE.getIconHost() + ":" + GeoFlinkConfig.INSTANCE.getIconPort() + "/assets/img/pe_icons";

  public static final String getIconUrl(String pictureName) {
    return iconBaseUrl + "/" + pictureName + ".png";
  }

  public String getIconHost() {
    return config.getString(ConfigKeys.ICON_HOST);
  }

  public int getIconPort() {
    return config.getInteger(ConfigKeys.ICON_PORT);
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
