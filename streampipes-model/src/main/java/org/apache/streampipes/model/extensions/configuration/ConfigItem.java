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

package org.apache.streampipes.model.extensions.configuration;

import java.util.Objects;

public class ConfigItem {

  private String key;
  private String description;
  private String value;
  private String valueType;
  private ConfigurationScope configurationScope;
  private boolean isPassword;

  public ConfigItem() {
    setPassword(false);
  }

  public static <T> ConfigItem from(String key,
                                    T defaultValue,
                                    String description) {
    return from(key, defaultValue, description, ConfigurationScope.CONTAINER_STARTUP_CONFIG, false);
  }

  public static <T> ConfigItem from(String key,
                                    T defaultValue,
                                    String description,
                                    ConfigurationScope configurationScope) {
    return from(key, defaultValue, description, configurationScope, false);
  }

  public static <T> ConfigItem from(String key,
                                    T defaultValue,
                                    String description,
                                    ConfigurationScope configurationScope,
                                    boolean isPassword) {
    return from(key, defaultValue, description, ConfigItemUtils.getValueType(defaultValue), configurationScope,
        isPassword);
  }

  public static <T> ConfigItem from(String key,
                                    T defaultValue,
                                    String description,
                                    String valueType,
                                    ConfigurationScope configurationScope,
                                    boolean isPassword) {
    ConfigItem configItem = new ConfigItem();
    configItem.setKey(key);
    configItem.setValue(String.valueOf(defaultValue));
    configItem.setValueType(valueType);
    configItem.setDescription(description);
    configItem.setConfigurationScope(configurationScope);
    configItem.setPassword(isPassword);

    return configItem;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValueType() {
    return valueType;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public boolean isPassword() {
    return isPassword;
  }

  public void setPassword(boolean state) {
    isPassword = state;
  }

  public ConfigurationScope getConfigurationScope() {
    return configurationScope;
  }

  public void setConfigurationScope(ConfigurationScope configurationScope) {
    this.configurationScope = configurationScope;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfigItem that = (ConfigItem) o;
    return isPassword == that.isPassword
        && Objects.equals(key, that.key)
        && Objects.equals(description, that.description)
        && Objects.equals(value, that.value)
        && Objects.equals(valueType, that.valueType)
        && configurationScope == that.configurationScope;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, description, value, valueType, configurationScope, isPassword);
  }
}
