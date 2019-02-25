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

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.model.runtime.Event;
import org.streampipes.processors.enricher.flink.processor.math.operation.Operation;

public class StaticMathOp implements FlatMapFunction<Event, Event> {

    private Operation operation;
    private String leftOperand;
    private double rightOperandValue;
    private String resulField;

    public StaticMathOp(Operation operation, String leftOperand, double rightOperandValue, String resultField) {
        this.operation = operation;
        this.leftOperand = leftOperand;
        this.rightOperandValue = rightOperandValue;
        this.resulField = resultField;
    }

    @Override
    public void flatMap(Event in, Collector<Event> out) throws Exception {
        Double leftValue  = Double.parseDouble(String.valueOf(in.getFieldBySelector(leftOperand)
                .getAsPrimitive().getAsDouble()));

        Double result = operation.operate(leftValue, rightOperandValue);
        in.addField(resulField, result);

        out.collect(in);
    }
}
