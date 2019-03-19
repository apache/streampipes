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

package org.streampipes.processors.enricher.flink.processor.math.mathop;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.enricher.flink.processor.math.operation.Operation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class MathOpParameters extends EventProcessorBindingParams {

    private Operation operation;
    private String leftOperand;
    private String rightOperand;
    private String resultField;

    public MathOpParameters(DataProcessorInvocation graph, Operation operation, String leftOperand, String rightOperand, String resultField) {
        super(graph);
        this.operation = operation;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.resultField = resultField;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getLeftOperand() {
        return leftOperand;
    }

    public String getRightOperand() {
        return rightOperand;
    }

    public String getResultField() {
        return resultField;
    }
}
