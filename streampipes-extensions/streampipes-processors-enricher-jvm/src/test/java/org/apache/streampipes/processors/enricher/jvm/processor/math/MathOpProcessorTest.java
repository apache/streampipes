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

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.test.extensions.api.StoreEventCollector;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MathOpProcessorTest {
    @Test
    public void detectAdditionOperator() {
        var processor = new MathOpProcessor();

        DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(processor.declareModel());
        // Set "s0::value1" as leftOperand
        MappingPropertyUnary leftOperand = (MappingPropertyUnary)graph.getStaticProperties().get(0);
        leftOperand.setInternalName("leftOperand");
        leftOperand.setSelectedProperty("s0::value1");
        // Set "s0::value2" as rightOperand
        MappingPropertyUnary rightOperand = (MappingPropertyUnary)graph.getStaticProperties().get(1);
        rightOperand.setInternalName("rightOperand");
        rightOperand.setSelectedProperty("s0::value2");
        // choose operation to calculate
        OneOfStaticProperty operation = (OneOfStaticProperty)graph.getStaticProperties().get(2);
        operation.setInternalName ("operation");
        for (Option option : operation.getOptions()) {
            if (option.getName().equals("+")) {
                option.setSelected(true);
                break;
            }
        }

        ProcessorParams params = new ProcessorParams(graph);
        processor.onInvocation(params,null,null);

        StoreEventCollector collector = new StoreEventCollector();

        processor.onEvent(this.createTestEvent(1,2),collector);
        Assertions.assertEquals(3.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(2,3),collector);
        Assertions.assertEquals(5.,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(3,4),collector);
        Assertions.assertEquals(7.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(4,5),collector);
        Assertions.assertEquals(9.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(5,6),collector);
        Assertions.assertEquals(11.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(6,7),collector);
        Assertions.assertEquals(13.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(7,8),collector);
        Assertions.assertEquals(15.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();
    }

    @Test
    public void detectSubtractionOperator() {
        var processor = new MathOpProcessor();

        DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(processor.declareModel());

        MappingPropertyUnary leftOperand = (MappingPropertyUnary)graph.getStaticProperties().get(0);
        leftOperand.setInternalName("leftOperand");
        leftOperand.setSelectedProperty("s0::value1");

        MappingPropertyUnary rightOperand = (MappingPropertyUnary)graph.getStaticProperties().get(1);
        rightOperand.setInternalName("rightOperand");
        rightOperand.setSelectedProperty("s0::value2");

        OneOfStaticProperty operation = (OneOfStaticProperty)graph.getStaticProperties().get(2);
        operation.setInternalName ("operation");
        for (Option option : operation.getOptions()) {
            if (option.getName().equals("-")) {
                option.setSelected(true);
                break;
            }
        }

        ProcessorParams params = new ProcessorParams(graph);
        processor.onInvocation(params,null,null);

        StoreEventCollector collector = new StoreEventCollector();

        processor.onEvent(this.createTestEvent(4,1),collector);
        Assertions.assertEquals(3.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(8,1),collector);
        Assertions.assertEquals(7.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(9,4),collector);
        Assertions.assertEquals(5.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(6,2),collector);
        Assertions.assertEquals(4.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(10,3),collector);
        Assertions.assertEquals(7.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(15,2),collector);
        Assertions.assertEquals(13.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(19,17),collector);
        Assertions.assertEquals(2.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();
    }

    @Test
    public void detectMultiplicationOperator() {
        var processor = new MathOpProcessor();

        DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(processor.declareModel());

        MappingPropertyUnary leftOperand = (MappingPropertyUnary)graph.getStaticProperties().get(0);
        leftOperand.setInternalName("leftOperand");
        leftOperand.setSelectedProperty("s0::value1");

        MappingPropertyUnary rightOperand = (MappingPropertyUnary)graph.getStaticProperties().get(1);
        rightOperand.setInternalName("rightOperand");
        rightOperand.setSelectedProperty("s0::value2");

        OneOfStaticProperty operation = (OneOfStaticProperty)graph.getStaticProperties().get(2);
        operation.setInternalName ("operation");
        for (Option option : operation.getOptions()) {
            if (option.getName().equals("*")) {
                option.setSelected(true);
                break;
            }
        }

        ProcessorParams params = new ProcessorParams(graph);
        processor.onInvocation(params,null,null);

        StoreEventCollector collector = new StoreEventCollector();

        processor.onEvent(this.createTestEvent(4,1),collector);
        Assertions.assertEquals(4.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(8,1),collector);
        Assertions.assertEquals(8.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(9,4),collector);
        Assertions.assertEquals(36.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(6,2),collector);
        Assertions.assertEquals(12.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(10,3),collector);
        Assertions.assertEquals(30.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(15,2),collector);
        Assertions.assertEquals(30.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(19,17),collector);
        Assertions.assertEquals(323.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();
    }

    @Test
    public void detectDivisionOperator() {
        var processor = new MathOpProcessor();

        DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(processor.declareModel());

        MappingPropertyUnary leftOperand = (MappingPropertyUnary)graph.getStaticProperties().get(0);
        leftOperand.setInternalName("leftOperand");
        leftOperand.setSelectedProperty("s0::value1");

        MappingPropertyUnary rightOperand = (MappingPropertyUnary)graph.getStaticProperties().get(1);
        rightOperand.setInternalName("rightOperand");
        rightOperand.setSelectedProperty("s0::value2");

        OneOfStaticProperty operation = (OneOfStaticProperty)graph.getStaticProperties().get(2);
        operation.setInternalName ("operation");
        for (Option option : operation.getOptions()) {
            if (option.getName().equals("/")) {
                option.setSelected(true);
                break;
            }
        }

        ProcessorParams params = new ProcessorParams(graph);
        processor.onInvocation(params,null,null);

        StoreEventCollector collector = new StoreEventCollector();

        processor.onEvent(this.createTestEvent(4,1),collector);
        Assertions.assertEquals(4.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(8,1),collector);
        Assertions.assertEquals(8.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(16,4),collector);
        Assertions.assertEquals(4.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(6,2),collector);
        Assertions.assertEquals(3.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(12,3),collector);
        Assertions.assertEquals(4.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(16,2),collector);
        Assertions.assertEquals(8.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(34,17),collector);
        Assertions.assertEquals(2.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();
    }

    @Test
    public void detectRemainderOperator() {
        var processor = new MathOpProcessor();

        DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(processor.declareModel());

        MappingPropertyUnary leftOperand = (MappingPropertyUnary)graph.getStaticProperties().get(0);
        leftOperand.setInternalName("leftOperand");
        leftOperand.setSelectedProperty("s0::value1");

        MappingPropertyUnary rightOperand = (MappingPropertyUnary)graph.getStaticProperties().get(1);
        rightOperand.setInternalName("rightOperand");
        rightOperand.setSelectedProperty("s0::value2");

        OneOfStaticProperty operation = (OneOfStaticProperty)graph.getStaticProperties().get(2);
        operation.setInternalName ("operation");
        for (Option option : operation.getOptions()) {
            if (option.getName().equals("%")) {
                option.setSelected(true);
                break;
            }
        }

        ProcessorParams params = new ProcessorParams(graph);
        processor.onInvocation(params,null,null);

        StoreEventCollector collector = new StoreEventCollector();

        processor.onEvent(this.createTestEvent(4,1),collector);
        Assertions.assertEquals(0.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(8,1),collector);
        Assertions.assertEquals(0.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(9,4),collector);
        Assertions.assertEquals(1.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(6,2),collector);
        Assertions.assertEquals(0.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(10,3),collector);
        Assertions.assertEquals(1.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(15,2),collector);
        Assertions.assertEquals(1.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();

        processor.onEvent(this.createTestEvent(19,17),collector);
        Assertions.assertEquals(2.0,collector.getEvents().get(0).getFieldBySelector("calculationResult").getRawValue());
        collector.getEvents().clear();
    }

    private Event createTestEvent(Integer value1,Integer value2) {

        var eventSchema = GuessSchemaBuilder.create()
                .property(PrimitivePropertyBuilder
                        .create(Datatypes.Integer, "value1")
                        .scope(PropertyScope.DIMENSION_PROPERTY)
                        .build())
                .property(PrimitivePropertyBuilder
                        .create(Datatypes.Integer, "value2")
                        .scope(PropertyScope.DIMENSION_PROPERTY)
                        .build())
                .build().eventSchema;

        Map<String, Object> map = new HashMap<>();
        map.put("value1",value1);
        map.put("value2",value2);

        return EventFactory.fromMap(map,new SourceInfo("", "s0"),new SchemaInfo(eventSchema,new ArrayList<>()));
    }
}
