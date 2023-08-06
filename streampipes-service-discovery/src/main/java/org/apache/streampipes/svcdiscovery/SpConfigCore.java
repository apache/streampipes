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

package org.apache.streampipes.svcdiscovery;

import org.apache.streampipes.model.extensions.configuration.ConfigItem;
import org.apache.streampipes.model.extensions.configuration.ConfigurationScope;
import org.apache.streampipes.svcdiscovery.api.SpConfig;

public class SpConfigCore implements SpConfig {

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
  public void register(ConfigItem configItem) {

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
