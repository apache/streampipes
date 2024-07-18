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

package org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure;


import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureProcessor.BYTES_OPTION;
import static org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureProcessor.EVENT_SIZE;
import static org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureProcessor.KILO_BYTES_OPTION;
import static org.apache.streampipes.processors.enricher.jvm.processor.sizemeasure.SizeMeasureProcessor.SIZE_UNIT;

public class TestSizeMeasureProcessor {
  private static final String KEY = "key";
  private static final String VALUE = "value";

  private static Map<String, Object> inputEvent = Map.of(KEY, VALUE);

  private SizeMeasureProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new SizeMeasureProcessor();
  }


  static Stream<Arguments> arguments() {
    return Stream.of(
        Arguments.of(
            BYTES_OPTION,
            inputEvent,
            96.0
        ),
        Arguments.of(
            KILO_BYTES_OPTION,
            inputEvent,
            0.09375
        )
    );
  }

  @ParameterizedTest
  @MethodSource("arguments")
  public void testStringToState(
      String sizeUnit,
      Map<String, Object> intpuEvent,
      double expectedventSize
  ) {

    var inputEvents = List.of(intpuEvent);
    List<Map<String, Object>> outputEvents = List.of(Map.of(
        KEY, VALUE,
        EVENT_SIZE, expectedventSize
    ));

    var configuration = TestConfiguration
        .builder()
        .config(SIZE_UNIT, sizeUnit)
        .prefixStrategy(PrefixStrategy.SAME_PREFIX)
        .build();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }
}