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

package org.apache.streampipes.svcdiscovery.consul;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.model.extensions.configuration.ConfigItem;
import org.apache.streampipes.model.extensions.configuration.ConfigItemUtils;
import org.apache.streampipes.model.extensions.configuration.ConfigurationScope;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.SpConfig;

import com.ecwid.consul.v1.ConsulClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulSpConfig extends AbstractConsulService implements SpConfig {

  public static final String SERVICE_ROUTE_PREFIX = "sp/v1/";
  private static final Logger LOG = LoggerFactory.getLogger(ConsulSpConfig.class);
  private static final String SLASH = "/";
  private final String serviceName;
  private final ConsulClient kvClient;

  public ConsulSpConfig(String serviceName,
                        Environment environment) {
    super(environment);
    this.kvClient = consulInstance();
    this.serviceName = serviceName;
  }

  @Override
  public <T> void register(String key, T defaultValue, String description, ConfigurationScope configurationScope) {
    register(key, String.valueOf(defaultValue), ConfigItemUtils.getValueType(defaultValue), description,
        configurationScope, false);
  }

  @Override
  public void register(String key, boolean defaultValue, String description) {
    register(key, Boolean.toString(defaultValue), "xs:boolean", description,
        ConfigurationScope.CONTAINER_STARTUP_CONFIG, false);
  }

  @Override
  public void register(String key, int defaultValue, String description) {
    register(key, Integer.toString(defaultValue), "xs:integer", description,
        ConfigurationScope.CONTAINER_STARTUP_CONFIG, false);
  }

  @Override
  public void register(String key, double defaultValue, String description) {
    register(key, Double.toString(defaultValue), "xs:double", description, ConfigurationScope.CONTAINER_STARTUP_CONFIG,
        false);

  }

  @Override
  public void register(String key, String defaultValue, String description) {
    register(key, defaultValue, "xs:string", description, ConfigurationScope.CONTAINER_STARTUP_CONFIG, false);
  }

  @Override
  public void registerObject(String key, Object defaultValue, String description) {
    var i = kvClient.getKVValue(addSn(key));
    if (i.getValue() == null) {
      kvClient.setKVValue(addSn(key), toJson(defaultValue));
    }
  }

  @Override
  public void registerPassword(String key, String defaultValue, String description) {
    register(key, defaultValue, "xs:string", description, ConfigurationScope.CONTAINER_STARTUP_CONFIG, true);
  }

  @Override
  public void register(ConfigItem configItem) {
    String key = addSn(configItem.getKey());
    var i = kvClient.getKVValue(key);

    if (i.getValue() == null) {
      // Set the value of environment variable as default
      String envVariable = System.getenv(configItem.getKey());
      if (envVariable != null) {
        configItem.setValue(envVariable);
        kvClient.setKVValue(key, toJson(configItem));
      } else {
        kvClient.setKVValue(key, toJson(configItem));
      }
    }
  }

  private void register(String key,
                        String defaultValue,
                        String valueType,
                        String description,
                        ConfigurationScope configurationScope,
                        boolean isPassword) {
    ConfigItem configItem = ConfigItem.from(key, defaultValue, description, valueType, configurationScope, isPassword);
    register(configItem);
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
    return getConfigItem(key).getValue();
  }

  @Override
  public boolean exists(String key) {
    var os = kvClient.getKVValue(addSn(key)).getValue();
    return os != null;
  }

  @Override
  public <T> T getObject(String key, Class<T> clazz, T defaultValue) {
    var os = kvClient.getKVValue(addSn(key));
    if (os.getValue() != null) {
      try {
        return JacksonSerializer.getObjectMapper().readValue(os.getValue().getDecodedValue(), clazz);
      } catch (JsonProcessingException e) {
        LOG.info("Could not deserialize object", e);
        return defaultValue;
      }
    } else {
      return defaultValue;
    }
  }

  @Override
  public ConfigItem getConfigItem(String key) {
    var os = kvClient.getKVValue(addSn(key)).getValue();
    return fromJson(os.getDecodedValue());
  }

  @Override
  public void setBoolean(String key, Boolean value) {
    setString(key, value.toString());
  }

  @Override
  public void setInteger(String key, int value) {
    setString(key, String.valueOf(value));
  }

  @Override
  public void setDouble(String key, double value) {
    setString(key, String.valueOf(value));
  }

  @Override
  public void setString(String key, String value) {
    kvClient.setKVValue(addSn(key), value);
  }

  @Override
  public void setObject(String key, Object value) {
    kvClient.setKVValue(addSn(key), toJson(value));
  }

  private String addSn(String key) {
    return SERVICE_ROUTE_PREFIX + serviceName + SLASH + key;
  }

  private ConfigItem fromJson(String content) {
    try {
      return JacksonSerializer.getObjectMapper().readValue(content, ConfigItem.class);
    } catch (Exception e) {
      // if old config is used, this is a fallback
      ConfigItem configItem = new ConfigItem();
      configItem.setValue(content);
      return configItem;
    }
  }

  private ConfigItem prepareConfigItem(String valueType,
                                       String description,
                                       ConfigurationScope configurationScope,
                                       boolean password) {
    ConfigItem configItem = new ConfigItem();
    configItem.setValueType(valueType);
    configItem.setDescription(description);
    configItem.setPassword(password);
    configItem.setConfigurationScope(configurationScope);

    return configItem;
  }

  private String toJson(Object object) {
    try {
      return JacksonSerializer.getObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      LOG.info("Could not serialize object to JSON", e);
      return "";
    }
  }

}
