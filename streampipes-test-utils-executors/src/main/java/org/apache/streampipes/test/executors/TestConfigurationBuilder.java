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
package org.apache.streampipes.test.executors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestConfigurationBuilder{
  private Map<String, Object> fieldConfiguration = new HashMap<>();
  private List<String> eventPrefixes = List.of("");

  public TestConfigurationBuilder config(String key, Object value){
    this.fieldConfiguration.put(key, value);
    return this;
  }
  public TestConfigurationBuilder configWithPrefix(String key, Object value, String prefix){
    this.fieldConfiguration.put(key, prefix + "::" + value);
    return this;
  }

  public TestConfigurationBuilder configWithDefaultPrefix(String key, Object value){
    return this.configWithPrefix(key, value, "");
  }

  public TestConfigurationBuilder config(Map<String, Object> config){
    this.fieldConfiguration = config;
    return this;
  }

  public TestConfigurationBuilder prefixStrategy(PrefixStrategy strategy){
    this.eventPrefixes = switch (strategy){
      case SAME_PREFIX -> List.of(StreamPrefix.S0);
      case ALTERNATE -> List.of(StreamPrefix.S0, StreamPrefix.S1);
    };
    return this;
  }

  public TestConfigurationBuilder customPrefixStrategy(List<String> eventPrefixes) {
    this.eventPrefixes = eventPrefixes;
    return this;
  }

  public TestConfiguration build(){
    return new TestConfiguration(this.fieldConfiguration, this.eventPrefixes);
  }
}