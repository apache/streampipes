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

package org.apache.streampipes.processors.transformation.jvm.processor.array.count;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.List;

public class CountArrayProcessor extends StreamPipesDataProcessor {

  public static final String COUNT_NAME = "countValue";
  public static final String ARRAY_FIELD = "array-field";

  private String arrayField;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.transformation.jvm.count-array", 0)
        .category(DataProcessorType.COUNT_OPERATOR)
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .requiredStream(
            StreamRequirementsBuilder.create()
                .requiredPropertyWithUnaryMapping(EpRequirements.listRequirement(),
                    Labels.withId(ARRAY_FIELD), PropertyScope.NONE)
                .build())
        .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(Labels.empty(), COUNT_NAME,
            SO.NUMBER)))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.arrayField = parameters.extractor().mappingPropertyValue(ARRAY_FIELD);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {

    List<AbstractField> allEvents = event.getFieldBySelector(arrayField).getAsList().getRawValue();

    event.addField(CountArrayProcessor.COUNT_NAME, allEvents.size());

    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
