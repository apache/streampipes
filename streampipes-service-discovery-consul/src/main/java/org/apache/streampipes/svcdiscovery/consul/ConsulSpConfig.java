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

import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.SpConfig;
import org.apache.streampipes.svcdiscovery.api.model.ConfigItem;
import org.apache.streampipes.svcdiscovery.api.model.ConfigItemUtils;
import org.apache.streampipes.svcdiscovery.api.model.ConfigurationScope;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class ConsulSpConfig extends AbstractConsulService implements SpConfig {

  public static final String SERVICE_ROUTE_PREFIX = "sp/v1/";
  private static final Logger LOG = LoggerFactory.getLogger(ConsulSpConfig.class);
  private static final String SLASH = "/";
  private final String serviceName;
  private final KeyValueClient kvClient;

  // TODO Implement mechanism to update the client when some configuration parameters change in Consul
  private Map<String, Object> configProps;

  public ConsulSpConfig(String serviceName) {
    Consul consul = consulInstance();
    this.kvClient = consul.keyValueClient();
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
    Optional<String> i = kvClient.getValueAsString(addSn(key));
    if (!i.isPresent()) {
      kvClient.putValue(addSn(key), toJson(defaultValue));
    }
  }

  @Override
  public void registerPassword(String key, String defaultValue, String description) {
    register(key, defaultValue, "xs:string", description, ConfigurationScope.CONTAINER_STARTUP_CONFIG, true);
  }

  @Override
  public void register(ConfigItem configItem) {
    String key = addSn(configItem.getKey());
    Optional<String> i = kvClient.getValueAsString(key);

    if (!i.isPresent()) {
      // Set the value of environment variable as default
      String envVariable = System.getenv(configItem.getKey());
      if (envVariable != null) {
        configItem.setValue(envVariable);
        kvClient.putValue(key, toJson(configItem));
      } else {
        kvClient.putValue(key, toJson(configItem));
      }
    }
  }

  private void register(String key, String defaultValue, String valueType, String description,
                        ConfigurationScope configurationScope, boolean isPassword) {
    ConfigItem configItem = ConfigItem.from(key, defaultValue, description, valueType, configurationScope, isPassword);
    register(configItem);

    if (configProps != null) {
      configProps.put(key, getString(key));
    }
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
  public <T> T getObject(String key, Class<T> clazz, T defaultValue) {
    Optional<String> os = kvClient.getValueAsString(addSn(key));
    if (os.isPresent()) {
      try {
        return JacksonSerializer.getObjectMapper().readValue(os.get(), clazz);
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
    Optional<String> os = kvClient.getValueAsString(addSn(key));

    return fromJson(os.get());
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
    kvClient.putValue(addSn(key), value);
  }

  @Override
  public void setObject(String key, Object value) {
    kvClient.putValue(addSn(key), toJson(value));
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

  private ConfigItem prepareConfigItem(String valueType, String description, ConfigurationScope configurationScope,
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
