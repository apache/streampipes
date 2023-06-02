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

import org.apache.streampipes.model.connect.grounding.ParserDescription;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;

public class ParserDescriptionBuilder extends
    AbstractConfigurablePipelineElementBuilder<ParserDescriptionBuilder, ParserDescription> {

  protected ParserDescriptionBuilder(String appId, String label, String description) {
    super(appId, label, description, new ParserDescription());
  }

  /**
   * Creates a new format description using the builder pattern.
   *
   * @param id          A unique identifier of the new element, e.g., com.mycompany.sink.mynewdatasink
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   */
  public static ParserDescriptionBuilder create(String id, String label, String description) {
    return new ParserDescriptionBuilder(id, label, description);
  }

  @Override
  protected ParserDescriptionBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {
    var group = new StaticPropertyGroup(
        elementDescription.getAppId(),
        elementDescription.getName(),
        elementDescription.getDescription(),
        getStaticProperties());
    this.elementDescription.setConfig(group);
  }
}

