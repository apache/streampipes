/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.processors.enricher.flink.processor.math.staticmathop;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.enricher.flink.config.EnricherFlinkConfig;
import org.streampipes.processors.enricher.flink.processor.math.operation.*;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class StaticMathOpController extends FlinkDataProcessorDeclarer<StaticMathOpParameters> {

    private final String RESULT_FIELD = "calculationResultStatic";
    private final String LEFT_OPERAND = "leftOperand";
    private final String RIGHT_OPERAND_VALUE = "rightOperandValue";
    private final String OPERATION = "operation";

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.streampipes.processors.enricher.flink.processor.math.staticmathop",
                "Static Math", "Performs calculation on an event property with a static value (+, -, *, /, %)")
                 .iconUrl(EnricherFlinkConfig.getIconUrl("math-icon-static"))
                .category(DataProcessorType.ALGORITHM)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                                Labels.from(LEFT_OPERAND, "Left operand", "Select left operand"),
                                PropertyScope.NONE)
                        .build())
                .requiredFloatParameter(Labels.from(RIGHT_OPERAND_VALUE, "Right operand value",
                        "Specify the value of the right operand."))
                .outputStrategy(
                        OutputStrategies.append(
                                EpProperties.numberEp(Labels.empty(), RESULT_FIELD, SO.Number)))
                .requiredSingleValueSelection(OPERATION, "Select Operation", "", Options.from("+", "-", "/", "*", "%"))
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }

    @Override
    public FlinkDataProcessorRuntime<StaticMathOpParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
        String leftOperand = extractor.mappingPropertyValue(LEFT_OPERAND);
        double rightOperand = extractor.singleValueParameter(RIGHT_OPERAND_VALUE, Double.class);
        String operation = extractor.selectedSingleValue(OPERATION, String.class);

        Operation arithmeticOperation = null;
        switch (operation) {
            case "+": arithmeticOperation = new OperationAddition();
                break;
            case "-": arithmeticOperation = new OperationSubtracting();
                break;
            case "*": arithmeticOperation = new OperationMultiply();
                break;
            case "/": arithmeticOperation = new OperationDivide();
                break;
            case "%": arithmeticOperation = new OperationModulo();
        }

        StaticMathOpParameters parameters = new StaticMathOpParameters(graph, arithmeticOperation, leftOperand, rightOperand, RESULT_FIELD);

        return new StaticMathOpProgram(parameters, EnricherFlinkConfig.INSTANCE.getDebug());

    }
}
