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


package org.apache.streampipes.extensions.api.memorymanager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpMemoryManagerTest {

  private final SpMemoryManager manager = SpMemoryManager.INSTANCE;

  @AfterAll
  static void shutdownScheduler() {
    SpMemoryManager.shutdown();
  }

  @Test
  void releasesAReservationExactlyOnce() throws InterruptedException {
    long initial = manager.getFreeMemory();
    var reservation = manager.reserve(16);
    assertEquals(initial - 16, manager.getFreeMemory());
    reservation.close();
    reservation.close();
    assertEquals(initial, manager.getFreeMemory());
  }

  @Test
  void rejectsInvalidOrImpossibleReservationsWithoutChangingTheBudget() {
    long initial = manager.getFreeMemory();
    assertThrows(IllegalArgumentException.class, () -> manager.reserve(0));
    assertThrows(IllegalArgumentException.class, () -> manager.reserve(-1));
    assertThrows(IllegalArgumentException.class, () -> manager.reserve(initial + 1));
    assertEquals(initial, manager.getFreeMemory());
  }

  @Test
  void interruptionDoesNotCreateAReservation() {
    long initial = manager.getFreeMemory();
    Thread.currentThread().interrupt();
    try {
      assertThrows(InterruptedException.class, () -> manager.reserve(16));
      assertEquals(initial, manager.getFreeMemory());
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  void waitsForOutstandingReservationsAndResumesAfterRelease() throws Exception {
    long initial = manager.getFreeMemory();
    var executor = Executors.newSingleThreadExecutor();
    var entered = new CountDownLatch(1);
    var held = manager.reserve(initial);
    try {
      var pending = executor.submit(() -> {
        entered.countDown();
        try (var reservation = manager.reserve(16)) {
          return true;
        }
      });
      assertTrue(entered.await(1, TimeUnit.SECONDS));
      assertThrows(java.util.concurrent.TimeoutException.class, () -> pending.get(50, TimeUnit.MILLISECONDS));
      held.close();
      assertTrue(pending.get(2, TimeUnit.SECONDS));
      assertEquals(initial, manager.getFreeMemory());
    } finally {
      held.close();
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
    }
  }

  @Test
  void thresholdBlocksAcquisitionUntilReservationsDrain() throws Exception {
    long initial = manager.getFreeMemory();
    var executor = Executors.newSingleThreadExecutor();
    var held = manager.reserve(initial - 1);
    try {
      manager.scheduledTask();
      assertTrue(manager.isMemoryBlocked());
      var pending = executor.submit(() -> {
        try (var reservation = manager.reserve(1)) {
          return true;
        }
      });
      assertThrows(java.util.concurrent.TimeoutException.class, () -> pending.get(50, TimeUnit.MILLISECONDS));
      held.close();
      assertTrue(pending.get(2, TimeUnit.SECONDS));
      assertFalse(manager.isMemoryBlocked());
      assertEquals(initial, manager.getFreeMemory());
    } finally {
      held.close();
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
    }
  }
}
