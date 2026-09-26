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

package org.apache.streampipes.audit.management;

import org.apache.streampipes.audit.api.AuditDetails;
import org.apache.streampipes.audit.api.AuditEvent;
import org.apache.streampipes.audit.api.AuditEventDefinition;
import org.apache.streampipes.audit.api.AuditEventProvider;
import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.events.AdapterCreatedDetails;
import org.apache.streampipes.audit.events.AdapterCreationReason;
import org.apache.streampipes.audit.events.AuthenticationAuditRecorder;
import org.apache.streampipes.audit.events.AuthenticationMethod;
import org.apache.streampipes.audit.events.StandardAuditEvents;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultAuditServiceTest {
  private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-25T12:00:00Z"), ZoneOffset.UTC);

  record CustomDetails(String message) implements AuditDetails { }

  @Test
  void recordsStandardAndDownstreamTypedEvents() {
    var custom = new AuditEventDefinition<>("bytefabrik.sample.create", CustomDetails.class);
    AuditEventProvider provider = () -> List.of(custom);
    var events = new ArrayList<AuditEvent<?>>();
    var service = new DefaultAuditService(List.of(new StandardAuditEvents(), provider), events::add, CLOCK);
    service.record(StandardAuditEvents.ADAPTER_CREATE, AuditOutcome.SUCCEEDED, "user-1", "1",
        new AdapterCreatedDetails("stream-1", null));
    service.record(custom, AuditOutcome.SUCCEEDED, "system:sample", null, new CustomDetails("safe"));
    service.close();
    assertEquals(2, events.size());
    assertEquals("user-1", events.getFirst().actor());
    assertEquals("adapter", events.getFirst().resourceType());
    assertEquals("1", events.getFirst().resourceId());
    assertEquals(null, events.getLast().resourceType());
    assertEquals(null, events.getLast().resourceId());
    assertEquals(CLOCK.instant(), events.getFirst().recordedAt());
    assertEquals(CLOCK.instant(), service.lastSuccessfulWrite());
  }

  @Test
  void resourceTypesBelongToRegisteredDefinitions() {
    var notebook = new AuditEventDefinition<>("bytefabrik.notebook.create", CustomDetails.class, "ai-notebook");
    var login = new AuditEventDefinition<>("sp.user.login", CustomDetails.class);
    AuditEventProvider provider = () -> List.of(notebook, login);
    var events = new ArrayList<AuditEvent<?>>();
    var service = new DefaultAuditService(List.of(provider), events::add, CLOCK);
    service.record(notebook, AuditOutcome.SUCCEEDED, "user-1", "urn:notebook:1", null);
    service.record(login, AuditOutcome.SUCCEEDED, "user-1", null, null);
    service.record(notebook, AuditOutcome.FAILED, "user-1", null, null);
    service.record(login, AuditOutcome.SUCCEEDED, "user-1", "unexpected-id", null);
    service.record(new AuditEventDefinition<>(notebook.id(), CustomDetails.class, "adapter"),
        AuditOutcome.SUCCEEDED, "user-1", "1", null);
    service.close();
    assertEquals(3, events.size());
    assertEquals("ai-notebook", events.getFirst().resourceType());
    assertEquals("urn:notebook:1", events.getFirst().resourceId());
    assertEquals(null, events.get(1).resourceType());
    assertEquals(null, events.get(1).resourceId());
    assertEquals("ai-notebook", events.getLast().resourceType());
    assertEquals(null, events.getLast().resourceId());
    assertEquals(2, service.failureCount());
    assertThrows(IllegalArgumentException.class,
        () -> new AuditEventDefinition<>("sp.user.login", CustomDetails.class, " "));
  }

  @Test
  void normalizesMissingActorsAndPreservesExplicitAttribution() {
    var events = new ArrayList<AuditEvent<?>>();
    var service = new DefaultAuditService(List.of(new StandardAuditEvents()), events::add, CLOCK);
    for (String actor : new String[]{null, "  ", "system:recovery", "user-1"}) {
      service.record(StandardAuditEvents.ADAPTER_CREATE, AuditOutcome.PARTIAL, actor, "adapter-1",
          new AdapterCreatedDetails("stream-1", AdapterCreationReason.STREAM_CREATION_REJECTED));
    }
    service.close();
    assertEquals(List.of("unknown", "unknown", "system:recovery", "user-1"),
        events.stream().map(AuditEvent::actor).toList());
    assertEquals(AdapterCreationReason.STREAM_CREATION_REJECTED,
        ((AdapterCreatedDetails) events.getFirst().details()).reasonCode());
    assertEquals(0, service.failureCount());
  }

  @Test
  void reasonCodesKeepTheirSerializedNames() throws Exception {
    var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    for (var reason : AdapterCreationReason.values()) {
      var details = new AdapterCreatedDetails("stream-1", reason);
      var json = mapper.writeValueAsString(details);
      assertEquals(reason.name(), mapper.readTree(json).path("reasonCode").asText());
      assertEquals(details, mapper.readValue(json, AdapterCreatedDetails.class));
    }
  }

  @Test
  void builtInAuthenticationEventsHaveNoResource() {
    var events = new ArrayList<AuditEvent<?>>();
    var service = new DefaultAuditService(List.of(new StandardAuditEvents()), events::add, CLOCK);
    var recorder = new AuthenticationAuditRecorder(service);
    recorder.loggedIn("user-1", AuthenticationMethod.PASSWORD);
    recorder.loginDenied(AuthenticationMethod.OAUTH2);
    recorder.loggedOut("user-1");
    service.close();
    assertEquals(List.of("sp.auth.login", "sp.auth.login", "sp.auth.logout"),
        events.stream().map(event -> event.definition().id()).toList());
    assertEquals(List.of(AuditOutcome.SUCCEEDED, AuditOutcome.DENIED, AuditOutcome.SUCCEEDED),
        events.stream().map(AuditEvent::outcome).toList());
    assertEquals("unknown", events.get(1).actor());
    for (var event : events) {
      assertNull(event.resourceType());
      assertNull(event.resourceId());
    }
    assertNull(events.getLast().details());
  }

  @Test
  void rejectsDuplicateProviderIds() {
    assertThrows(IllegalArgumentException.class, () -> new DefaultAuditService(
        List.of(new StandardAuditEvents(), new StandardAuditEvents()), event -> { }, CLOCK));
  }

  @Test
  void storageFailureCannotEscapeIntoBusinessOperation() {
    var service = new DefaultAuditService(List.of(new StandardAuditEvents()), event -> {
      throw new IllegalStateException("do not log a secret from storage");
    }, CLOCK);
    service.record(StandardAuditEvents.ADAPTER_CREATE, AuditOutcome.SUCCEEDED, "user-1", "1", null);
    service.close();
    assertEquals(1, service.failureCount());
    assertNull(service.lastSuccessfulWrite());
  }

  @Test
  void invalidOrOversizedEventsNeverReachStorage() {
    var events = new ArrayList<AuditEvent<?>>();
    var custom = new AuditEventDefinition<>("bytefabrik.sample.create", CustomDetails.class);
    var service = new DefaultAuditService(List.of(new StandardAuditEvents()), events::add, CLOCK);
    service.record(custom, AuditOutcome.SUCCEEDED, "user-1", null, new CustomDetails("safe"));
    service.record(StandardAuditEvents.ADAPTER_CREATE, AuditOutcome.SUCCEEDED, "user-1", null,
        new AdapterCreatedDetails("a".repeat(17000), null));
    service.close();
    assertEquals(2, service.failureCount());
    assertEquals(0, events.size());
  }

  record MutableDetails(List<String> values) implements AuditDetails { }

  @Test
  void slowStorageDoesNotBlockPublisherAndOverflowIsObservable() throws Exception {
    var entered = new CountDownLatch(1);
    var release = new CountDownLatch(1);
    var events = new ArrayList<AuditEvent<?>>();
    var custom = new AuditEventDefinition<>("bytefabrik.sample.edit", MutableDetails.class);
    var service = new DefaultAuditService(List.of(() -> List.of(custom)), event -> {
      entered.countDown();
      await(release);
      events.add(event);
    }, CLOCK, 1, Duration.ofSeconds(2));
    try {
      service.record(custom, AuditOutcome.SUCCEEDED, "user-1", null, new MutableDetails(List.of("first")));
      assertTrue(entered.await(2, TimeUnit.SECONDS));
      var values = new ArrayList<>(List.of("original"));
      assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
        service.record(custom, AuditOutcome.SUCCEEDED, "user-1", null, new MutableDetails(values));
        service.record(custom, AuditOutcome.SUCCEEDED, "user-1", null, new MutableDetails(List.of("overflow")));
      });
      values.set(0, "modified");
      assertEquals(1, service.status().queueDepth());
      assertEquals(1, service.status().droppedEventCount());
      assertEquals(0, service.status().writeFailureCount());
      assertTrue(service.status().degraded());
      assertNull(service.lastSuccessfulWrite());
      release.countDown();
      service.close();
      assertEquals(2, events.size());
      assertEquals(List.of("original"), ((MutableDetails) events.get(1).details()).values());
      assertEquals(CLOCK.instant(), events.get(1).recordedAt());
      assertTrue(service.status().available());
      assertTrue(service.status().degraded());
    } finally {
      release.countDown();
      service.close();
    }
  }

  @Test
  void shutdownDeadlineCountsUnconfirmedEventsAndClosesStoreOnceWorkerExits() throws Exception {
    var entered = new CountDownLatch(1);
    var closed = new CountDownLatch(1);
    var interrupted = new AtomicBoolean();
    AuditEventStore store = new AuditEventStore() {
      @Override
      public void append(AuditEvent<?> event) {
        entered.countDown();
        try {
          new CountDownLatch(1).await();
        } catch (InterruptedException e) {
          interrupted.set(true);
          Thread.currentThread().interrupt();
          throw new IllegalStateException("interrupted");
        }
      }

      @Override
      public void close() {
        closed.countDown();
      }
    };
    var service = new DefaultAuditService(List.of(new StandardAuditEvents()), store, CLOCK,
        2, Duration.ofMillis(50));
    try {
      record(service);
      assertTrue(entered.await(2, TimeUnit.SECONDS));
      record(service);
      assertTimeoutPreemptively(Duration.ofSeconds(1), service::close);
      assertTrue(closed.await(2, TimeUnit.SECONDS));
      assertTrue(interrupted.get());
      assertEquals(2, service.status().droppedEventCount());
      assertEquals(0, service.status().queueDepth());
      assertFalse(service.status().available());
      record(service);
      assertEquals(3, service.status().droppedEventCount());
      service.close();
      assertEquals(3, service.status().droppedEventCount());
    } finally {
      service.close();
    }
  }

  @Test
  void workerContinuesAfterFailedWriteAndDrainsOnShutdown() {
    var writes = new ArrayList<AuditEvent<?>>();
    var closed = new AtomicBoolean();
    AuditEventStore store = new AuditEventStore() {
      @Override
      public void append(AuditEvent<?> event) {
        writes.add(event);
        if (writes.size() == 1) {
          throw new IllegalStateException("unavailable");
        }
      }

      @Override
      public void close() {
        closed.set(true);
      }
    };
    var service = new DefaultAuditService(List.of(new StandardAuditEvents()), store, CLOCK);
    record(service);
    record(service);
    service.close();
    assertTrue(closed.get());
    assertEquals(2, writes.size());
    assertEquals(1, service.status().writeFailureCount());
    assertEquals(0, service.status().droppedEventCount());
    assertEquals(CLOCK.instant(), service.lastSuccessfulWrite());
  }

  @Test
  void initializesWithoutAnEventAndRetriesFailedInitializationBeforeWriting() throws Exception {
    var started = new CountDownLatch(1);
    var attempts = new AtomicInteger();
    var writes = new AtomicInteger();
    AuditEventStore store = new AuditEventStore() {
      @Override
      public void initialize() {
        started.countDown();
        if (attempts.incrementAndGet() == 1) {
          throw new IllegalStateException("temporarily unavailable");
        }
      }

      @Override
      public void append(AuditEvent<?> event) {
        assertEquals(2, attempts.get());
        writes.incrementAndGet();
      }
    };
    try (var service = new DefaultAuditService(List.of(new StandardAuditEvents()), store, CLOCK)) {
      assertTrue(started.await(2, TimeUnit.SECONDS));
      record(service);
      service.close();
      assertEquals(2, attempts.get());
      assertEquals(1, writes.get());
      assertEquals(1, service.status().failureCount());
      assertEquals(0, service.status().writeFailureCount());
    }
  }

  private void record(DefaultAuditService service) {
    service.record(StandardAuditEvents.ADAPTER_CREATE, AuditOutcome.SUCCEEDED, "user-1", "1", null);
  }

  private static void await(CountDownLatch latch) {
    try {
      if (!latch.await(3, TimeUnit.SECONDS)) {
        throw new IllegalStateException("Test latch timed out");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }
}
