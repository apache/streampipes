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

package org.apache.streampipes.processors.enricher.flink.processor.math.staticmathop;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.container.config.ConfigExtractor;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.enricher.flink.config.EnricherFlinkConfig;
import org.apache.streampipes.processors.enricher.flink.processor.math.operation.Operation;
import org.apache.streampipes.processors.enricher.flink.processor.math.operation.OperationAddition;
import org.apache.streampipes.processors.enricher.flink.processor.math.operation.OperationDivide;
import org.apache.streampipes.processors.enricher.flink.processor.math.operation.OperationModulo;
import org.apache.streampipes.processors.enricher.flink.processor.math.operation.OperationMultiply;
import org.apache.streampipes.processors.enricher.flink.processor.math.operation.OperationSubtracting;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class StaticMathOpController extends FlinkDataProcessorDeclarer<StaticMathOpParameters> {

  private final String RESULT_FIELD = "calculationResultStatic";
  private final String LEFT_OPERAND = "leftOperand";
  private final String RIGHT_OPERAND_VALUE = "rightOperandValue";
  private final String OPERATION = "operation";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.enricher.flink.processor.math.staticmathop")
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
  public FlinkDataProcessorRuntime<StaticMathOpParameters> getRuntime(DataProcessorInvocation graph,
                                                                      ProcessingElementParameterExtractor extractor,
                                                                      ConfigExtractor configExtractor,
                                                                      StreamPipesClient streamPipesClient) {
    String leftOperand = extractor.mappingPropertyValue(LEFT_OPERAND);
    double rightOperand = extractor.singleValueParameter(RIGHT_OPERAND_VALUE, Double.class);
    String operation = extractor.selectedSingleValue(OPERATION, String.class);

    Operation arithmeticOperation = null;
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

    StaticMathOpParameters parameters = new StaticMathOpParameters(graph, arithmeticOperation, leftOperand, rightOperand, RESULT_FIELD);

    return new StaticMathOpProgram(parameters, EnricherFlinkConfig.INSTANCE.getDebug());

  }
}
