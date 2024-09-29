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
package org.apache.streampipes.processors.filters.jvm.processor.merge;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;
import org.apache.streampipes.test.executors.TestConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestMergeByTimeProcessor {

  private static final String S0_PREFIX = "s0";
  private static final String S1_PREFIX = "s1";
  private static final Integer timeInterval = 100;

  MergeByTimeProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new MergeByTimeProcessor();
  }
  @ParameterizedTest
  @MethodSource("data")
  public void test(List<Map<String, Object>> events, List<Map<String, Object>> outputEvents,
          PrefixStrategy prefixStrategy, List<String> customPrefixes) {

    TestConfigurationBuilder configurationBuilder = TestConfiguration.builder()
            .config(MergeByTimeProcessor.TIME_INTERVAL, timeInterval)
            .configWithPrefix(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY,
                    MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, S0_PREFIX)
            .configWithPrefix(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY,
                    MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, S1_PREFIX);

    if (customPrefixes != null) {
      configurationBuilder.customPrefixStrategy(customPrefixes);
    } else {
      configurationBuilder.prefixStrategy(prefixStrategy);
    }

    Consumer<DataProcessorInvocation> invocationConfig = (invocation -> {
      List<String> outputKeySelectors = invocation.getOutputStrategies().stream()
              .filter(CustomOutputStrategy.class::isInstance).map(o -> (CustomOutputStrategy) o).findFirst()
              .map(CustomOutputStrategy::getSelectedPropertyKeys).orElse(new ArrayList<>());
      outputKeySelectors.add(S0_PREFIX + "::" + MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY);
      outputKeySelectors.add(S1_PREFIX + "::" + MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY);
    });

    ProcessingElementTestExecutor testExecutor = new ProcessingElementTestExecutor(processor,
            configurationBuilder.build(), invocationConfig);

    testExecutor.run(events, outputEvents);
  }

  static Stream<Arguments> data() {
    return Stream.of(
            Arguments.of(
                    List.of(Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "0"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "90")),
                    List.of(Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "90",
                            MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "0")),
                    PrefixStrategy.ALTERNATE, null),
            Arguments.of(
                    List.of(Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "0"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "110")),
                    List.of(), PrefixStrategy.ALTERNATE, null),
            Arguments.of(
                    List.of(Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "0"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "80"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "110"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "500")),
                    List.of(Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "80",
                            MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "0")),
                    PrefixStrategy.ALTERNATE, null),
            Arguments.of(
                    List.of(Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "0"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "10"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "110"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "115"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "120"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "230"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "340"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "500"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "510")),
                    List.of(Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "0",
                            MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "10"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "115",
                                    MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "110"),
                            Map.of(MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_2_KEY, "510",
                                    MergeByTimeProcessor.TIMESTAMP_MAPPING_STREAM_1_KEY, "500")),
                    null, List.of(S1_PREFIX, S0_PREFIX, S0_PREFIX, S1_PREFIX, S0_PREFIX, S1_PREFIX, S0_PREFIX,
                            S0_PREFIX, S1_PREFIX)));
  }
}