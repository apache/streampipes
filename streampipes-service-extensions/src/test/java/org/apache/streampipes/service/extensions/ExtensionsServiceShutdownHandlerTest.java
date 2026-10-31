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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpHttpErrorStatusCode;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntime;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerManagement;
import org.apache.streampipes.extensions.management.init.RunningInstances;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.function.FunctionShutdownResult;
import org.apache.streampipes.model.function.FunctionsShutdownResponse;
import org.apache.streampipes.model.graph.DataProcessorInvocation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExtensionsServiceShutdownHandlerTest {

  private static final String HEALTHY_INSTANCE = "healthy-instance";
  private static final String FAILING_INSTANCE = "failing-instance";

  private final RunningInstances runningInstances = RunningInstances.INSTANCE;
  private AdapterWorkerManagement adapterManagement;
  private StreamPipesClient client;
  private Supplier<FunctionsShutdownResponse> functionShutdown;

  @BeforeEach
  void setUp() {
    clearRunningInstances();
    adapterManagement = mock(AdapterWorkerManagement.class);
    when(adapterManagement.getAllRunningAdapterInstances()).thenReturn(List.of());
    client = mock(StreamPipesClient.class, RETURNS_DEEP_STUBS);
    functionShutdown = FunctionsShutdownResponse::new;
  }

  @AfterEach
  void tearDown() {
    clearRunningInstances();
  }

  @Test
  void detachesEveryPipelineElementEvenIfOneFails() {
    var healthy = mock(IStreamPipesRuntime.class);
    when(healthy.onRuntimeDetached(HEALTHY_INSTANCE)).thenReturn(new Response(HEALTHY_INSTANCE, true));
    var failing = mock(IStreamPipesRuntime.class);
    when(failing.onRuntimeDetached(FAILING_INSTANCE)).thenThrow(new IllegalStateException("boom"));
    runningInstances.add(FAILING_INSTANCE, invocation(FAILING_INSTANCE), failing);
    runningInstances.add(HEALTHY_INSTANCE, invocation(HEALTHY_INSTANCE), healthy);

    handler().onShutdown();

    verify(healthy).onRuntimeDetached(HEALTHY_INSTANCE);
    verify(failing).onRuntimeDetached(FAILING_INSTANCE);
    assertEquals(0, runningInstances.getRunningInstancesCount());
  }

  @Test
  void stopsEveryAdapterEvenIfOneFails() throws AdapterException {
    var failingAdapter = adapter("failing-adapter");
    var healthyAdapter = adapter("healthy-adapter");
    when(adapterManagement.getAllRunningAdapterInstances()).thenReturn(List.of(failingAdapter, healthyAdapter));
    doThrow(new AdapterException("boom")).when(adapterManagement).stopAdapter(failingAdapter);

    handler().onShutdown();

    verify(adapterManagement).stopAdapter(failingAdapter);
    verify(adapterManagement).stopAdapter(healthyAdapter);
  }

  @Test
  void persistsStateOfStatefulFunctionsOnly() {
    var state = Map.<String, Object>of("counter", 42);
    functionShutdown = () -> new FunctionsShutdownResponse(List.of(
        new FunctionShutdownResult("stateful", state),
        new FunctionShutdownResult("stateless", null)
    ));

    handler().onShutdown();

    verify(client.adminApi()).persistFunctionState("stateful", state);
    verify(client.adminApi(), never()).persistFunctionState("stateless", null);
  }

  @Test
  void unreachableCoreStopsPersistenceEarlyButNotLocalCleanup() {
    var runtime = mock(IStreamPipesRuntime.class);
    when(runtime.onRuntimeDetached(HEALTHY_INSTANCE)).thenReturn(new Response(HEALTHY_INSTANCE, true));
    runningInstances.add(HEALTHY_INSTANCE, invocation(HEALTHY_INSTANCE), runtime);
    var adapter = adapter("adapter");
    when(adapterManagement.getAllRunningAdapterInstances()).thenReturn(List.of(adapter));
    functionShutdown = () -> new FunctionsShutdownResponse(List.of(
        new FunctionShutdownResult("first", Map.of("counter", 1)),
        new FunctionShutdownResult("second", Map.of("counter", 2))
    ));
    var admin = client.adminApi();
    doThrow(new IllegalStateException("connection refused")).when(admin).persistFunctionState(any(), any());

    assertDoesNotThrow(() -> handler().onShutdown());

    verify(admin, times(1)).persistFunctionState(any(), any());
    verify(runtime).onRuntimeDetached(HEALTHY_INSTANCE);
    assertEquals(0, runningInstances.getRunningInstancesCount());
  }

  @Test
  void endpointErrorsDoNotStopPersistenceOfOtherFunctions() {
    functionShutdown = () -> new FunctionsShutdownResponse(List.of(
        new FunctionShutdownResult("first", Map.of("counter", 1)),
        new FunctionShutdownResult("second", Map.of("counter", 2))
    ));
    var admin = client.adminApi();
    doThrow(new SpHttpErrorStatusCode("404", 404)).when(admin).persistFunctionState(any(), any());

    handler().onShutdown();

    verify(admin, times(2)).persistFunctionState(any(), any());
  }

  @Test
  void toleratesMissingClient() {
    functionShutdown = () -> new FunctionsShutdownResponse(List.of(
        new FunctionShutdownResult("stateful", Map.of("counter", 1))
    ));
    var withoutClient = new ExtensionsServiceShutdownHandler(runningInstances, adapterManagement, functionShutdown,
        () -> {
          throw new IllegalStateException("no client");
        }, List.of());

    assertDoesNotThrow(withoutClient::onShutdown);
  }

  @Test
  void persistsFunctionStateBeforeStoppingPipelineElements() {
    var runtime = mock(IStreamPipesRuntime.class);
    when(runtime.onRuntimeDetached(HEALTHY_INSTANCE)).thenReturn(new Response(HEALTHY_INSTANCE, true));
    runningInstances.add(HEALTHY_INSTANCE, invocation(HEALTHY_INSTANCE), runtime);
    var state = Map.<String, Object>of("counter", 7);
    functionShutdown = () -> new FunctionsShutdownResponse(List.of(new FunctionShutdownResult("stateful", state)));
    var admin = client.adminApi();

    handler().onShutdown();

    var order = inOrder(admin, runtime);
    order.verify(admin).persistFunctionState("stateful", state);
    order.verify(runtime).onRuntimeDetached(HEALTHY_INSTANCE);
  }

  @Test
  void runsEveryAdditionalHookEvenIfOneFails() {
    var failing = mock(ExtensionServiceShutdownHook.class);
    doThrow(new IllegalStateException("boom")).when(failing).onShutdown();
    var healthy = mock(ExtensionServiceShutdownHook.class);

    handler(List.of(failing, healthy)).onShutdown();

    verify(failing).onShutdown();
    verify(healthy).onShutdown();
  }

  private ExtensionsServiceShutdownHandler handler() {
    return handler(List.of());
  }

  private ExtensionsServiceShutdownHandler handler(List<ExtensionServiceShutdownHook> hooks) {
    return new ExtensionsServiceShutdownHandler(runningInstances, adapterManagement, functionShutdown, () -> client,
        hooks);
  }

  private DataProcessorInvocation invocation(String elementId) {
    var invocation = new DataProcessorInvocation();
    invocation.setElementId(elementId);
    invocation.setAppId("app-" + elementId);
    return invocation;
  }

  private AdapterDescription adapter(String elementId) {
    var description = new AdapterDescription();
    description.setElementId(elementId);
    return description;
  }

  private void clearRunningInstances() {
    runningInstances.getRunningInstanceKeys().forEach(runningInstances::remove);
  }
}
