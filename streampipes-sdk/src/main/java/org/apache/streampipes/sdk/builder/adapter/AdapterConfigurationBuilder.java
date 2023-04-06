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

import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.AdapterConfiguration;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.Parser;
import org.apache.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterConfigurationBuilder extends
    AbstractConfigurablePipelineElementBuilder<AdapterConfigurationBuilder, AdapterConfiguration> {

  private final AdapterDescription adapterDescription;
  private final List<Parser> supportedParsers;

  protected AdapterConfigurationBuilder(String appId) {
    super(appId, new AdapterConfiguration());
    adapterDescription = new AdapterDescription();
    adapterDescription.setAppId(appId);
    supportedParsers = new ArrayList<>();
  }

  public static AdapterConfigurationBuilder create(String appId) {
    return new AdapterConfigurationBuilder(appId);
  }

  @Override
  protected AdapterConfigurationBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {
    // TODO
  }

  @Override
  public AdapterConfiguration build() {
    return new AdapterConfiguration(adapterDescription, supportedParsers);
  }

  public AdapterConfigurationBuilder withSupportedParsers(Parser... parsers) {
    Collections.addAll(supportedParsers, parsers);
    return this;
  }

  public AdapterConfigurationBuilder withCategory(AdapterType... categories) {
    this.adapterDescription
        .setCategory(Arrays
            .stream(categories)
            .map(Enum::name)
            .collect(Collectors.toList()));
    return me();
  }


}
