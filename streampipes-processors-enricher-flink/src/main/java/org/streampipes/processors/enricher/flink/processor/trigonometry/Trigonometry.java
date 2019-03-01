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

package org.streampipes.processors.enricher.flink.processor.trigonometry;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.model.runtime.Event;

public class Trigonometry implements FlatMapFunction<Event, Event> {

    private String operand;
    private Operation operation;
    private String resultField;

    public Trigonometry(String operand, Operation operation, String resultField) {
        this.operand = operand;
        this.operation = operation;
        this.resultField = resultField;
    }

    @Override
    public void flatMap(Event in, Collector<Event> out) throws Exception {
        double value = in.getFieldBySelector(operand).getAsPrimitive().getAsDouble();
        double result;

        if (operation == Operation.SIN) {
            result = Math.sin(value);
        } else if (operation == Operation.COS) {
            result = Math.cos(value);
        } else {
            result = Math.tan(value);
        }
        in.addField(resultField, result);

        out.collect(in);
    }
}
