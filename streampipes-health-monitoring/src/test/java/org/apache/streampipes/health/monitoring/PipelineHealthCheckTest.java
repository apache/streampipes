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
package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.health.monitoring.model.ActiveResources;
import org.apache.streampipes.health.monitoring.model.HealthCheckData;
import org.apache.streampipes.health.monitoring.utils.HealthCheckUtils;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PipelineHealthCheckTest {

  @Test
  public void retriesRestorationBeyondPreviousLimit() {
    var pipeline = pipeline(processor("missing", "Missing processor"));
    var clock = new MutableClock();
    var recoveryBackoff = new PipelineRecoveryBackoff(
        clock,
        PipelineRecoveryBackoff.DEFAULT_INITIAL_DELAY,
        PipelineRecoveryBackoff.DEFAULT_MAX_DELAY
    );
    var healthCheck = healthCheck(pipeline, Map.of("missing", false), recoveryBackoff);

    for (int i = 0; i < 11; i++) {
      healthCheck.runCheck();
      clock.advance(PipelineRecoveryBackoff.DEFAULT_MAX_DELAY);
    }

    assertEquals(11, healthCheck.restoreAttempts("missing"));
    assertEquals(PipelineHealthStatus.FAILURE, pipeline.getHealthStatus());
  }

  @Test
  public void waitsUntilBackoffExpiresBeforeRetrying() {
    var pipeline = pipeline(processor("missing", "Missing processor"));
    var clock = new MutableClock();
    var recoveryBackoff = new PipelineRecoveryBackoff(
        clock,
        PipelineRecoveryBackoff.DEFAULT_INITIAL_DELAY,
        PipelineRecoveryBackoff.DEFAULT_MAX_DELAY
    );
    var healthCheck = healthCheck(pipeline, Map.of("missing", false), recoveryBackoff);

    healthCheck.runCheck();
    healthCheck.runCheck();

    assertEquals(1, healthCheck.restoreAttempts("missing"));
    assertEquals(PipelineHealthStatus.FAILURE, pipeline.getHealthStatus());

    clock.advance(PipelineRecoveryBackoff.DEFAULT_INITIAL_DELAY);
    healthCheck.runCheck();

    assertEquals(2, healthCheck.restoreAttempts("missing"));
  }

  @Test
  public void recoveredElementDoesNotMaskFailedElement() {
    var pipeline = pipeline(
        processor("recovered", "Recovered processor"),
        processor("missing", "Missing processor")
    );
    var healthCheck = healthCheck(pipeline, Map.of(
        "recovered", true,
        "missing", false
    ));

    healthCheck.runCheck();

    assertEquals(PipelineHealthStatus.FAILURE, pipeline.getHealthStatus());
  }

  @Test
  public void pipelineIsHealthyWhenAllMissingElementsAreRestored() {
    var pipeline = pipeline(
        processor("first", "First processor"),
        processor("second", "Second processor")
    );
    pipeline.setHealthStatus(PipelineHealthStatus.FAILURE);
    var healthCheck = healthCheck(pipeline, Map.of(
        "first", true,
        "second", true
    ));

    healthCheck.runCheck();

    assertEquals(PipelineHealthStatus.OK, pipeline.getHealthStatus());
  }

  @Test
  public void observedRunningElementClearsFailureAndBackoffState() {
    var pipeline = pipeline(processor("running", "Running processor"));
    pipeline.setHealthStatus(PipelineHealthStatus.FAILURE);
    var recoveryBackoff = new PipelineRecoveryBackoff();
    recoveryBackoff.recordFailure(pipeline.getPipelineId(), "running");
    var healthCheck = healthCheck(
        pipeline,
        Map.of(),
        recoveryBackoff,
        Set.of("running")
    );

    healthCheck.runCheck();

    assertEquals(PipelineHealthStatus.OK, pipeline.getHealthStatus());
    assertEquals(0, recoveryBackoff.reset(pipeline.getPipelineId(), "running"));
    assertEquals(0, healthCheck.restoreAttempts("running"));
  }

  private TestPipelineHealthCheck healthCheck(Pipeline pipeline,
                                              Map<String, Boolean> restorationResults) {
    return healthCheck(pipeline, restorationResults, new PipelineRecoveryBackoff());
  }

  private TestPipelineHealthCheck healthCheck(Pipeline pipeline,
                                              Map<String, Boolean> restorationResults,
                                              PipelineRecoveryBackoff recoveryBackoff) {
    return healthCheck(pipeline, restorationResults, recoveryBackoff, Set.of());
  }

  private TestPipelineHealthCheck healthCheck(Pipeline pipeline,
                                              Map<String, Boolean> restorationResults,
                                              PipelineRecoveryBackoff recoveryBackoff,
                                              Set<String> runningInstanceIds) {
    IPipelineStorage pipelineStorage = mock(IPipelineStorage.class);
    when(pipelineStorage.getElementById(pipeline.getPipelineId())).thenReturn(pipeline);
    when(pipelineStorage.updateElement(any())).thenAnswer(invocation -> invocation.getArgument(0));

    var resourceProvider = new ResourceProvider(pipelineStorage, null, null);
    var activeResources = new ActiveResources(
        List.of(pipeline),
        List.of(pipeline),
        List.of(),
        List.of()
    );
    var healthCheckData = new HealthCheckData(
        resourceProvider,
        activeResources,
        Map.of(),
        Map.of("service-id", new ExtensionInstanceHealth(Map.of(), runningInstanceIds))
    );

    return new TestPipelineHealthCheck(
        healthCheckData,
        resourceProvider,
        restorationResults,
        recoveryBackoff
    );
  }

  private Pipeline pipeline(DataProcessorInvocation... processors) {
    var pipeline = new Pipeline();
    pipeline.setPipelineId("pipeline-id");
    pipeline.setName("Pipeline");
    pipeline.setRunning(true);
    pipeline.setHealthStatus(PipelineHealthStatus.OK);
    pipeline.setSepas(List.of(processors));
    return pipeline;
  }

  private DataProcessorInvocation processor(String instanceId,
                                            String name) {
    var processor = new DataProcessorInvocation();
    processor.setElementId("urn:streampipes.org:spi:" + instanceId);
    processor.setName(name);
    return processor;
  }

  private static class TestPipelineHealthCheck extends PipelineHealthCheck {

    private final Map<String, Boolean> restorationResults;
    private final Map<String, Integer> restorationAttempts = new HashMap<>();

    TestPipelineHealthCheck(HealthCheckData healthCheckData,
                            ResourceProvider resourceProvider,
                            Map<String, Boolean> restorationResults,
                            PipelineRecoveryBackoff recoveryBackoff) {
      super(healthCheckData, null, resourceProvider, null, recoveryBackoff);
      this.restorationResults = restorationResults;
    }

    @Override
    protected boolean restorePipelineElement(InvocableStreamPipesEntity pipelineElement,
                                             String pipelineId) {
      var instanceId = HealthCheckUtils.extractInstanceId(pipelineElement);
      restorationAttempts.merge(instanceId, 1, Integer::sum);
      return restorationResults.get(instanceId);
    }

    int restoreAttempts(String instanceId) {
      return restorationAttempts.getOrDefault(instanceId, 0);
    }
  }

  private static class MutableClock extends Clock {

    private Instant instant = Instant.EPOCH;

    @Override
    public ZoneId getZone() {
      return ZoneId.of("UTC");
    }

    @Override
    public Clock withZone(ZoneId zone) {
      return this;
    }

    @Override
    public Instant instant() {
      return instant;
    }

    void advance(Duration duration) {
      instant = instant.plus(duration);
    }
  }
}
