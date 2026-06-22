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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class ExtensionInstanceAvailabilityCheck {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionInstanceAvailabilityCheck.class);

  private final IExtensionsServiceStorage extensionsServiceStorage;
  private final String serviceId;
  private final ExtensionServiceRequestManager extensionRequestManager;
  private final SpResourceManager resourceManager;

  public ExtensionInstanceAvailabilityCheck(IExtensionsServiceStorage extensionsServiceStorage,
                                            String serviceId,
                                            ExtensionServiceRequestManager extensionRequestManager,
                                            SpResourceManager resourceManager) {
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.serviceId = serviceId;
    this.extensionRequestManager = extensionRequestManager;
    this.resourceManager = resourceManager;
  }

  public ExtensionInstanceHealth checkRunningInstances() {
    try {
      var service = extensionsServiceStorage.findAll().stream()
          .filter(svc -> svc.getSvcId().equals(serviceId))
          .findFirst();

      if (service.isEmpty()) {
        return new ExtensionInstanceHealth(Set.of(), Set.of());
      } else {
        var response = extensionRequestManager.request(
            ExtensionServiceRequests
                .extensionInstanceHealth(makeRequestTarget(service.get()), resourceManager)
        );
        if (response.statusCode() != 200) {
          return new ExtensionInstanceHealth(Set.of(), Set.of());
        }
        return deserialize(response.responseBody());
      }

    } catch (IOException e) {
      LOG.error("Extension service {} is unavailable", serviceId);
      return new ExtensionInstanceHealth(Set.of(), Set.of());
    }
  }

  private ExtensionInstanceHealth deserialize(String json) throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().readValue(json, ExtensionInstanceHealth.class);
  }

  private ExtensionServiceRequestTarget makeRequestTarget(SpServiceRegistration service) {
    return ExtensionServiceRequestTargets.extensionInstanceHealth(service);
  }
}
