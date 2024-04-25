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
package org.apache.streampipes.model.datalake.param;

import java.util.HashMap;
import java.util.Map;

public class ProvidedRestQueryParams {

  private final String measurementId;
  private final Map<String, String> providedParams;

  public ProvidedRestQueryParams(String measurementId,
                                 Map<String, String> providedParams) {
    this.measurementId = measurementId;
    this.providedParams = providedParams;
  }

  public ProvidedRestQueryParams(ProvidedRestQueryParams params) {
    this.measurementId = params.getMeasurementId();
    this.providedParams = new HashMap<>();
    providedParams.putAll(params.getProvidedParams());
  }

  public boolean has(String key) {
    return providedParams.containsKey(key);
  }

  public Long getAsLong(String key) {
    return has(key) ? Long.parseLong(providedParams.get(key)) : null;
  }

  public Integer getAsInt(String key) {
    return has(key) ? Integer.parseInt(String.valueOf(providedParams.get(key))) : null;
  }

  public String getAsString(String key) {
    return has(key) ? providedParams.get(key) : null;
  }

  public boolean getAsBoolean(String key) {
    return has(key) && Boolean.parseBoolean(providedParams.get(key));
  }

  public String getMeasurementId() {
    return measurementId;
  }

  public void update(String key, String value) {
    this.providedParams.put(key, value);
  }

  public void update(String key, long value) {
    update(key, String.valueOf(value));
  }

  public void update(String key, Integer value) {
    update(key, String.valueOf(value));
  }

  public void update(String key, boolean value) {
    update(key, String.valueOf(value));
  }

  public void remove(String key) {
    this.providedParams.remove(key);
  }

  public Map<String, String> getProvidedParams() {
    return providedParams;
  }
}
