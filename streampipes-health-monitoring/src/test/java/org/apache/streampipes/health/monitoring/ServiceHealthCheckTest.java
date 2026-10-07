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

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequest;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceHealthCheckTest {

  private IExtensionsServiceStorage storage;
  private ExtensionServiceRequestManager requests;
  private MockedStatic<ExtensionServiceRequests> requestFactory;
  private SpServiceRegistration stored;
  private ServiceHealthCheck check;
  private final AtomicLong now = new AtomicLong(1_000_000);

  @BeforeEach
  public void setUp() throws Exception {
    storage = mock(IExtensionsServiceStorage.class);
    requests = mock(ExtensionServiceRequestManager.class);
    var resources = mock(SpResourceManager.class);
    var clock = mock(Clock.class);
    when(clock.millis()).thenAnswer(invocation -> now.get());
    stored = new SpServiceRegistration();
    stored.setSvcId("edge");
    stored.setRev("1");
    stored.setStatus(SpServiceStatus.HEALTHY);
    stored.setHealthCheckPath("health");
    // CouchDB returns independent snapshots: mutating an update must not mutate the findAll result.
    when(storage.findAll()).thenAnswer(invocation -> List.of(snapshot()));
    when(storage.getElementById("edge")).thenAnswer(invocation -> snapshot());
    doAnswer(invocation -> {
      stored = invocation.getArgument(0);
      return null;
    }).when(storage).updateElement(any());
    requestFactory = mockStatic(ExtensionServiceRequests.class);
    requestFactory.when(() -> ExtensionServiceRequests.serviceHealth(any(), eq(resources)))
        .thenReturn(mock(ExtensionServiceRequest.class));
    failRequests();
    check = new ServiceHealthCheck(storage, requests, resources, 60_000, 3, clock);
  }

  @AfterEach
  public void tearDown() {
    requestFactory.close();
  }

  @Test
  public void requiresConsecutiveFailuresAndStartsFreshGracePeriod() {
    check.run();
    check.run();
    assertEquals(SpServiceStatus.HEALTHY, stored.getStatus());
    check.run();
    assertEquals(SpServiceStatus.UNHEALTHY, stored.getStatus());
    assertEquals(now.get(), stored.getFirstTimeSeenUnhealthy());
    verify(storage, never()).deleteElement(any());
  }

  @Test
  public void removesOnlyAfterElapsedGracePeriod() {
    failThreeTimes();
    now.addAndGet(59_999);
    check.run();
    verify(storage, never()).deleteElement(any());
    now.incrementAndGet();
    check.run();
    verify(storage).deleteElement(any());
  }

  @Test
  public void recoveryClearsHealthAndTimestampAndNextOutageGetsFreshGrace() throws Exception {
    failThreeTimes();
    now.addAndGet(120_000);
    succeedRequests();
    check.run();
    assertEquals(SpServiceStatus.HEALTHY, stored.getStatus());
    assertEquals(0, stored.getFirstTimeSeenUnhealthy());
    failRequests();
    check.run();
    check.run();
    assertEquals(SpServiceStatus.HEALTHY, stored.getStatus());
    check.run();
    assertEquals(now.get(), stored.getFirstTimeSeenUnhealthy());
    verify(storage, never()).deleteElement(any());
  }

  @Test
  public void successBreaksFailureStreakBeforeUnhealthy() throws Exception {
    check.run();
    check.run();
    succeedRequests();
    check.run();
    failRequests();
    check.run();
    check.run();
    assertEquals(SpServiceStatus.HEALTHY, stored.getStatus());
    verify(storage, never()).deleteElement(any());
  }

  @Test
  public void ioFailuresUseTheSameThreshold() throws Exception {
    when(requests.request(any())).thenThrow(new IOException("NATS unavailable"));
    failThreeTimes();
    assertEquals(SpServiceStatus.UNHEALTHY, stored.getStatus());
    verify(storage, never()).deleteElement(any());
  }

  @Test
  public void unhealthyRegistrationWithoutTimestampGetsAGracePeriod() {
    stored.setStatus(SpServiceStatus.UNHEALTHY);
    failThreeTimes();
    assertEquals(now.get(), stored.getFirstTimeSeenUnhealthy());
    verify(storage, never()).deleteElement(any());
  }

  @Test
  public void registeredServiceIsNotPromotedBySuccessfulHealthProbe() throws Exception {
    stored.setStatus(SpServiceStatus.REGISTERED);
    succeedRequests();
    check.run();
    assertEquals(SpServiceStatus.REGISTERED, stored.getStatus());
    verify(storage, never()).updateElement(any());
  }

  @Test
  public void failedMigrationExpiresOnlyAfterThresholdAndGracePeriod() {
    stored.setStatus(SpServiceStatus.MIGRATING);
    check.run();
    check.run();
    assertEquals(0, stored.getFirstTimeSeenUnhealthy());
    check.run();
    assertEquals(SpServiceStatus.MIGRATING, stored.getStatus());
    assertEquals(now.get(), stored.getFirstTimeSeenUnhealthy());
    now.addAndGet(59_999);
    check.run();
    verify(storage, never()).deleteElement(any());
    now.incrementAndGet();
    check.run();
    verify(storage).deleteElement(any());
  }

  @Test
  public void recoveryPreservesMigrationReadinessAndResetsFailureTracking() throws Exception {
    assertReadinessPreservedOnRecovery(SpServiceStatus.MIGRATING);
  }

  @Test
  public void recoveryPreservesRegistrationReadinessAndResetsFailureTracking() throws Exception {
    assertReadinessPreservedOnRecovery(SpServiceStatus.REGISTERED);
  }

  private void assertReadinessPreservedOnRecovery(SpServiceStatus status) throws Exception {
    stored.setStatus(status);
    failThreeTimes();
    assertEquals(status, stored.getStatus());
    now.addAndGet(120_000);
    succeedRequests();
    check.run();
    assertEquals(status, stored.getStatus());
    assertEquals(0, stored.getFirstTimeSeenUnhealthy());
    failRequests();
    check.run();
    check.run();
    assertEquals(0, stored.getFirstTimeSeenUnhealthy());
    check.run();
    assertEquals(status, stored.getStatus());
    assertEquals(now.get(), stored.getFirstTimeSeenUnhealthy());
    verify(storage, never()).deleteElement(any());
  }

  @Test
  public void concurrentMigrationCompletionWinsOverSuccessfulProbe() throws Exception {
    stored.setStatus(SpServiceStatus.MIGRATING);
    stored.setFirstTimeSeenUnhealthy(now.get());
    when(requests.request(any())).thenAnswer(invocation -> {
      stored.setStatus(SpServiceStatus.HEALTHY);
      stored.setRev("2");
      return new ExtensionServiceOperationResult(200, null);
    });
    check.run();
    assertEquals(SpServiceStatus.HEALTHY, stored.getStatus());
    verify(storage, never()).updateElement(any());
  }

  @Test
  public void recoveryUpdateRetainsProbedRevisionIfLifecycleChangesJustBeforeWrite() throws Exception {
    stored.setStatus(SpServiceStatus.UNHEALTHY);
    stored.setFirstTimeSeenUnhealthy(now.get());
    succeedRequests();
    doAnswer(invocation -> {
      SpServiceRegistration update = invocation.getArgument(0);
      assertEquals("1", update.getRev());
      stored.setStatus(SpServiceStatus.MIGRATING);
      stored.setRev("2");
      // Model CouchDB's optimistic concurrency rejection of the old revision.
      throw new IllegalStateException("revision conflict");
    }).when(storage).updateElement(any());
    check.run();
    assertEquals(SpServiceStatus.MIGRATING, stored.getStatus());
    assertEquals("2", stored.getRev());
  }

  @Test
  public void concurrentMigrationCompletionWinsOverFailedProbe() throws Exception {
    check.run();
    check.run();
    when(requests.request(any())).thenAnswer(invocation -> {
      stored.setStatus(SpServiceStatus.MIGRATING);
      stored.setRev("2");
      throw new IOException("probe failed");
    });
    check.run();
    assertEquals(SpServiceStatus.MIGRATING, stored.getStatus());
    verify(storage, never()).updateElement(any());
    verify(storage, never()).deleteElement(any());
  }

  @Test
  public void rejectsInvalidThresholds() {
    assertThrows(IllegalArgumentException.class,
        () -> new ServiceHealthCheck(storage, requests, null, 60_000, 0, Clock.systemUTC()));
    assertThrows(IllegalArgumentException.class,
        () -> new ServiceHealthCheck(storage, requests, null, -1, 3, Clock.systemUTC()));
  }

  private void failThreeTimes() {
    check.run();
    check.run();
    check.run();
  }

  private void failRequests() throws IOException {
    when(requests.request(any())).thenReturn(new ExtensionServiceOperationResult(503, null));
  }

  private void succeedRequests() throws IOException {
    when(requests.request(any())).thenReturn(new ExtensionServiceOperationResult(200, null));
  }

  private SpServiceRegistration snapshot() {
    var copy = new SpServiceRegistration();
    copy.setSvcId(stored.getSvcId());
    copy.setRev(stored.getRev());
    copy.setStatus(stored.getStatus());
    copy.setHealthCheckPath(stored.getHealthCheckPath());
    copy.setFirstTimeSeenUnhealthy(stored.getFirstTimeSeenUnhealthy());
    return copy;
  }
}
