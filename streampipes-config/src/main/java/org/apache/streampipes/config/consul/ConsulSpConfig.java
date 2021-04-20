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

package org.apache.streampipes.config.consul;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.kv.Value;
import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.config.SpConfigChangeCallback;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.model.ConfigItem;
import org.apache.streampipes.svcdiscovery.api.model.ConfigurationScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConsulSpConfig extends SpConfig implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ConsulSpConfig.class);

    private static final String SLASH = "/";
    public static final String SERVICE_ROUTE_PREFIX = "sp/v1/";

    private final String serviceName;
    private final KeyValueClient kvClient;

    // TODO Implement mechanism to update the client when some configuration parameters change in Consul
    private SpConfigChangeCallback callback;
    private Map<String, Object> configProps;

    public ConsulSpConfig(String serviceName) {
        super(serviceName);
        Consul consul = consulInstance();
        this.kvClient = consul.keyValueClient();
        this.serviceName = serviceName;
    }

    public ConsulSpConfig(String serviceName, SpConfigChangeCallback callback) {
        this(serviceName);
        this.callback = callback;
        this.configProps = new HashMap<>();
        new Thread(this).start();
    }

    @Override
    public void run() {
        Consul consulThread = Consul.builder().build(); // connect to Consul on localhost
        KeyValueClient kvClientThread = consulThread.keyValueClient();
        while (true) {

            configProps.keySet().forEach((s) -> {
                Optional<Value> te = kvClientThread.getValue(addSn(s));
                if (!te.get().getValueAsString().get().equals(configProps.get(s))) {
                    callback.onChange();
                    configProps.put(s, te.get().getValueAsString().get());
                }
            });

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T> void register(String key, T defaultValue, String description, ConfigurationScope configurationScope) {
        register(key, String.valueOf(defaultValue), getValueType(defaultValue), description, configurationScope, false);
    }

    private <T> String getValueType(T defaultValue) {
        if (defaultValue instanceof Boolean) {
          return "xs:boolean";
        } else if (defaultValue instanceof Integer) {
          return "xs:integer";
        } else if (defaultValue instanceof Double) {
          return "xs:double";
        } else {
          return "xs:string";
        }
    }

    @Override
    public void register(String key, boolean defaultValue, String description) {
        register(key, Boolean.toString(defaultValue), "xs:boolean", description, ConfigurationScope.CONTAINER_STARTUP_CONFIG, false);
    }

    @Override
    public void register(String key, int defaultValue, String description) {
        register(key, Integer.toString(defaultValue), "xs:integer", description, ConfigurationScope.CONTAINER_STARTUP_CONFIG, false);
    }

    @Override
    public void register(String key, double defaultValue, String description) {
        register(key, Double.toString(defaultValue), "xs:double", description, ConfigurationScope.CONTAINER_STARTUP_CONFIG, false);

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

    private void register(String key, String defaultValue, String valueType, String description, ConfigurationScope configurationScope, boolean isPassword) {

        Optional<String> i = kvClient.getValueAsString(addSn(key));
        ConfigItem configItem = prepareConfigItem(valueType, description, configurationScope, isPassword);
        // TODO this check does not work
        if (!i.isPresent()) {
            // Set the value of environment variable as default
            String envVariable = System.getenv(key);
            if (envVariable != null) {
                configItem.setValue(envVariable);
                kvClient.putValue(addSn(key), toJson(configItem));
            } else {
                configItem.setValue(defaultValue);
                kvClient.putValue(addSn(key), toJson(configItem));
            }
        }

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

    private ConfigItem prepareConfigItem(String valueType, String description, ConfigurationScope configurationScope, boolean password) {
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
