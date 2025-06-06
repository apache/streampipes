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
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
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
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.vocabulary.SO;

public class MathOpProcessor implements IStreamPipesDataProcessor {

  protected static final String RESULT_FIELD = "calculationResult";
  protected static final String LEFT_OPERAND = "leftOperand";
  protected static final String RIGHT_OPERAND = "rightOperand";
  protected static final String OPERATION = "operation";

  Operation arithmeticOperation = null;
  String leftOperand;
  String rightOperand;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        MathOpProcessor::new,
        ProcessingElementBuilder
            .create("org.apache.streampipes.processors.enricher.jvm.processor.math.mathop", 0)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .category(DataProcessorType.ALGORITHM)
            .requiredStream(StreamRequirementsBuilder
                                .create()
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.numberReq(),
                                    Labels.withId(LEFT_OPERAND),
                                    PropertyScope.NONE
                                )
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.numberReq(),
                                    Labels.withId(RIGHT_OPERAND),
                                    PropertyScope.NONE
                                )
                                .build())
            .outputStrategy(
                OutputStrategies.append(
                    EpProperties.numberEp(Labels.empty(), RESULT_FIELD, SO.NUMBER)))
            .requiredSingleValueSelection(
                Labels.withId(OPERATION), Options.from(
                    "+", "-", "/",
                    "*", "%"
                )
            )
            .build()
    );
  }

  @Override
  public void onPipelineStarted(
      IDataProcessorParameters params,
      SpOutputCollector collector,
      EventProcessorRuntimeContext runtimeContext
  ) {
    this.leftOperand = params.extractor()
                             .mappingPropertyValue(LEFT_OPERAND);
    this.rightOperand = params.extractor()
                              .mappingPropertyValue(RIGHT_OPERAND);
    String operation = params.extractor()
                             .selectedSingleValue(OPERATION, String.class);

    switch (operation) {
      case "+":
        this.arithmeticOperation = new OperationAddition();
        break;
      case "-":
        this.arithmeticOperation = new OperationSubtracting();
        break;
      case "/":
        this.arithmeticOperation = new OperationDivide();
        break;
      case "*":
        this.arithmeticOperation = new OperationMultiply();
        break;
      case "%":
        this.arithmeticOperation = new OperationModulo();
        break;
      default:
        throw new IllegalArgumentException("Unsupported operation: " + operation);
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    Double leftValue = event.getFieldBySelector(this.leftOperand)
                            .getAsPrimitive()
                            .getAsDouble();
    Double rightValue = event.getFieldBySelector(this.rightOperand)
                             .getAsPrimitive()
                             .getAsDouble();
    Double result = this.arithmeticOperation.operate(leftValue, rightValue);

    event.addField(RESULT_FIELD, result);
    spOutputCollector.collect(event);
  }

  @Override
  public void onPipelineStopped() {
  }
}
