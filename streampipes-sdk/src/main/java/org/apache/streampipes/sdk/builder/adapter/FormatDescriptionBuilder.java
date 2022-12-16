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

import org.apache.streampipes.model.connect.grounding.FormatDescription;
import org.apache.streampipes.sdk.builder.AbstractConfigurablePipelineElementBuilder;

public class FormatDescriptionBuilder extends
    AbstractConfigurablePipelineElementBuilder<FormatDescriptionBuilder, FormatDescription> {

  protected FormatDescriptionBuilder(String appId, String label, String description) {
    super(appId, label, description, new FormatDescription());
  }

  /**
   * Creates a new format description using the builder pattern.
   *
   * @param id          A unique identifier of the new element, e.g., com.mycompany.sink.mynewdatasink
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   */
  public static FormatDescriptionBuilder create(String id, String label, String description) {
    return new FormatDescriptionBuilder(id, label, description);
  }

  /**
   * Add a format type to the format description, e.g. json
   *
   * @param formatType
   * @return
   */
  public FormatDescriptionBuilder addFormatType(String formatType) {
    this.elementDescription.setFormatType(formatType);
    return me();
  }

  @Override
  protected FormatDescriptionBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {
    this.elementDescription.setConfig(getStaticProperties());
  }
}

