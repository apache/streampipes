/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.config.consul;

import org.streampipes.config.SpConfig;
import org.streampipes.config.SpConfigChangeCallback;
import org.streampipes.config.model.ConfigItem;
import org.streampipes.config.model.ConfigurationScope;

public class MockSpConfig extends SpConfig {

    public MockSpConfig(String name) {
        super(name);
    }

    public MockSpConfig(String serviceName, SpConfigChangeCallback callback) {
        super(serviceName, callback);
    }

    @Override
    public <T> void register(String key, T defaultValue, String description, ConfigurationScope configurationScope) {

    }

    @Override
    public void register(String key, boolean defaultValue, String description) {

    }

    @Override
    public void register(String key, int defaultValue, String description) {

    }

    @Override
    public void register(String key, double defaultValue, String description) {

    }

    @Override
    public void register(String key, String defaultValue, String description) {

    }

    @Override
    public void registerObject(String key, Object defaultValue, String description) {

    }

    @Override
    public void registerPassword(String key, String defaultValue, String description) {

    }

    @Override
    public boolean getBoolean(String key) {
        return false;
    }

    @Override
    public int getInteger(String key) {
        return 0;
    }

    @Override
    public double getDouble(String key) {
        return 0;
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz, T defaultValue) {
        return null;
    }

    @Override
    public ConfigItem getConfigItem(String key) {
        return null;
    }

    @Override
    public void setBoolean(String key, Boolean value) {

    }

    @Override
    public void setInteger(String key, int value) {

    }

    @Override
    public void setDouble(String key, double value) {

    }

    @Override
    public void setString(String key, String value) {

    }

    @Override
    public void setObject(String key, Object value) {

    }
}
