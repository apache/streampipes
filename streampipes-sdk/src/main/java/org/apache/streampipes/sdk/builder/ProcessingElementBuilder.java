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

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.sdk.helpers.Locales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessingElementBuilder
    extends AbstractProcessingElementBuilder<ProcessingElementBuilder, DataProcessorDescription> {


  private List<OutputStrategy> outputStrategies;

  private ProcessingElementBuilder(String id, String name, String description, int version) {
    super(id, name, description, new DataProcessorDescription());
    this.outputStrategies = new ArrayList<>();
    this.elementDescription.setVersion(version);
  }

  private ProcessingElementBuilder(String id, int version) {
    super(id, new DataProcessorDescription());
    this.outputStrategies = new ArrayList<>();
    this.elementDescription.setVersion(version);
  }

  /**
   * Creates a new processing element based on a label using the builder pattern.
   * @param id          A unique identifier of the new element, e.g., com.mycompany.processor.mynewdataprocessor
   * @param label       A human-readable name of the element.
   *                    Will later be shown as the element name in the StreamPipes UI.
   * @param description A human-readable description of the element.
   * @param version     version of the processing element for migration purposes. Should be 0 in standard cases.
   *                    Only in case there exist migrations for the specific element the version needs to be aligned.
   * @return Builder for the pre-defined processing element.
   */
  public static ProcessingElementBuilder create(String id, String label, String description, int version) {
    return new ProcessingElementBuilder(id, label, description, version);
  }

  /**
   * Creates a new processing element using the builder pattern. If no label and description is
   * given
   * for an element,
   * {@link org.apache.streampipes.sdk.builder.AbstractProcessingElementBuilder#withLocales(Locales...)}
   * must be called.
   *
   * @param id A unique identifier of the new element, e.g., com.mycompany.sink.mynewdatasink
   */
  public static ProcessingElementBuilder create(String id, int version) {
    return new ProcessingElementBuilder(id, version);
  }

  /**
   * Assigns an output strategy to the element which defines the output the data processor produces.
   *
   * @param outputStrategy An {@link org.apache.streampipes.model.output.OutputStrategy}. Use
   *                       {@link org.apache.streampipes.sdk.helpers.OutputStrategies} to assign the strategy.
   * @return {@link ProcessingElementBuilder}
   */
  public ProcessingElementBuilder outputStrategy(OutputStrategy outputStrategy) {
    this.outputStrategies.add(outputStrategy);
    return me();
  }

  /**
   * Assigns a category to the element which later serves to categorize data processors in the UI.
   *
   * @param epaCategory The {@link org.apache.streampipes.model.DataProcessorType} of the element.
   * @return {@link ProcessingElementBuilder}
   */
  public ProcessingElementBuilder category(DataProcessorType... epaCategory) {
    this.elementDescription.setCategory(Arrays
        .stream(epaCategory)
        .map(Enum::name)
        .collect(Collectors.toList()));
    return me();
  }

  @Override
  public void prepareBuild() {
    super.prepareBuild();
    this.elementDescription.setOutputStrategies(outputStrategies);
  }

  @Override
  protected ProcessingElementBuilder me() {
    return this;
  }
}
