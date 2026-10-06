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


package org.apache.streampipes.wrapper.standalone.runtime;

import org.apache.streampipes.extensions.api.limiter.SpRateLimiter;
import org.apache.streampipes.extensions.api.memorymanager.SpMemoryManager;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.param.IInternalRuntimeParameters;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StandaloneRuntimeAdmissionTest {

  @AfterAll
  static void cleanup() {
    SpMemoryManager.shutdown();
    SpMonitoringManager.INSTANCE.remove("processor");
    SpMonitoringManager.INSTANCE.remove("sink");
  }

  @Test
  void runtimesDoNotReserveOrReleaseTransportMemoryAgain() throws InterruptedException {
    // Direct dispatch must work without touching the transport's rate limiter.
    SpRateLimiter.INSTANCE.reset();
    long initial = SpMemoryManager.INSTANCE.getFreeMemory();
    var payload = Map.<String, Object>of("value", 42);
    Event event = EventFactory.fromMap(payload);
    var internal = mock(IInternalRuntimeParameters.class);
    when(internal.makeEvent(null, payload, "source")).thenReturn(event);

    var processor = new StandaloneEventProcessorRuntime();
    processor.instanceId = "processor";
    processor.internalRuntimeParameters = internal;
    processor.pipelineElement = mock(IStreamPipesDataProcessor.class);
    doAnswer(invocation -> {
      assertEquals(initial - 32, SpMemoryManager.INSTANCE.getFreeMemory());
      return null;
    }).when(processor.pipelineElement).onEvent(event, null);

    var sink = new StandaloneEventSinkRuntime();
    sink.instanceId = "sink";
    sink.internalRuntimeParameters = internal;
    sink.pipelineElement = mock(IStreamPipesDataSink.class);
    doAnswer(invocation -> {
      assertEquals(initial - 32, SpMemoryManager.INSTANCE.getFreeMemory());
      return null;
    }).when(sink.pipelineElement).onEvent(event);

    try (var reservation = SpMemoryManager.INSTANCE.reserve(32)) {
      processor.process(payload, 32, "source");
      sink.process(payload, 32, "source");
      verify(processor.pipelineElement).onEvent(event, null);
      verify(sink.pipelineElement).onEvent(event);
      assertEquals(initial - 32, SpMemoryManager.INSTANCE.getFreeMemory());
    }
    assertEquals(initial, SpMemoryManager.INSTANCE.getFreeMemory());
  }
}
