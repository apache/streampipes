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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AdapterDescriptionBuilder extends
    AbstractConfigurablePipelineElementBuilder<AdapterDescriptionBuilder, AdapterDescription> {

  private AdapterDescriptionBuilder(String appId) {
    super(appId, new AdapterDescription());
  }

  public static AdapterDescriptionBuilder create(String appId) {
    return new AdapterDescriptionBuilder(appId);
  }

  public AdapterDescriptionBuilder category(AdapterType... categories) {
    this.elementDescription
        .setCategory(Arrays
            .stream(categories)
            .map(Enum::name)
            .collect(Collectors.toList()));
    return me();
  }

  public AdapterDescriptionBuilder elementId(String elementId) {
    this.elementDescription.setElementId(elementId);
    return me();
  }
  @Override
  protected AdapterDescriptionBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {
    this.elementDescription.setConfig(getStaticProperties());
  }


}
