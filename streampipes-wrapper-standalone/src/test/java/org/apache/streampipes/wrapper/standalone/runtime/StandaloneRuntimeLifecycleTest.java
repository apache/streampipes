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

import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.pe.param.IInternalRuntimeParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpInputCollector;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.EventFactory;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StandaloneRuntimeLifecycleTest {

  @Test
  void preservesStartupAndShutdownOrderAndResolvesCollectorsOnlyOnce() {
    for (boolean processor : List.of(true, false)) {
      var fixture = new Fixture(processor);
      fixture.start.run();
      fixture.runtime.stopRuntime();
      assertEquals(1, fixture.lookups);
      var expected = new ArrayList<>(List.of("started", "register1", "register2"));
      if (processor) {
        expected.add("outputConnect");
      }
      expected.addAll(List.of("connect1", "connect2", "unregister1", "unregister2"));
      if (!processor) {
        expected.add("stopped");
      }
      expected.addAll(List.of("disconnect1", "disconnect2"));
      if (processor) {
        expected.addAll(List.of("stopped", "outputDisconnect"));
      }
      expected.add("monitoring");
      assertEquals(expected, fixture.calls);
    }
  }

  @Test
  void partialConnectionFailureRollsBackAndStopsTheElementOnlyOnce() {
    for (boolean processor : List.of(true, false)) {
      var fixture = new Fixture(processor);
      var failure = new IllegalStateException("connect failed");
      doThrow(failure).when(fixture.second).connect();
      assertSame(failure, assertThrows(IllegalStateException.class, fixture.start::run));
      verify(fixture.first).unregisterConsumer("test-runtime");
      verify(fixture.second).unregisterConsumer("test-runtime");
      verify(fixture.first).disconnect();
      verify(fixture.second).disconnect();
      if (processor) {
        verify(fixture.output).disconnect();
      }
      assertTrue(fixture.calls.contains("monitoring"));
      fixture.runtime.stopRuntime();
      fixture.verifyStopped(1);
    }
  }

  @Test
  void failedStartCallbackDoesNotTriggerStopCallback() {
    for (boolean processor : List.of(true, false)) {
      var fixture = new Fixture(processor);
      var failure = new IllegalStateException("start callback failed");
      if (processor) {
        doThrow(failure).when(fixture.processor).onPipelineStarted(any(), any(), any());
      } else {
        doThrow(failure).when(fixture.sink).onPipelineStarted(any(), any());
      }
      assertSame(failure, assertThrows(IllegalStateException.class, fixture.start::run));
      fixture.verifyStopped(0);
      verify(fixture.first).disconnect();
      verify(fixture.second).disconnect();
      if (processor) {
        verify(fixture.output).disconnect();
      }
      assertTrue(fixture.calls.contains("monitoring"));
    }
  }

  @Test
  void cleanupAttemptsEveryResourceAndRetainsAllFailures() {
    for (boolean processor : List.of(true, false)) {
      var fixture = new Fixture(processor);
      fixture.start.run();
      var unregisterFailure = new IllegalStateException("unregister failed");
      var disconnectFailure = new IllegalStateException("disconnect failed");
      var stopFailure = new IllegalStateException("stop callback failed");
      doThrow(unregisterFailure).when(fixture.first).unregisterConsumer("test-runtime");
      doThrow(disconnectFailure).when(fixture.first).disconnect();
      if (processor) {
        doThrow(stopFailure).when(fixture.processor).onPipelineStopped();
      } else {
        doThrow(stopFailure).when(fixture.sink).onPipelineStopped();
      }
      var failure = assertThrows(IllegalStateException.class, fixture.runtime::stopRuntime);
      assertSame(unregisterFailure, failure);
      assertTrue(containsFailure(failure, disconnectFailure));
      assertTrue(containsFailure(failure, stopFailure));
      verify(fixture.second).unregisterConsumer("test-runtime");
      verify(fixture.second).disconnect();
      if (processor) {
        verify(fixture.output).disconnect();
      }
      assertTrue(fixture.calls.contains("monitoring"));
      assertThrows(IllegalStateException.class, fixture.runtime::stopRuntime);
      fixture.verifyStopped(1);
    }
  }

  @Test
  void startupFailureRemainsPrimaryWhenCleanupAlsoFails() {
    for (boolean processor : List.of(true, false)) {
      var fixture = new Fixture(processor);
      var startFailure = new IllegalStateException("connect failed");
      var cleanupFailure = new IllegalStateException("disconnect failed");
      doThrow(startFailure).when(fixture.second).connect();
      doThrow(cleanupFailure).when(fixture.first).disconnect();
      var failure = assertThrows(IllegalStateException.class, fixture.start::run);
      assertSame(startFailure, failure);
      assertTrue(containsFailure(failure, cleanupFailure));
      verify(fixture.second).disconnect();
      fixture.verifyStopped(1);
      assertTrue(fixture.calls.contains("monitoring"));
    }
  }

  @Test
  void reusingAnExceptionDoesNotInterruptCleanupWithSelfSuppression() {
    var fixture = new Fixture(true);
    fixture.start.run();
    var failure = new IllegalStateException("shared failure");
    doThrow(failure).when(fixture.first).unregisterConsumer("test-runtime");
    doThrow(failure).when(fixture.second).unregisterConsumer("test-runtime");
    doThrow(failure).when(fixture.first).disconnect();
    assertSame(failure, assertThrows(IllegalStateException.class, fixture.runtime::stopRuntime));
    verify(fixture.second).disconnect();
    verify(fixture.output).disconnect();
    fixture.verifyStopped(1);
    assertTrue(fixture.calls.contains("monitoring"));
  }

  @Test
  void conversionAndCallbackFailuresAreReportedThroughTheSharedEventPath() {
    for (boolean processor : List.of(true, false)) {
      var fixture = new Fixture(processor);
      fixture.start.run();
      var internal = mock(IInternalRuntimeParameters.class);
      fixture.runtime.internalRuntimeParameters = internal;
      var payload = Map.<String, Object>of("value", 42);
      var conversionFailure = new IllegalArgumentException("missing field");
      when(internal.makeEvent(any(), eq(payload), eq("source"))).thenThrow(conversionFailure);
      assertDoesNotThrow(() -> fixture.runtime.process(payload, 32, "source"));
      verify(fixture.logger).error(conversionFailure);

      var event = EventFactory.fromMap(payload);
      doReturn(event).when(internal).makeEvent(any(), eq(payload), eq("source"));
      var callbackFailure = new IllegalStateException("callback failed");
      if (processor) {
        doThrow(callbackFailure).when(fixture.processor).onEvent(event, fixture.output);
      } else {
        doThrow(callbackFailure).when(fixture.sink).onEvent(event);
      }
      assertDoesNotThrow(() -> fixture.runtime.process(payload, 32, "source"));
      verify(fixture.logger).error(callbackFailure);
      fixture.runtime.stopRuntime();
    }
  }

  private boolean containsFailure(Throwable failure, Throwable expected) {
    return failure == expected
        || Arrays.stream(failure.getSuppressed()).anyMatch(suppressed -> containsFailure(suppressed, expected));
  }

  private static class Fixture {
    private final List<String> calls = new ArrayList<>();
    private final SpInputCollector first = mock(SpInputCollector.class);
    private final SpInputCollector second = mock(SpInputCollector.class);
    private final SpOutputCollector output = mock(SpOutputCollector.class);
    private final IStreamPipesDataProcessor processor = mock(IStreamPipesDataProcessor.class);
    private final IStreamPipesDataSink sink = mock(IStreamPipesDataSink.class);
    private final IExtensionsLogger logger = mock(IExtensionsLogger.class);
    private final StandalonePipelineElementRuntime<?, ?, ?, ?, ?> runtime;
    private final Runnable start;
    private final boolean isProcessor;
    private int lookups;

    Fixture(boolean isProcessor) {
      this.isProcessor = isProcessor;
      if (isProcessor) {
        var processorRuntime = new StandaloneEventProcessorRuntime() {
          @Override
          protected List<SpInputCollector> getInputCollectors(List<SpDataStream> streams) {
            lookups++;
            return List.of(first, second);
          }

          @Override
          public SpOutputCollector getOutputCollector() {
            return output;
          }

          @Override
          protected void removeMonitoring(String resourceId) {
            calls.add("monitoring");
            super.removeMonitoring(resourceId);
          }
        };
        var invocation = new DataProcessorInvocation();
        invocation.setElementId("test-runtime");
        var params = mock(IDataProcessorParameters.class);
        var context = mock(EventProcessorRuntimeContext.class);
        when(context.getLogger()).thenReturn(logger);
        start = () -> processorRuntime.startRuntime(invocation, processor, params, context);
        runtime = processorRuntime;
        doAnswer(call -> calls.add("started")).when(processor).onPipelineStarted(params, output, context);
        doAnswer(call -> calls.add("stopped")).when(processor).onPipelineStopped();
      } else {
        var sinkRuntime = new StandaloneEventSinkRuntime() {
          @Override
          protected List<SpInputCollector> getInputCollectors(List<SpDataStream> streams) {
            lookups++;
            return List.of(first, second);
          }

          @Override
          protected void removeMonitoring(String resourceId) {
            calls.add("monitoring");
            super.removeMonitoring(resourceId);
          }
        };
        var invocation = new DataSinkInvocation();
        invocation.setElementId("test-runtime");
        var params = mock(IDataSinkParameters.class);
        var context = mock(EventSinkRuntimeContext.class);
        when(context.getLogger()).thenReturn(logger);
        start = () -> sinkRuntime.startRuntime(invocation, sink, params, context);
        runtime = sinkRuntime;
        doAnswer(call -> calls.add("started")).when(sink).onPipelineStarted(params, context);
        doAnswer(call -> calls.add("stopped")).when(sink).onPipelineStopped();
      }
      doAnswer(call -> calls.add("register1")).when(first).registerConsumer("test-runtime", runtime);
      doAnswer(call -> calls.add("register2")).when(second).registerConsumer("test-runtime", runtime);
      doAnswer(call -> calls.add("connect1")).when(first).connect();
      doAnswer(call -> calls.add("connect2")).when(second).connect();
      doAnswer(call -> calls.add("unregister1")).when(first).unregisterConsumer("test-runtime");
      doAnswer(call -> calls.add("unregister2")).when(second).unregisterConsumer("test-runtime");
      doAnswer(call -> calls.add("disconnect1")).when(first).disconnect();
      doAnswer(call -> calls.add("disconnect2")).when(second).disconnect();
      doAnswer(call -> calls.add("outputConnect")).when(output).connect();
      doAnswer(call -> calls.add("outputDisconnect")).when(output).disconnect();
    }

    void verifyStopped(int count) {
      if (isProcessor) {
        verify(processor, times(count)).onPipelineStopped();
      } else {
        verify(sink, times(count)).onPipelineStopped();
      }
    }
  }
}
