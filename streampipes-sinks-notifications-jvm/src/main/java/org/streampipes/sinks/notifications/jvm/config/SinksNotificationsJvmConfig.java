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

package org.streampipes.sinks.notifications.jvm.config;


import org.streampipes.config.SpConfig;
import org.streampipes.container.model.PeConfig;

public enum SinksNotificationsJvmConfig implements PeConfig {
  INSTANCE;

  private SpConfig config;

  public final static String serverUrl;
  public final static String iconBaseUrl;

  private final static String service_id = "pe/org.streampipes.sinks.notifications.jvm";
  private final static String service_name = "Sinks Notifications JVM";
  private final static String service_container_name = "sinks-notifications-jvm";


  SinksNotificationsJvmConfig() {
    config = SpConfig.getSpConfig(service_id);
    config.register(ConfigKeys.HOST, service_container_name, "Hostname for the notifications module");
    config.register(ConfigKeys.PORT, 8090, "Port for the pe esper");

    config.register(ConfigKeys.ICON_HOST, "backend", "Hostname for the icon host");
    config.register(ConfigKeys.ICON_PORT, 80, "Port for the icons in nginx");
    config.register(ConfigKeys.NGINX_HOST, System.getenv("STREAMPIPES_HOST"), "External hostname of " +
            "StreamPipes Nginx");
    config.register(ConfigKeys.NGINX_PORT, 80, "External port of StreamPipes Nginx");
    config.register(ConfigKeys.KAFKA_HOST, "kafka", "Host for kafka of the pe sinks project");
    config.register(ConfigKeys.KAFKA_PORT, 9092, "Port for kafka of the pe sinks project");
    config.register(ConfigKeys.ZOOKEEPER_HOST, "zookeeper", "Host for zookeeper of the pe sinks project");
    config.register(ConfigKeys.ZOOKEEPER_PORT, 2181, "Port for zookeeper of the pe sinks project");
    config.register(ConfigKeys.COUCHDB_HOST, "couchdb", "Host for couchdb of the pe sinks project");
    config.register(ConfigKeys.COUCHDB_PORT, 5984, "Port for couchdb of the pe sinks project");
    config.register(ConfigKeys.JMS_HOST, "tcp://activemq", "Hostname for pe actions service for active mq");
    config.register(ConfigKeys.JMS_PORT, 61616, "Port for pe actions service for active mq");

    config.register(ConfigKeys.SLACK_TOKEN, ConfigKeys.SLACK_NOT_INITIALIZED, "Token for the slack bot. Must be generated in slack");

    config.register(ConfigKeys.EMAIL_FROM, "", "The Email adress which send from");
    config.register(ConfigKeys.EMAIL_USERNAME, "", "The username of the email account");
    config.registerPassword(ConfigKeys.EMAIL_PASSWORD, "", "The password of the email account");
    config.register(ConfigKeys.EMAIL_SMTP_HOST, "", "The SMTP Host");
    config.register(ConfigKeys.EMAIL_SMTP_PORT, "", "The SMTP Port");
    config.register(ConfigKeys.EMAIL_STARTTLS, false, "Use startls?");
    config.register(ConfigKeys.EMAIL_SLL, false, "Use SLL?");
    config.register(ConfigKeys.WEBSOCKET_PROTOCOL, "ws", "");

    config.register(ConfigKeys.SERVICE_NAME, service_name, "The name of the service");

  }

  static {
    serverUrl = SinksNotificationsJvmConfig.INSTANCE.getHost() + ":" + SinksNotificationsJvmConfig.INSTANCE.getPort();
    iconBaseUrl = "http://" + SinksNotificationsJvmConfig.INSTANCE.getIconHost() + ":" + SinksNotificationsJvmConfig.INSTANCE.getIconPort() + "/assets/img/pe_icons";
  }

  public static final String getIconUrl(String pictureName) {
    return iconBaseUrl + "/" + pictureName + ".png";
  }

  @Override
  public String getHost() {
    return config.getString(ConfigKeys.HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(ConfigKeys.PORT);
  }

  public String getIconHost() {
    return config.getString(ConfigKeys.ICON_HOST);
  }

  public int getIconPort() {
    return config.getInteger(ConfigKeys.ICON_PORT);
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


  public String getSlackToken() {
    return config.getString(ConfigKeys.SLACK_TOKEN);
  }

  public String getEmailFrom() {
    return config.getString(ConfigKeys.EMAIL_FROM);
  }

  public String getEmailUsername() {
    return config.getString(ConfigKeys.EMAIL_USERNAME);
  }

  public String getEmailPassword() {
    return config.getString(ConfigKeys.EMAIL_PASSWORD);
  }

  public String getEmailSmtpHost() {
    return config.getString(ConfigKeys.EMAIL_SMTP_HOST);
  }

  public int getEmailSmtpPort() {
    return config.getInteger(ConfigKeys.EMAIL_SMTP_PORT);
  }

  public boolean useEmailStarttls() {
    return config.getBoolean(ConfigKeys.EMAIL_STARTTLS);
  }

  public boolean useEmailSll() {
    return config.getBoolean(ConfigKeys.EMAIL_SLL);
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
