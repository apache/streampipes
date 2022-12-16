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

package org.apache.streampipes.sdk.builder;

import org.apache.streampipes.model.graph.DataSourceDescription;

public class DataSourceBuilder extends AbstractPipelineElementBuilder<DataSourceBuilder, DataSourceDescription> {

  /**
   * Creates a new data source using the builder pattern.
   *
   * @param id          A unique identifier of the new element, e.g., com.mycompany.source.mynewdatasource
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   */
  protected DataSourceBuilder(String id, String label, String description) {
    super(id, label, description, new DataSourceDescription());
  }

  public static DataSourceBuilder create(String id, String label, String description) {
    return new DataSourceBuilder(id, label, description);
  }

  @Override
  protected DataSourceBuilder me() {
    return this;
  }

  @Override
  protected void prepareBuild() {

  }
}
