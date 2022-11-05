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

import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;

import java.util.List;

public class GenericDataStreamAdapterBuilder extends
    AdapterDescriptionBuilder<GenericDataStreamAdapterBuilder, GenericAdapterStreamDescription> {

  protected GenericDataStreamAdapterBuilder(String id, GenericAdapterStreamDescription element) {
    super(id, element);
  }

  /**
   * Create a new generic stream adapter description using the builder pattern.
   *
   * @param appId that is set for the GenericAdapterStreamDescription
   * @return the builder object
   */
  public static GenericDataStreamAdapterBuilder create(String appId) {
    return new GenericDataStreamAdapterBuilder(appId, new GenericAdapterStreamDescription());
  }

  @Override
  protected GenericDataStreamAdapterBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {

  }

  /**
   * Add a FormatDescription to the GenericDataStreamAdapterDescription
   *
   * @param formatDescription that should be added to the adapter
   * @return the builder object
   */
  public GenericDataStreamAdapterBuilder format(FormatDescription formatDescription) {
    this.elementDescription.setFormatDescription(formatDescription);
    return this;
  }

  /**
   * Add a ProtocolDescription to the GenericDataStreamAdapterDescription
   *
   * @param protocolDescription that should be added to the adapter
   * @return the builder object
   */
  public GenericDataStreamAdapterBuilder protocol(ProtocolDescription protocolDescription) {
    this.elementDescription.setProtocolDescription(protocolDescription);
    return this;
  }

  /**
   * Add a list of TransformationRules to the GenericDataStreamAdapterDescription
   *
   * @param rules that should be added to the adapter
   * @return the builder object
   */
  public GenericDataStreamAdapterBuilder addRules(List<TransformationRuleDescription> rules) {
    this.elementDescription.setRules(rules);
    return this;
  }
}
