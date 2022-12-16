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
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ProtocolDescriptionBuilder extends
    AbstractConfigurablePipelineElementBuilder<ProtocolDescriptionBuilder, ProtocolDescription> {

  protected ProtocolDescriptionBuilder(String appId, String label, String description) {
    super(appId, label, description, new ProtocolDescription());
  }

  private ProtocolDescriptionBuilder(String id) {
    super(id, new ProtocolDescription());
  }

  /**
   * Creates a new protocol description using the builder pattern.
   *
   * @param id          A unique identifier of the new element, e.g., com.mycompany.protocol.mynewprotocol
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   */
  public static ProtocolDescriptionBuilder create(String id, String label, String description) {
    return new ProtocolDescriptionBuilder(id, label, description);
  }

  /**
   * Creates a new protocol description using the builder pattern.
   *
   * @param id A unique identifier of the new element, e.g., com.mycompany.protocol.mynewprotocol
   */
  public static ProtocolDescriptionBuilder create(String id) {
    return new ProtocolDescriptionBuilder(id);
  }

  public ProtocolDescriptionBuilder sourceType(AdapterSourceType sourceType) {
    this.elementDescription.setSourceType(sourceType.toString());
    return this;
  }

  public ProtocolDescriptionBuilder category(AdapterType... categories) {
    this.elementDescription
        .setCategory(Arrays
            .stream(categories)
            .map(Enum::name)
            .collect(Collectors.toList()));
    return me();
  }

  @Override
  protected ProtocolDescriptionBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {
    this.elementDescription.setConfig(getStaticProperties());
  }
}
