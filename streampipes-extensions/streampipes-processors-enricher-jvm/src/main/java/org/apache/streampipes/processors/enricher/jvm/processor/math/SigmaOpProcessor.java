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


package org.apache.streampipes.processors.enricher.jvm.processor.math;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class SigmaOpProcessor extends StreamPipesDataProcessor {

  protected static final String FIELD_ID = "field";
  private static final String RESULT_FIELD = "sigmaResult";

  private Double fieldSigmaValue;
  private String selectedFieldName;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder
        .create("org.apache.streampipes.processors.enricher.jvm.processor.math.sigmaop", 0)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.ALGORITHM)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.numberReq(),
                Labels.withId(FIELD_ID),
                PropertyScope.NONE)
            .build())
        .outputStrategy(
            OutputStrategies.append(
                EpProperties.numberEp(Labels.empty(), RESULT_FIELD, SO.NUMBER)))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    ProcessingElementParameterExtractor extractor = parameters.extractor();
    this.selectedFieldName = extractor.mappingPropertyValue(FIELD_ID);
    this.fieldSigmaValue = 0.0;
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) throws SpRuntimeException {
    Double fieldValue = in.getFieldBySelector(selectedFieldName).getAsPrimitive().getAsDouble();

    fieldSigmaValue += fieldValue;
    in.addField(RESULT_FIELD, fieldSigmaValue);

    out.collect(in);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
