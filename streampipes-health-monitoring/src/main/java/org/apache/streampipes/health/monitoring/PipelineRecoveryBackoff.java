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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class PipelineRecoveryBackoff {

  static final Duration DEFAULT_INITIAL_DELAY = Duration.ofSeconds(30);
  static final Duration DEFAULT_MAX_DELAY = Duration.ofMinutes(10);

  private final Clock clock;
  private final Duration initialDelay;
  private final Duration maxDelay;
  private final Map<RecoveryKey, RecoveryState> recoveryStates = new ConcurrentHashMap<>();

  PipelineRecoveryBackoff() {
    this(Clock.systemUTC(), DEFAULT_INITIAL_DELAY, DEFAULT_MAX_DELAY);
  }

  PipelineRecoveryBackoff(Duration healthCheckInterval) {
    this(
        Clock.systemUTC(),
        healthCheckInterval,
        max(DEFAULT_MAX_DELAY, healthCheckInterval)
    );
  }

  PipelineRecoveryBackoff(Clock clock,
                          Duration initialDelay,
                          Duration maxDelay) {
    this.clock = clock;
    this.initialDelay = initialDelay;
    this.maxDelay = maxDelay;
  }

  boolean isAttemptDue(String pipelineId,
                       String instanceId) {
    var state = recoveryStates.get(new RecoveryKey(pipelineId, instanceId));
    return state == null || !clock.instant().isBefore(state.nextAttemptAt());
  }

  RecoveryState recordFailure(String pipelineId,
                              String instanceId) {
    var key = new RecoveryKey(pipelineId, instanceId);
    return recoveryStates.compute(key, (ignored, previousState) -> {
      int failedAttempts = previousState == null ? 1 : previousState.failedAttempts() + 1;
      Duration delay = calculateDelay(failedAttempts);
      return new RecoveryState(failedAttempts, delay, clock.instant().plus(delay));
    });
  }

  int reset(String pipelineId,
            String instanceId) {
    var previousState = recoveryStates.remove(new RecoveryKey(pipelineId, instanceId));
    return previousState == null ? 0 : previousState.failedAttempts();
  }

  void retainOnly(Set<RecoveryKey> activeInstances) {
    recoveryStates.keySet().retainAll(activeInstances);
  }

  RecoveryState getState(String pipelineId,
                         String instanceId) {
    return recoveryStates.get(new RecoveryKey(pipelineId, instanceId));
  }

  private Duration calculateDelay(int failedAttempts) {
    int exponent = Math.min(failedAttempts - 1, 30);
    long multiplier = 1L << exponent;
    Duration calculatedDelay = initialDelay.multipliedBy(multiplier);
    return calculatedDelay.compareTo(maxDelay) > 0 ? maxDelay : calculatedDelay;
  }

  private static Duration max(Duration first,
                              Duration second) {
    return first.compareTo(second) >= 0 ? first : second;
  }

  record RecoveryKey(String pipelineId, String instanceId) {
  }

  record RecoveryState(int failedAttempts,
                       Duration delay,
                       Instant nextAttemptAt) {
  }
}
