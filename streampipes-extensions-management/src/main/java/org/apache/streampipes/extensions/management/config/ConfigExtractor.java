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
package org.apache.streampipes.extensions.management.config;

import org.apache.streampipes.extensions.api.config.IConfigExtractor;
import org.apache.streampipes.extensions.management.client.StreamPipesClientResolver;
import org.apache.streampipes.model.extensions.configuration.ConfigItem;
import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;

public class ConfigExtractor implements IConfigExtractor {

  private final SpServiceConfiguration serviceConfig;

  private ConfigExtractor(SpServiceConfiguration serviceConfig) {
    this.serviceConfig = serviceConfig;
  }

  public static ConfigExtractor from(String serviceGroup) {
    var client = new StreamPipesClientResolver().makeStreamPipesClientInstance();
    var serviceConfig = client.adminApi().getServiceConfiguration(serviceGroup);
    return new ConfigExtractor(serviceConfig);
  }

  public static ConfigExtractor from(SpServiceConfiguration serviceConfig) {
    return new ConfigExtractor(serviceConfig);
  }

  @Override
  public boolean getBoolean(String key) {
    return Boolean.parseBoolean(getString(key));
  }

  @Override
  public int getInteger(String key) {
    return Integer.parseInt(getString(key));
  }

  @Override
  public double getDouble(String key) {
    return Double.parseDouble(getString(key));
  }

  @Override
  public String getString(String key) {
    return getItem(key).getValue();
  }

  @Override
  public <T> T getObject(String key, Class<T> clazz, T defaultValue) {
    return null;
  }

  @Override
  public ConfigItem getConfigItem(String key) {
    return getItem(key);
  }

  private ConfigItem getItem(String key) {
    return serviceConfig.getConfigs()
        .stream()
        .filter(c -> c.getKey().equals(key))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
