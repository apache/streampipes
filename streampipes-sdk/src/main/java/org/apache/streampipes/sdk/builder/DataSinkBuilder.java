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

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.sdk.helpers.Locales;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DataSinkBuilder extends AbstractProcessingElementBuilder<DataSinkBuilder, DataSinkDescription> {

  protected DataSinkBuilder(String id, String label, String description) {
    super(id, label, description, new DataSinkDescription());
  }

  protected DataSinkBuilder(String id) {
    super(id, new DataSinkDescription());
  }

  /**
   * Creates a new data sink using the builder pattern.
   *
   * @param id          A unique identifier of the new element, e.g., com.mycompany.sink.mynewdatasink
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   */
  public static DataSinkBuilder create(String id, String label, String description) {
    return new DataSinkBuilder(id, label, description);
  }

  /**
   * Creates a new data sink using the builder pattern. If no label and description is given
   * for an element,
   * {@link org.apache.streampipes.sdk.builder.AbstractProcessingElementBuilder#withLocales(Locales...)}
   * must be called.
   *
   * @param id A unique identifier of the new element, e.g., com.mycompany.sink.mynewdatasink
   */
  public static DataSinkBuilder create(String id) {
    return new DataSinkBuilder(id);
  }

  public DataSinkBuilder category(DataSinkType... categories) {
    this.elementDescription
        .setCategory(Arrays
            .stream(categories)
            .map(Enum::name)
            .collect(Collectors.toList()));
    return me();
  }

  @Override
  protected DataSinkBuilder me() {
    return this;
  }
}
