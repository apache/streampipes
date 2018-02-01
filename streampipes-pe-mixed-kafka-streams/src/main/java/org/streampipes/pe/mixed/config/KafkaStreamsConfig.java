/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.mixed.config;

import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum KafkaStreamsConfig implements PeConfig {
  INSTANCE;

  private SpConfig config;
  private final static String HOST = "host";
  private final static String PORT = "port";

  private final static String ICON_HOST = "icon_host";
  private final static String ICON_PORT = "icon_port";

  private final static String SERVICE_ID = "pe/org.streampipes.pe.mixed.kafka";
  private final static String SERVICE_NAME = "pe-kafka-streams-samples";

  KafkaStreamsConfig() {
    config = SpConfig.getSpConfig(SERVICE_ID);

    config.register(HOST, "pe-kafka-streams-samples", "Hostname for the pe mixed kafka streams component");
    config.register(PORT, 8088, "Port for the pe mixed flink component");

    config.register(ICON_HOST, "backend", "Hostname for the icon host");
    config.register(ICON_PORT, 80, "Port for the icons in nginx");

    config.register(SERVICE_NAME, "Mixed flink", "The name of the service");

  }

  @Override
  public String getHost() {
    return config.getString(HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(PORT);
  }

  public static final String iconBaseUrl = KafkaStreamsConfig.INSTANCE.getIconHost() + ":" + KafkaStreamsConfig.INSTANCE.getIconPort
          () +
          "/img/pe_icons";

  public static final String getIconUrl(String pictureName) {
    return iconBaseUrl + "/" + pictureName + ".png";
  }

  public String getIconHost() {
    return config.getString(ICON_HOST);
  }

  public int getIconPort() {
    return config.getInteger(ICON_PORT);
  }

  @Override
  public String getId() {
    return SERVICE_ID;
  }

  @Override
  public String getName() {
    return config.getString(SERVICE_NAME);
  }
}