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

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PipelineRecoveryBackoffTest {

  @Test
  public void delayIncreasesExponentiallyAndIsCapped() {
    var recoveryBackoff = recoveryBackoff();
    var expectedDelays = new long[]{30, 60, 120, 240, 480, 600, 600};

    for (long expectedDelay : expectedDelays) {
      var state = recoveryBackoff.recordFailure("pipeline", "instance");
      assertEquals(Duration.ofSeconds(expectedDelay), state.delay());
    }
  }

  @Test
  public void configuredHealthCheckIntervalIsUsedAsInitialDelay() {
    var recoveryBackoff = new PipelineRecoveryBackoff(Duration.ofMinutes(2));
    var expectedDelays = new long[]{2, 4, 8, 10, 10};

    for (long expectedDelay : expectedDelays) {
      var state = recoveryBackoff.recordFailure("pipeline", "instance");
      assertEquals(Duration.ofMinutes(expectedDelay), state.delay());
    }
  }

  @Test
  public void healthCheckIntervalAboveDefaultMaximumBecomesMaximumDelay() {
    var healthCheckInterval = Duration.ofMinutes(15);
    var recoveryBackoff = new PipelineRecoveryBackoff(healthCheckInterval);

    var firstFailure = recoveryBackoff.recordFailure("pipeline", "instance");
    var secondFailure = recoveryBackoff.recordFailure("pipeline", "instance");

    assertEquals(healthCheckInterval, firstFailure.delay());
    assertEquals(healthCheckInterval, secondFailure.delay());
  }

  @Test
  public void resetAllowsImmediateRecoveryAttempt() {
    var recoveryBackoff = recoveryBackoff();
    recoveryBackoff.recordFailure("pipeline", "instance");

    assertFalse(recoveryBackoff.isAttemptDue("pipeline", "instance"));
    assertEquals(1, recoveryBackoff.reset("pipeline", "instance"));
    assertTrue(recoveryBackoff.isAttemptDue("pipeline", "instance"));
  }

  @Test
  public void inactiveInstancesAreRemoved() {
    var recoveryBackoff = recoveryBackoff();
    recoveryBackoff.recordFailure("pipeline", "active");
    recoveryBackoff.recordFailure("pipeline", "inactive");

    recoveryBackoff.retainOnly(Set.of(
        new PipelineRecoveryBackoff.RecoveryKey("pipeline", "active")
    ));

    assertEquals(1, recoveryBackoff.getState("pipeline", "active").failedAttempts());
    assertNull(recoveryBackoff.getState("pipeline", "inactive"));
  }

  private PipelineRecoveryBackoff recoveryBackoff() {
    return new PipelineRecoveryBackoff(
        Clock.fixed(Instant.EPOCH, ZoneOffset.UTC),
        PipelineRecoveryBackoff.DEFAULT_INITIAL_DELAY,
        PipelineRecoveryBackoff.DEFAULT_MAX_DELAY
    );
  }
}
