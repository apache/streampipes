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

package org.apache.streampipes.processors.transformation.jvm.processor.fieldrename;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.TransformOperations;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class FiledRenameProcessor extends StreamPipesDataProcessor {

  private static final String CONVERT_PROPERTY = "convert-property";
  private static final String FIELD_NAME = "field-name";
  private String oldPropertyName;
  private String newPropertyName;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.fieldrename")
        .category(DataProcessorType.TRANSFORM)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(), Labels.withId
                (CONVERT_PROPERTY), PropertyScope.NONE)
            .build())
        .requiredTextParameter(Labels.withId(FIELD_NAME))
        .outputStrategy(OutputStrategies.transform(TransformOperations
            .dynamicRuntimeNameTransformation(CONVERT_PROPERTY, FIELD_NAME)))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {

    this.oldPropertyName = processorParams.extractor().mappingPropertyValue(CONVERT_PROPERTY);
    this.newPropertyName = processorParams.extractor().singleValueParameter(FIELD_NAME, String.class);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {

    AbstractField<?> propertyValue = event.getFieldBySelector(oldPropertyName);
    event.removeFieldBySelector(oldPropertyName);
    event.addField(newPropertyName, propertyValue);
    spOutputCollector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
