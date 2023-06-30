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

package org.apache.streampipes.processors.enricher.jvm.processor.trigonometry;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class TrigonometryProcessor extends StreamPipesDataProcessor {

  private static final String OPERAND = "operand";
  private static final String OPERATION = "operation";
  private static final String RESULT_FIELD = "trigonometryResult";

  private Operation operation;
  private String operand;


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.enricher.jvm.processor.trigonometry")
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.ALGORITHM)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                Labels.withId(OPERAND),
                PropertyScope.NONE)
            .build())
        .outputStrategy(
            OutputStrategies.append(
                EpProperties.numberEp(Labels.empty(), RESULT_FIELD, SO.NUMBER)))
        .requiredSingleValueSelection(Labels.withId(OPERATION),
            Options.from("sin", "cos", "tan"))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    this.operand = parameters.extractor().mappingPropertyValue(OPERAND);
    String stringOperation = parameters.extractor().selectedSingleValue(OPERATION, String.class);

    switch (stringOperation) {
      case "sin":
        operation = Operation.SIN;
        break;
      case "cos":
        operation = Operation.COS;
        break;
      case "tan":
        operation = Operation.TAN;

    }
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) throws SpRuntimeException {
    double value = in.getFieldBySelector(operand).getAsPrimitive().getAsDouble();
    double result;

    if (operation == Operation.SIN) {
      result = Math.sin(value);
    } else if (operation == Operation.COS) {
      result = Math.cos(value);
    } else {
      result = Math.tan(value);
    }
    in.addField(RESULT_FIELD, result);

    out.collect(in);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
