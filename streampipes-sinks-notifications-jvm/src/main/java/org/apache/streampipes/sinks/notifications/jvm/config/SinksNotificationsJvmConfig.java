/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.sinks.notifications.jvm.config;


import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.SpConfig;
import org.apache.streampipes.container.model.PeConfig;

public enum SinksNotificationsJvmConfig implements PeConfig {
  INSTANCE;

  private SpConfig config;

  public final static String serverUrl;

  private final static String service_id = "pe/org.apache.streampipes.sinks.notifications.jvm";
  private final static String service_name = "Sinks Notifications JVM";
  private final static String service_container_name = "sinks-notifications-jvm";


  SinksNotificationsJvmConfig() {
    config = SpServiceDiscovery.getSpConfig(service_id);
    config.register(ConfigKeys.HOST, service_container_name, "Hostname for the notifications module");
    config.register(ConfigKeys.PORT, 8090, "Port for the pe esper");

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
  }

  @Override
  public String getHost() {
    return config.getString(ConfigKeys.HOST);
  }

  @Override
  public int getPort() {
    return config.getInteger(ConfigKeys.PORT);
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
