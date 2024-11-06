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

import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MathOpProcessorTest {

    private MathOpProcessor processor;

    @BeforeEach
    public void setup() {
        processor = new MathOpProcessor();
    }

    @Test
    public void detectAdditionOperator() {
        TestConfiguration leftOperandConfiguration = TestConfiguration.builder()
                .configWithDefaultPrefix(MathOpProcessor.LEFT_OPERAND, "leftOperand")
                .configWithDefaultPrefix(MathOpProcessor.RIGHT_OPERAND, "rightOperand")
                .config(MathOpProcessor.OPERATION, "+").build();

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d));
        List<Map<String, Object>> outputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d, "calculationResult", 10.0d));
        ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(
                processor, leftOperandConfiguration);

        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void detectSubtractionOperator() {
        TestConfiguration leftOperandConfiguration = TestConfiguration.builder()
                .configWithDefaultPrefix(MathOpProcessor.LEFT_OPERAND, "leftOperand")
                .configWithDefaultPrefix(MathOpProcessor.RIGHT_OPERAND, "rightOperand")
                .config(MathOpProcessor.OPERATION, "-").build();

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d));
        List<Map<String, Object>> outputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d, "calculationResult", 0.0d));
        ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(
                processor, leftOperandConfiguration);

        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void detectMultiplicationOperator() {
        TestConfiguration leftOperandConfiguration = TestConfiguration.builder()
                .configWithDefaultPrefix(MathOpProcessor.LEFT_OPERAND, "leftOperand")
                .configWithDefaultPrefix(MathOpProcessor.RIGHT_OPERAND, "rightOperand")
                .config(MathOpProcessor.OPERATION, "*").build();

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d));
        List<Map<String, Object>> outputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d, "calculationResult", 25.0d));
        ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(
                processor, leftOperandConfiguration);

        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void detectDivisionOperator() {
        TestConfiguration leftOperandConfiguration = TestConfiguration.builder()
                .configWithDefaultPrefix(MathOpProcessor.LEFT_OPERAND, "leftOperand")
                .configWithDefaultPrefix(MathOpProcessor.RIGHT_OPERAND, "rightOperand")
                .config(MathOpProcessor.OPERATION, "/").build();

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d));
        List<Map<String, Object>> outputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d, "calculationResult", 1.0d));
        ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(
                processor, leftOperandConfiguration);

        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void detectModulusOperator() {
        TestConfiguration leftOperandConfiguration = TestConfiguration.builder()
                .configWithDefaultPrefix(MathOpProcessor.LEFT_OPERAND, "leftOperand")
                .configWithDefaultPrefix(MathOpProcessor.RIGHT_OPERAND, "rightOperand")
                .config(MathOpProcessor.OPERATION, "%").build();

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d));
        List<Map<String, Object>> outputEvents = List.of(
                Map.of("leftOperand", 5.0d, "rightOperand", 5.0d, "calculationResult", 0.0d));
        ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(
                processor, leftOperandConfiguration);

        testExecutor.run(inputEvents, outputEvents);
    }
}