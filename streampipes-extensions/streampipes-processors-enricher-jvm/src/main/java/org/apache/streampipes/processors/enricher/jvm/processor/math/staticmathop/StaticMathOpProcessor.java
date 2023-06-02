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

package org.apache.streampipes.processors.enricher.jvm.processor.math.staticmathop;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.enricher.jvm.processor.math.operation.Operation;
import org.apache.streampipes.processors.enricher.jvm.processor.math.operation.OperationAddition;
import org.apache.streampipes.processors.enricher.jvm.processor.math.operation.OperationDivide;
import org.apache.streampipes.processors.enricher.jvm.processor.math.operation.OperationModulo;
import org.apache.streampipes.processors.enricher.jvm.processor.math.operation.OperationMultiply;
import org.apache.streampipes.processors.enricher.jvm.processor.math.operation.OperationSubtracting;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class StaticMathOpProcessor extends StreamPipesDataProcessor {

  private static final String RESULT_FIELD = "calculationResultStatic";
  private static final String LEFT_OPERAND = "leftOperand";
  private static final String RIGHT_OPERAND_VALUE = "rightOperandValue";
  private static final String OPERATION = "operation";

  Operation arithmeticOperation;
  String leftOperand;
  double rightOperandValue;


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.enricher.jvm.processor.math.staticmathop")
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.ALGORITHM)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                Labels.withId(LEFT_OPERAND),
                PropertyScope.NONE)
            .build())
        .requiredFloatParameter(Labels.withId(RIGHT_OPERAND_VALUE))
        .outputStrategy(
            OutputStrategies.keep())
        .requiredSingleValueSelection(Labels.withId(OPERATION),
            Options.from("+", "-", "/", "*", "%"))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.leftOperand = parameters.extractor().mappingPropertyValue(LEFT_OPERAND);
    this.rightOperandValue = parameters.extractor().singleValueParameter(RIGHT_OPERAND_VALUE, Double.class);
    String operation = parameters.extractor().selectedSingleValue(OPERATION, String.class);

    switch (operation) {
      case "+":
        arithmeticOperation = new OperationAddition();
        break;
      case "-":
        arithmeticOperation = new OperationSubtracting();
        break;
      case "*":
        arithmeticOperation = new OperationMultiply();
        break;
      case "/":
        arithmeticOperation = new OperationDivide();
        break;
      case "%":
        arithmeticOperation = new OperationModulo();
    }
  }

  @Override
  public void onEvent(Event in, SpOutputCollector out) throws SpRuntimeException {
    Double leftValue = Double.parseDouble(String.valueOf(in.getFieldBySelector(leftOperand)
        .getAsPrimitive().getAsDouble()));

    Double result = arithmeticOperation.operate(leftValue, rightOperandValue);
    in.updateFieldBySelector(leftOperand, result);

    out.collect(in);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
