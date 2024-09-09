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

package org.apache.streampipes.processors.enricher.jvm.processor.limitsenrichment;

import org.apache.streampipes.test.executors.PrefixStrategy;
import org.apache.streampipes.test.executors.ProcessingElementTestExecutor;
import org.apache.streampipes.test.executors.TestConfiguration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class QualityControlLimitsEnrichmentTest {

  @Test
  void onEvent() {

    var processor = new QualityControlLimitsEnrichmentProcessor();

    List<Map<String, Object>> inputEvents = List.of(Map.of(
        "temperature", 10.1
    ));

    List<Map<String, Object>> outputEvents = List.of(Map.of(
        "temperature", 10.1,
        QualityControlLimitsEnrichmentProcessor.UPPER_CONTROL_LIMIT, 80.0,
        QualityControlLimitsEnrichmentProcessor.UPPER_WARNING_LIMIT, 70.0,
        QualityControlLimitsEnrichmentProcessor.LOWER_CONTROL_LIMIT, 30.0,
        QualityControlLimitsEnrichmentProcessor.LOWER_WARNING_LIMIT, 20.0
    ));

    var configuration = TestConfiguration
        .builder()
        .config(QualityControlLimitsEnrichmentProcessor.UPPER_CONTROL_LIMIT_LABEL, 80.0)
        .config(QualityControlLimitsEnrichmentProcessor.UPPER_WARNING_LIMIT_LABEL, 70.0)
        .config(QualityControlLimitsEnrichmentProcessor.LOWER_CONTROL_LIMIT_LABEL, 30.0)
        .config(QualityControlLimitsEnrichmentProcessor.LOWER_WARNING_LIMIT_LABEL, 20.0)
        .prefixStrategy(PrefixStrategy.SAME_PREFIX)
        .build();

    var testExecutor = new ProcessingElementTestExecutor(processor, configuration);

    testExecutor.run(inputEvents, outputEvents);
  }
}