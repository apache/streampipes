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

package org.apache.streampipes.sdk.builder.adapter;

import org.apache.streampipes.model.connect.guess.FieldStatusInfo;
import org.apache.streampipes.model.connect.guess.SampleData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builder for {@link SampleData} similar to {@code GuessSchemaBuilder}.
 */
public class SampleDataBuilder {

  private final List<Map<String, Object>> samples;
  private Map<String, FieldStatusInfo> fieldStatusInfos;

  private SampleDataBuilder() {
    this.samples = new ArrayList<>();
    this.fieldStatusInfos = new HashMap<>();
  }

  /**
   * Creates a new builder instance.
   */
  public static SampleDataBuilder create() {
    return new SampleDataBuilder();
  }

  /**
   * Adds a single sample map to the builder. The provided map will be copied.
   */
  public SampleDataBuilder sample(Map<String, Object> sample) {
    if (!sample.isEmpty()) {
      this.samples.add(new HashMap<>(sample));
    }
    return this;
  }

  /**
   * Adds multiple samples to the builder. Each map will be copied.
   */
  public SampleDataBuilder samples(List<Map<String, Object>> samples) {
    Objects.requireNonNull(samples, "samples must not be null");
    for (Map<String, Object> s : samples) {
      this.samples.add(new HashMap<>(s));
    }
    return this;
  }


  public SampleDataBuilder fieldStatusInfos(Map<String, FieldStatusInfo> fieldStatusInfos) {
    this.fieldStatusInfos = fieldStatusInfos;
    return this;
  }


  /**
   * Builds the {@link SampleData} instance.
   */
  public SampleData build() {
    SampleData sd = new SampleData();
    sd.setSamples(new ArrayList<>(this.samples));
    sd.setFieldStatusInfos(new HashMap<>(this.fieldStatusInfos));
    return sd;
  }
}
