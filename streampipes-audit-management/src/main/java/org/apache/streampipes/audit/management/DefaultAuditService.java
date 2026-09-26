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
import org.apache.streampipes.audit.api.AuditEntryDetails;
import org.apache.streampipes.audit.api.AuditEvent;
import org.apache.streampipes.audit.api.AuditEventDefinition;
import org.apache.streampipes.audit.api.AuditEventProvider;
import org.apache.streampipes.audit.api.AuditEventReader;
import org.apache.streampipes.audit.api.AuditEventStore;
import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditPage;
import org.apache.streampipes.audit.api.AuditQuery;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.audit.api.AuditStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/** Bounded best-effort recording. Only the worker performs storage I/O and owns storage shutdown. */
public final class DefaultAuditService implements AuditService {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultAuditService.class);
  private static final int MAX_DETAILS_BYTES = 16 * 1024;
  private final Map<String, AuditEventDefinition<?>> definitions;
  private final AuditEventStore store;
  private final Clock clock;
  private final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
  private final AtomicLong failures = new AtomicLong();
  private final AtomicLong writeFailures = new AtomicLong();
  private final AtomicLong dropped = new AtomicLong();
  private final Object monitor = new Object();
  private final ArrayDeque<PendingEvent<?>> queue = new ArrayDeque<>();
  private final int capacity;
  private final Duration shutdownTimeout;
  private final Thread worker;
  private boolean accepting = true;
  private boolean storageInitialized;
  private PendingEvent<?> active;
  private volatile Instant lastSuccessfulWrite;
  private volatile boolean available;

  public DefaultAuditService(List<AuditEventProvider> providers, AuditEventStore store, Clock clock) {
    this(providers, store, clock, 1024, Duration.ofSeconds(10));
  }

  public DefaultAuditService(List<AuditEventProvider> providers, AuditEventStore store, Clock clock,
                             int capacity, Duration shutdownTimeout) {
    if (capacity < 1 || shutdownTimeout.isNegative() || shutdownTimeout.isZero()) {
      throw new IllegalArgumentException("Invalid audit queue configuration");
    }
    var registered = new HashMap<String, AuditEventDefinition<?>>();
    for (var provider : providers) {
      for (var definition : provider.eventTypes()) {
        if (registered.putIfAbsent(definition.id(), definition) != null) {
          throw new IllegalArgumentException("Duplicate audit event type: " + definition.id());
        }
      }
    }
    this.definitions = Map.copyOf(registered);
    this.store = store;
    this.clock = clock;
    this.capacity = capacity;
    this.shutdownTimeout = shutdownTimeout;
    this.worker = Thread.ofPlatform().daemon().name("audit-writer").start(this::run);
  }

  @Override
  public <T extends AuditDetails> void record(AuditEventDefinition<T> definition, AuditOutcome outcome,
                                              String actor, String resourceId, T details) {
    try {
      actor = actor == null || actor.isBlank() ? "unknown" : actor;
      var recordedAt = clock.instant();
      if (!definition.equals(definitions.get(definition.id()))) {
        throw new IllegalArgumentException("Unregistered audit event type");
      }
      String resourceType = definition.resourceType();
      if (resourceId != null && resourceType == null) {
        throw new IllegalArgumentException("Resource ID supplied for an event without a resource type");
      }
      // Validate the envelope, but never retain a reference to the caller's mutable details.
      var event = new AuditEvent<>(UUID.randomUUID(), recordedAt, definition, outcome, actor, resourceType, resourceId, details);
      byte[] snapshot = mapper.writeValueAsBytes(details);
      if (snapshot.length > MAX_DETAILS_BYTES) {
        throw new IllegalArgumentException("Audit details exceed size limit");
      }
      var pending = new PendingEvent<>(event.eventId(), recordedAt, definition, outcome, actor, resourceType, resourceId, snapshot);
      synchronized (monitor) {
        if (!accepting || queue.size() >= capacity) {
          dropped.incrementAndGet();
          failures.incrementAndGet();
          return;
        }
        queue.addLast(pending);
        monitor.notifyAll();
      }
    } catch (JsonProcessingException | RuntimeException e) {
      failures.incrementAndGet();
      dropped.incrementAndGet();
      LOG.warn("Audit event rejected ({})", e.getClass().getSimpleName());
    }
  }

  private void run() {
    try {
      try {
        initializeStorage();
      } catch (RuntimeException e) {
        failures.incrementAndGet();
        LOG.warn("Audit startup initialization failed ({})", e.getClass().getSimpleName());
      }
      while (true) {
        PendingEvent<?> pending;
        synchronized (monitor) {
          while (queue.isEmpty() && accepting) {
            monitor.wait();
          }
          if (queue.isEmpty()) {
            return;
          }
          pending = queue.removeFirst();
          active = pending;
        }
        try {
          append(pending);
          synchronized (monitor) {
            if (!pending.abandoned) {
              lastSuccessfulWrite = clock.instant();
              available = true;
            }
          }
        } catch (IOException | RuntimeException e) {
          synchronized (monitor) {
            if (!pending.abandoned) {
              failures.incrementAndGet();
              writeFailures.incrementAndGet();
              available = false;
            }
          }
          LOG.warn("Audit recording failed ({})", e.getClass().getSimpleName());
        } finally {
          synchronized (monitor) {
            active = null;
          }
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      try {
        store.close();
      } catch (Exception e) {
        LOG.warn("Audit storage close failed ({})", e.getClass().getSimpleName());
      }
    }
  }

  private void initializeStorage() {
    if (!storageInitialized) {
      store.initialize();
      storageInitialized = true;
    }
  }

  private <T extends AuditDetails> void append(PendingEvent<T> pending) throws IOException {
    initializeStorage();
    var details = mapper.readValue(pending.details, pending.definition.detailsType());
    store.append(new AuditEvent<>(pending.id, pending.recordedAt, pending.definition, pending.outcome,
        pending.actor, pending.resourceType, pending.resourceId, details));
  }

  @Override
  public AuditPage query(AuditQuery query) {
    return reader().query(query);
  }

  @Override
  public Optional<AuditEntryDetails> find(UUID eventId, String locator) {
    return reader().find(eventId, locator);
  }

  @Override
  public List<String> eventTypes() {
    return definitions.keySet().stream().sorted().toList();
  }

  private AuditEventReader reader() {
    if (store instanceof AuditEventReader reader) {
      return reader;
    }
    throw new IllegalStateException("Audit reading unavailable");
  }

  @Override
  public AuditStatus status() {
    synchronized (monitor) {
      var lastWrite = lastSuccessfulWrite;
      return new AuditStatus(true, available, failures.get(), lastWrite == null ? null : lastWrite.toString(),
          queue.size(), writeFailures.get(), dropped.get(), !available || dropped.get() > 0);
    }
  }

  public long failureCount() {
    return failures.get();
  }

  public Instant lastSuccessfulWrite() {
    return lastSuccessfulWrite;
  }

  @Override
  public void close() {
    synchronized (monitor) {
      if (!accepting) {
        return;
      }
      accepting = false;
      monitor.notifyAll();
    }
    try {
      worker.join(shutdownTimeout);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    synchronized (monitor) {
      if (worker.isAlive()) {
        long abandoned = queue.size();
        queue.clear();
        if (active != null) {
          active.abandoned = true;
          abandoned++;
        }
        dropped.addAndGet(abandoned);
        failures.addAndGet(abandoned);
        available = false;
        worker.interrupt();
        LOG.warn("Audit shutdown deadline reached; {} events unconfirmed", abandoned);
      }
    }
  }

  private static final class PendingEvent<T extends AuditDetails> {
    private final UUID id;
    private final Instant recordedAt;
    private final AuditEventDefinition<T> definition;
    private final AuditOutcome outcome;
    private final String actor;
    private final String resourceType;
    private final String resourceId;
    private final byte[] details;
    private boolean abandoned;

    private PendingEvent(UUID id, Instant recordedAt, AuditEventDefinition<T> definition, AuditOutcome outcome,
                         String actor, String resourceType, String resourceId, byte[] details) {
      this.id = id;
      this.recordedAt = recordedAt;
      this.definition = definition;
      this.outcome = outcome;
      this.actor = actor;
      this.resourceType = resourceType;
      this.resourceId = resourceId;
      this.details = details;
    }
  }
}
