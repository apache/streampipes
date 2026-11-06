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

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Removes registrations left behind by a predecessor of a newly registered extension service.
 *
 * <p>A restarted extension service registers under a new id but keeps its group, host and
 * port. A registration of the same group at the same address that was never deregistered
 * usually belongs to a predecessor that is gone. Left in place, it counts as an unreachable
 * service whose instances are unknown, and recovery of the pipeline elements that ran there
 * waits until the service health check removes it. Several live replicas may share one
 * address as well, for example behind a Kubernetes service name, so every candidate is asked
 * for its own health first: the per-service health endpoint answers only for its own id, and
 * the NATS request topic is per id, so a live replica keeps its registration and only a
 * registration nobody answers for is removed.
 */
public class StaleRegistrationCleanup {

  private static final Logger LOG = LoggerFactory.getLogger(StaleRegistrationCleanup.class);

  private final IExtensionsServiceStorage storage;
  private final ExtensionServiceRequestManager requestManager;
  private final SpResourceManager resourceManager;

  public StaleRegistrationCleanup(IExtensionsServiceStorage storage,
                                  ExtensionServiceRequestManager requestManager,
                                  SpResourceManager resourceManager) {
    this.storage = storage;
    this.requestManager = requestManager;
    this.resourceManager = resourceManager;
  }

  /**
   * Probes every other registration of the same group at the same address and removes the
   * ones that do not answer for their own id. Never throws; failures are logged.
   */
  public void removePredecessorsOf(SpServiceRegistration registration) {
    try {
      findCandidates(registration).forEach(candidate -> {
        if (isAlive(candidate)) {
          LOG.info("Service {} at {}:{} is alive next to new registration {}, keeping it",
              candidate.getSvcId(), candidate.getHost(), candidate.getPort(), registration.getSvcId());
        } else {
          LOG.info("Removing stale registration {} of service {} at {}:{}, replaced by {}",
              candidate.getSvcId(), candidate.getSvcGroup(), candidate.getHost(), candidate.getPort(),
              registration.getSvcId());
          storage.deleteElement(candidate);
        }
      });
    } catch (RuntimeException e) {
      LOG.warn("Could not clean up stale registrations for service {}", registration.getSvcId(), e);
    }
  }

  List<SpServiceRegistration> findCandidates(SpServiceRegistration registration) {
    return storage.findAll().stream()
        .filter(other -> !Objects.equals(other.getSvcId(), registration.getSvcId()))
        .filter(other -> Objects.equals(other.getSvcGroup(), registration.getSvcGroup()))
        .filter(other -> Objects.equals(other.getHost(), registration.getHost()))
        .filter(other -> other.getPort() == registration.getPort())
        .toList();
  }

  private boolean isAlive(SpServiceRegistration candidate) {
    try {
      var target = ExtensionServiceRequestTargets.serviceHealth(candidate, candidate.getHealthCheckPath());
      var response = requestManager.request(ExtensionServiceRequests.serviceHealth(target, resourceManager));
      return response.statusCode() == HttpStatus.SC_OK;
    } catch (IOException | RuntimeException e) {
      return false;
    }
  }
}
