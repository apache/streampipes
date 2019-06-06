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

package org.streampipes.config;

import org.streampipes.config.consul.ConsulSpConfig;
import org.streampipes.config.model.ConfigItem;
import org.streampipes.config.model.ConfigurationScope;


public abstract class SpConfig {


    public SpConfig(String name) {

    }

    public SpConfig(String serviceName, SpConfigChangeCallback callback) {

    }

    public static SpConfig getSpConfig(String serviceName) {
        return new ConsulSpConfig(serviceName);
    }

    public static SpConfig getSpConfig(String serviceName, SpConfigChangeCallback callback) {
        return new ConsulSpConfig(serviceName, callback);
    }

    public abstract <T> void register(String key, T defaultValue, String description, ConfigurationScope configurationScope);

    public abstract void register(String key, boolean defaultValue, String description);

    public abstract void register(String key, int defaultValue, String description);

    public abstract void register(String key, double defaultValue, String description);

    public abstract void register(String key, String defaultValue, String description);

    public abstract void registerObject(String key, Object defaultValue, String description);

    public abstract void registerPassword(String key, String defaultValue, String description);

    public abstract boolean getBoolean(String key);

    public abstract int getInteger(String key);

    public abstract double getDouble(String key);

    public abstract String getString(String key);

    public abstract <T> T getObject(String key, Class<T> clazz, T defaultValue);

    public abstract ConfigItem getConfigItem(String key);

    public abstract void setBoolean(String key, Boolean value);

    public abstract void setInteger(String key, int value);

    public abstract void setDouble(String key, double value);

    public abstract void setString(String key, String value);

    public abstract void setObject(String key, Object value);

}
