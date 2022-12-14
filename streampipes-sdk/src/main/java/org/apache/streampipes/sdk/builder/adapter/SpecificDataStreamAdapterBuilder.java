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

import org.apache.streampipes.model.connect.adapter.SpecificAdapterStreamDescription;
import org.apache.streampipes.sdk.builder.DataSetBuilder;

public class SpecificDataStreamAdapterBuilder extends
    AdapterDescriptionBuilder<SpecificDataStreamAdapterBuilder, SpecificAdapterStreamDescription> {

  private SpecificDataStreamAdapterBuilder(String id, String label, String description) {
    super(id, label, description, new SpecificAdapterStreamDescription());
  }

  private SpecificDataStreamAdapterBuilder(String appId) {
    super(appId, new SpecificAdapterStreamDescription());
  }

  /**
   * Creates a new specific data stream adapter using the builder pattern.
   *
   * @param appId       A unique identifier of the new element, e.g., com.mycompany.set.mynewdataset
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   * @return a new instance of {@link DataSetBuilder}
   */
  public static SpecificDataStreamAdapterBuilder create(String appId, String label, String
      description) {
    return new SpecificDataStreamAdapterBuilder(appId, label, description);
  }

  /**
   * Creates a new specific data stream adapter using the builder pattern.
   *
   * @param appId A unique identifier of the new element, e.g., com.mycompany.set.mynewdataset
   * @return a new instance of {@link DataSetBuilder}
   */
  public static SpecificDataStreamAdapterBuilder create(String appId) {
    return new SpecificDataStreamAdapterBuilder(appId);
  }


  @Override
  protected SpecificDataStreamAdapterBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {
    this.elementDescription.setConfig(getStaticProperties());
  }
}
