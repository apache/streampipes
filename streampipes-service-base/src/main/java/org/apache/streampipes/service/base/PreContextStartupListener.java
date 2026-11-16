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
package org.apache.streampipes.service.base;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.PropertiesPropertySource;

import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Has no explicit order on purpose: it must run after Spring Boot's LoggingApplicationListener,
 * which handles the same event at a high precedence.
 */
class PreContextStartupListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  private static final String PROPERTY_SOURCE_NAME = "streampipesServiceConfig";

  private final PreContextStartup startup;

  PreContextStartupListener(PreContextStartup startup) {
    this.startup = startup;
  }

  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    BaseNetworkingConfig networkingConfig;
    try {
      networkingConfig = startup.run();
    } catch (UnknownHostException e) {
      throw new IllegalStateException(
          "Could not auto-resolve host address - please manually provide the hostname"
              + " using the `SP_HOST` environment variable", e);
    }

    Properties config = new Properties();
    ServiceBaseConfig.addPrometheusConfig(config);
    ServiceBaseConfig.addPortConfig(networkingConfig.getPort(), config);

    event.getEnvironment().getPropertySources().addLast(new PropertiesPropertySource(PROPERTY_SOURCE_NAME, config));
  }
}
