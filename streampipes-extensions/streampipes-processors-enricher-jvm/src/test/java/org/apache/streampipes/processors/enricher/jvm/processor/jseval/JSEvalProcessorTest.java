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

package org.apache.streampipes.processors.enricher.jvm.processor.jseval;

import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class JSEvalProcessorTest {

    private static final String JS_FUNCTION = "jsFunction";

    private JSEvalProcessor processor;

    @BeforeEach
    public void setup() {
        processor = new JSEvalProcessor();
    }

    @Test
    public void testSimpleArithmeticOperation() {
        String jsFunction = """
        function process(event) {
          return {
            result: event.value1 + event.value2,
            original_value1: event.value1,
            original_value2: event.value2
          };
        }
        """;

        List<Map<String, Object>> inputEvents = List.of(Map.of(
                "value1", 10,
                "value2", 5
        ));

        List<Map<String, Object>> outputEvents = List.of(Map.of(
                "result", 15,
                "original_value1", 10,
                "original_value2", 5
        ));

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testStringManipulation() {
        String jsFunction = """
        function process(event) {
          return {
            full_name: event.first_name + " " + event.last_name,
            name_length: (event.first_name + " " + event.last_name).length,
            first_name_upper: event.first_name.toUpperCase()
          };
        }
        """;

        List<Map<String, Object>> inputEvents = List.of(Map.of(
                "first_name", "John",
                "last_name", "Doe"
        ));

        List<Map<String, Object>> outputEvents = List.of(Map.of(
                "full_name", "John Doe",
                "name_length", 8,
                "first_name_upper", "JOHN"
        ));

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testConditionalLogic() {
        String jsFunction = """
        function process(event) {
          var status = event.temperature > 30 ? "Hot" : "Cool";
          var alert = event.temperature > 50;
          
          return {
            temperature: event.temperature,
            status: status,
            alert: alert,
            fahrenheit: (event.temperature * 9/5) + 32
          };
        }
        """;

        List<Map<String, Object>> inputEvents = List.of(Map.of(
                "temperature", 25.5
        ));

        List<Map<String, Object>> outputEvents = List.of(Map.of(
                "temperature", 25.5,
                "status", "Cool",
                "alert", false,
                "fahrenheit", 77.9
        ));

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testMathOperations() {
        String jsFunction = """
        function process(event) {
          return {
            square: Math.pow(event.value, 2),
            sqrt: Math.sqrt(event.value),
            rounded: Math.round(event.decimal),
            max_value: Math.max(event.a, event.b, event.c)
          };
        }
        """;

        List<Map<String, Object>> inputEvents = List.of(Map.of(
                "value", 9,
                "decimal", 4.7,
                "a", 12,
                "b", 8,
                "c", 15
        ));

        List<Map<String, Object>> outputEvents = List.of(Map.of(
                "square", 81.0,
                "sqrt", 3.0,
                "rounded", 5,
                "max_value", 15.0
        ));

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testComplexObjectTransformation() {
        String jsFunction = """
        function process(event) {
          var avgScore = (event.score1 + event.score2 + event.score3) / 3;
          var grade = avgScore >= 90 ? "A" : 
                      avgScore >= 80 ? "B" : 
                      avgScore >= 70 ? "C" : 
                      avgScore >= 60 ? "D" : "F";
          
          return {
            student_id: event.student_id,
            average_score: Math.round(avgScore * 100) / 100,
            grade: grade,
            passed: avgScore >= 60,
            total_points: event.score1 + event.score2 + event.score3
          };
        }
        """;

        List<Map<String, Object>> inputEvents = List.of(Map.of(
                "student_id", "12345",
                "score1", 85,
                "score2", 92,
                "score3", 78
        ));

        List<Map<String, Object>> outputEvents = List.of(Map.of(
                "student_id", "12345",
                "average_score", 85,
                "grade", "B",
                "passed", true,
                "total_points", 255
        ));

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    static Stream<Arguments> parameterizedTestArguments() {
        return Stream.of(
                Arguments.of(
                        "function process(event) { return { doubled: event.value * 2 }; }",
                        Map.of("value", 5),
                        Map.of("doubled", 10)
                ),
                Arguments.of(
                        "function process(event) { return { is_positive: event.number > 0 }; }",
                        Map.of("number", -3),
                        Map.of("is_positive", false)
                ),
                Arguments.of(
                        "function process(event) { return { concat: event.prefix + '_' + event.suffix }; }",
                        Map.of("prefix", "hello", "suffix", "world"),
                        Map.of("concat", "hello_world")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("parameterizedTestArguments")
    public void testParameterizedJSEval(
            String jsFunction,
            Map<String, Object> inputEvent,
            Map<String, Object> expectedOutput
    ) {
        List<Map<String, Object>> inputEvents = List.of(inputEvent);
        List<Map<String, Object>> outputEvents = List.of(expectedOutput);

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testWithMultipleInputEvents() {
        String jsFunction = """
        function process(event) {
          return {
            sensor_id: event.sensor_id,
            processed_value: event.raw_value * 1.5 + 10,
            timestamp: event.timestamp
          };
        }
        """;

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("sensor_id", "S001", "raw_value", 20, "timestamp", 1000L),
                Map.of("sensor_id", "S002", "raw_value", 15, "timestamp", 2000L),
                Map.of("sensor_id", "S003", "raw_value", 30, "timestamp", 3000L)
        );

        List<Map<String, Object>> outputEvents = List.of(
                Map.of("sensor_id", "S001", "processed_value", 40.0, "timestamp", 1000),
                Map.of("sensor_id", "S002", "processed_value", 32.5, "timestamp", 2000),
                Map.of("sensor_id", "S003", "processed_value", 55.0, "timestamp", 3000)
        );

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testDateTimeOperations() {
        String jsFunction = """
        function process(event) {
          var date = new Date(event.timestamp);
          return {
            original_timestamp: event.timestamp,
            year: date.getFullYear(),
            month: date.getMonth() + 1,
            day: date.getDate(),
            hour: date.getHours(),
            is_weekend: date.getDay() === 0 || date.getDay() === 6
          };
        }
        """;

        // Using a specific timestamp: 2023-09-15 14:30:00 UTC (Friday)
        long timestamp = 1694786200000L;

        List<Map<String, Object>> inputEvents = List.of(Map.of(
                "timestamp", timestamp
        ));

        List<Map<String, Object>> outputEvents = List.of(Map.of(
                "original_timestamp", timestamp,
                "year", 2023.0,
                "month", 9.0,
                "day", 15.0,
                "hour", 19.0,
                "is_weekend", false
        ));

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    // Tests for stateful JavaScript functions using IIFE pattern

    @Test
    public void testStatefulEventCounter() {
        // Example from StreamPipes blog - counting events with stateful logic
        String jsFunction = """
        (() => {
          let count = 0;
          return function process(event) {
            count += 1;
            return {
              sensor_id: event.sensor_id,
              original_value: event.value,
              count: count
            };
          };
        })()
        """;

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("sensor_id", "S001", "value", 10),
                Map.of("sensor_id", "S002", "value", 20),
                Map.of("sensor_id", "S001", "value", 30)
        );

        List<Map<String, Object>> outputEvents = List.of(
                Map.of("sensor_id", "S001", "original_value", 10, "count", 1),
                Map.of("sensor_id", "S002", "original_value", 20, "count", 2),
                Map.of("sensor_id", "S001", "original_value", 30, "count", 3)
        );

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testStatefulRunningAverage() {
        // Stateful function to calculate running average
        String jsFunction = """
        (() => {
          let sum = 0;
          let count = 0;
          return function process(event) {
            sum += event.value;
            count += 1;
            return {
              value: event.value,
              running_sum: sum,
              running_count: count,
              running_average: sum / count
            };
          };
        })()
        """;

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("value", 10),
                Map.of("value", 20),
                Map.of("value", 30)
        );

        List<Map<String, Object>> outputEvents = List.of(
                Map.of("value", 10, "running_sum", 10, "running_count", 1, "running_average", 10), // Integer, not 10.0
                Map.of("value", 20, "running_sum", 30, "running_count", 2, "running_average", 15), // Integer, not 15.0
                Map.of("value", 30, "running_sum", 60, "running_count", 3, "running_average", 20)  // Integer, not 20.0
        );

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testStatefulThresholdDetection() {
        // Stateful function to detect threshold breaches with state memory
        String jsFunction = """
        (() => {
          let previousValue = null;
          let breachCount = 0;
          const threshold = 50;
          
          return function process(event) {
            let currentValue = event.temperature;
            let isBreach = currentValue > threshold;
            let isIncreasing = previousValue !== null && currentValue > previousValue;
            
            if (isBreach) {
              breachCount += 1;
            }
            
            let result = {
              temperature: currentValue,
              threshold_breach: isBreach,
              is_increasing: isIncreasing,
              total_breaches: breachCount,
              previous_temperature: previousValue
            };
            
            previousValue = currentValue;
            return result;
          };
        })()
        """;

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("temperature", 30),
                Map.of("temperature", 60),  // First breach
                Map.of("temperature", 40),
                Map.of("temperature", 70)   // Second breach
        );

        List<Map<String, Object>> outputEvents = List.of(
                createMapWithNull("temperature", 30, "threshold_breach", false, "is_increasing", false,
                        "total_breaches", 0, "previous_temperature", null),
                Map.of("temperature", 60, "threshold_breach", true, "is_increasing", true,
                        "total_breaches", 1, "previous_temperature", 30),
                Map.of("temperature", 40, "threshold_breach", false, "is_increasing", false,
                        "total_breaches", 1, "previous_temperature", 60),
                Map.of("temperature", 70, "threshold_breach", true, "is_increasing", true,
                        "total_breaches", 2, "previous_temperature", 40)
        );

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    @Test
    public void testStatefulSessionTracker() {
        // Stateful function to track user sessions with timeout
        String jsFunction = """
    (() => {
      let lastActivityTime = null;
      let sessionCount = 0;
      const sessionTimeoutMs = 30000; // 30 seconds
      
      return function process(event) {
        let currentTime = event.timestamp;
        let isNewSession = false;
        let timeSinceLastActivity = 0;
        
        if (lastActivityTime === null || (currentTime - lastActivityTime) > sessionTimeoutMs) {
          sessionCount += 1;
          isNewSession = true;
          timeSinceLastActivity = 0; // Reset for new sessions
        } else {
          timeSinceLastActivity = currentTime - lastActivityTime;
        }
        
        let result = {
          user_id: event.user_id,
          activity: event.activity,
          timestamp: currentTime,
          session_number: sessionCount,
          is_new_session: isNewSession,
          time_since_last_activity: timeSinceLastActivity
        };
        
        lastActivityTime = currentTime; // Update AFTER calculating the difference
        return result;
      };
    })()
    """;

        long baseTime = 1000000;
        List<Map<String, Object>> inputEvents = List.of(
                Map.of("user_id", "user1", "activity", "login", "timestamp", baseTime),
                Map.of("user_id", "user1", "activity", "click", "timestamp", baseTime + 10000),  // Same session
                Map.of("user_id", "user1", "activity", "click", "timestamp", baseTime + 50000)   // New session (timeout)
        );

        List<Map<String, Object>> outputEvents = List.of(
                Map.of("user_id", "user1", "activity", "login", "timestamp", 1000000,
                        "session_number", 1, "is_new_session", true, "time_since_last_activity", 0),
                Map.of("user_id", "user1", "activity", "click", "timestamp", 1010000,
                        "session_number", 1, "is_new_session", false, "time_since_last_activity", 10000),
                Map.of("user_id", "user1", "activity", "click", "timestamp", 1050000,
                        "session_number", 2, "is_new_session", true, "time_since_last_activity", 0)
        );

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }


    @Test
    public void testStatefulWindowedSum() {
        // Stateful function implementing a simple sliding window sum
        String jsFunction = """
        (() => {
          let values = [];
          const windowSize = 3;
          
          return function process(event) {
            values.push(event.value);
            
            // Keep only the last windowSize values
            if (values.length > windowSize) {
              values.shift();
            }
            
            let windowSum = values.reduce((sum, val) => sum + val, 0);
            
            return {
              current_value: event.value,
              window_values: values.slice(), // Copy of the array
              window_sum: windowSum,
              window_size: values.length
            };
          };
        })()
        """;

        List<Map<String, Object>> inputEvents = List.of(
                Map.of("value", 10),
                Map.of("value", 20),
                Map.of("value", 30),
                Map.of("value", 40)  // This should cause the window to slide
        );

        List<Map<String, Object>> outputEvents = List.of(
                Map.of("current_value", 10, "window_values", List.of(10), "window_sum", 10, "window_size", 1),
                Map.of("current_value", 20, "window_values", List.of(10, 20), "window_sum", 30, "window_size", 2),
                Map.of("current_value", 30, "window_values", List.of(10, 20, 30), "window_sum", 60, "window_size", 3),
                Map.of("current_value", 40, "window_values", List.of(20, 30, 40), "window_sum", 90, "window_size", 3)
        );

        var configuration = TestConfiguration
                .builder()
                .config(JS_FUNCTION, jsFunction)
                .prefixStrategy(PrefixStrategy.SAME_PREFIX)
                .build();

        var testExecutor = new ProcessingElementTestExecutor(processor, configuration);
        testExecutor.run(inputEvents, outputEvents);
    }

    private Map<String, Object> createMapWithNull(Object... keyValuePairs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            map.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return map;
    }
}
