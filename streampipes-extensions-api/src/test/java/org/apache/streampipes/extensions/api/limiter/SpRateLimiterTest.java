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


package org.apache.streampipes.extensions.api.limiter;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpRateLimiterTest {

  private final SpRateLimiter limiter = SpRateLimiter.INSTANCE;

  @BeforeEach
  void setup() {
    limiter.reset();
    limiter.createRateLimiter(1, 0, TimeUnit.SECONDS);
  }

  @AfterEach
  void reset() {
    limiter.reset();
  }

  @Test
  void rejectsInvalidByteCounts() {
    assertThrows(IllegalArgumentException.class, () -> limiter.acquire(0));
    assertThrows(IllegalArgumentException.class, () -> limiter.acquire((long) Integer.MAX_VALUE + 1));
  }

  @Test
  void failsFastIfUninitialized() {
    limiter.reset();
    assertThrows(IllegalStateException.class, () -> limiter.acquire(1));
  }

  @Test
  void retriesTimedOutAdmissionRatherThanDroppingTheEvent() throws Exception {
    limiter.acquire(2);
    var executor = Executors.newSingleThreadExecutor();
    try {
      var pending = executor.submit(() -> {
        limiter.acquire(1);
        return true;
      });
      assertThrows(java.util.concurrent.TimeoutException.class, () -> pending.get(1200, TimeUnit.MILLISECONDS));
      assertTrue(pending.get(2, TimeUnit.SECONDS));
    } finally {
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
    }
  }

  @Test
  void shortPermitWaitsDoNotPayAFullPollingIntervalPerEvent() throws InterruptedException {
    int events = 200;
    int bytes = 1000;
    var reference = RateLimiter.create(1_000_000, 1, TimeUnit.SECONDS);
    long started = System.nanoTime();
    for (int i = 0; i < events; i++) {
      assertTrue(reference.tryAcquire(bytes, 1, TimeUnit.SECONDS));
    }
    long referenceNanos = System.nanoTime() - started;

    limiter.reset();
    limiter.createRateLimiter(1_000_000, 1, TimeUnit.SECONDS);
    started = System.nanoTime();
    for (int i = 0; i < events; i++) {
      limiter.acquire(bytes);
    }
    long elapsedNanos = System.nanoTime() - started;
    // Compare with the same Guava workload on this machine, with generous slack
    // for scheduling. The former polling loop took at least 10 ms per event.
    assertTrue(elapsedNanos < Math.max(TimeUnit.SECONDS.toNanos(1), referenceNanos * 2),
        "Short admission waits were rounded up: " + TimeUnit.NANOSECONDS.toMillis(elapsedNanos) + " ms");
  }

  @Test
  void waitingAdmissionIsInterruptibleAndDoesNotAdmitTheEvent() throws Exception {
    limiter.acquire(1000);
    var entered = new CountDownLatch(1);
    var interrupted = new AtomicBoolean();
    var admitted = new AtomicBoolean();
    var waiting = new Thread(() -> {
      entered.countDown();
      try {
        limiter.acquire(1);
        admitted.set(true);
      } catch (InterruptedException e) {
        interrupted.set(true);
      }
    });
    waiting.start();
    try {
      assertTrue(entered.await(1, TimeUnit.SECONDS));
      waiting.interrupt();
      waiting.join(2000);
      assertFalse(waiting.isAlive());
      assertTrue(interrupted.get());
      assertFalse(admitted.get());
      assertEquals(0, limiter.getCurrentQueueSize());
    } finally {
      waiting.interrupt();
      waiting.join(2000);
    }
  }
}
