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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.extensions.api.connect.IPollingSettings;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PullAdapterSchedulerTest {

  @Test
  void continuesSchedulingAfterRuntimeException() throws InterruptedException {
    var scheduler = new PullAdapterScheduler();
    var secondInvocation = new CountDownLatch(1);

    try {
      scheduler.schedule(new FailingOncePullAdapter(secondInvocation), "adapter-id");
      assertTrue(secondInvocation.await(2, TimeUnit.SECONDS));
    } finally {
      scheduler.shutdown();
    }
  }

  @Test
  void recordsOnlyFirstFailureUntilAdapterRecovers() throws InterruptedException {
    var adapterId = "repeatedly-failing-adapter";
    var scheduler = new PullAdapterScheduler();
    var fourthInvocation = new CountDownLatch(1);
    SpMonitoringManager.INSTANCE.remove(adapterId);

    try {
      scheduler.schedule(new RepeatedlyFailingPullAdapter(fourthInvocation), adapterId);
      assertTrue(fourthInvocation.await(2, TimeUnit.SECONDS));
    } finally {
      scheduler.shutdown();
    }

    try {
      var logEntries = SpMonitoringManager.INSTANCE.getMonitoringInfo()
          .getLogInfos()
          .get(adapterId);
      assertEquals(2, logEntries.size());
    } finally {
      SpMonitoringManager.INSTANCE.remove(adapterId);
    }
  }

  private static class FailingOncePullAdapter implements IPullAdapter {

    private final AtomicInteger invocations = new AtomicInteger();
    private final CountDownLatch secondInvocation;

    private FailingOncePullAdapter(CountDownLatch secondInvocation) {
      this.secondInvocation = secondInvocation;
    }

    @Override
    public void pullData() {
      if (invocations.incrementAndGet() == 1) {
        throw new IllegalStateException("first poll failed");
      }
      secondInvocation.countDown();
    }

    @Override
    public IPollingSettings getPollingInterval() {
      return PollingSettings.from(TimeUnit.MILLISECONDS, 10);
    }
  }

  private static class RepeatedlyFailingPullAdapter implements IPullAdapter {

    private final AtomicInteger invocations = new AtomicInteger();
    private final CountDownLatch fourthInvocation;

    private RepeatedlyFailingPullAdapter(CountDownLatch fourthInvocation) {
      this.fourthInvocation = fourthInvocation;
    }

    @Override
    public void pullData() {
      int invocation = invocations.incrementAndGet();
      try {
        if (invocation == 1 || invocation == 2 || invocation == 4) {
          throw new IllegalStateException("poll failed");
        }
      } finally {
        if (invocation == 4) {
          fourthInvocation.countDown();
        }
      }
    }

    @Override
    public IPollingSettings getPollingInterval() {
      return PollingSettings.from(TimeUnit.MILLISECONDS, 10);
    }
  }
}
