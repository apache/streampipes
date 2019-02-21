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

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.processors.enricher.flink.processor.math.operation.Operation;

import java.util.Map;

public class MathOp implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

    private Operation operation;
    private String leftOperand;
    private String rightOperand;
    private String resulField;

    public MathOp(Operation operation, String leftOperand, String rightOperand, String resulField) {
        this.operation = operation;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.resulField = resulField;
    }

    @Override
    public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
        Double leftValue  = Double.parseDouble(String.valueOf(in.get(leftOperand)));
        Double rightValue = Double.parseDouble(String.valueOf(in.get(rightOperand)));

        Double result = operation.operate(leftValue, rightValue);
        in.put(resulField, result);

        out.collect(in);
    }
}
