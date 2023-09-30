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

package org.apache.streampipes.svcdiscovery.api;

import org.apache.streampipes.model.extensions.configuration.ConfigItem;
import org.apache.streampipes.model.extensions.configuration.ConfigurationScope;

public interface SpConfig {

  <T> void register(String key, T defaultValue, String description, ConfigurationScope configurationScope);

  void register(String key, boolean defaultValue, String description);

  void register(String key, int defaultValue, String description);

  void register(String key, double defaultValue, String description);

  void register(String key, String defaultValue, String description);

  void register(ConfigItem configItem);

  void registerObject(String key, Object defaultValue, String description);

  void registerPassword(String key, String defaultValue, String description);

  boolean getBoolean(String key);

  int getInteger(String key);

  double getDouble(String key);

  String getString(String key);

  boolean exists(String key);

  <T> T getObject(String key, Class<T> clazz, T defaultValue);

  ConfigItem getConfigItem(String key);

  void setBoolean(String key, Boolean value);

  void setInteger(String key, int value);

  void setDouble(String key, double value);

  void setString(String key, String value);

  void setObject(String key, Object value);

}
