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

package org.apache.streampipes.service.core.extensions;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequest;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TransportAwareExtensionServiceRequestManager implements ExtensionServiceRequestManager {

  private static final Logger LOG =
      LoggerFactory.getLogger(TransportAwareExtensionServiceRequestManager.class);

  private final ExtensionServiceRequestManager httpRequestManager;
  private final NatsExtensionServiceRequestManager natsRequestManager;
  private final CoreExtensionTransportMode transportMode;

  public TransportAwareExtensionServiceRequestManager(
      ExtensionServiceRequestManager httpRequestManager,
      NatsExtensionServiceRequestManager natsRequestManager,
      CoreExtensionTransportMode transportMode
  ) {
    this.httpRequestManager = httpRequestManager;
    this.natsRequestManager = natsRequestManager;
    this.transportMode = transportMode;
  }

  @Override
  public ExtensionServiceOperationResult request(ExtensionServiceRequest request) throws IOException {
    var target = request.target();
    if (useNats(target)) {
      try {
        var response = natsRequestManager.request(request);
        if (response.isSuccess()
            || transportMode == CoreExtensionTransportMode.NATS
            || !isServiceLoad(target)) {
          return response;
        }

        LOG.warn("NATS request for operation {} to service {} returned status {} - falling back to HTTP",
            target.operation(), target.serviceId(), response.statusCode());
      } catch (IOException e) {
        if (transportMode == CoreExtensionTransportMode.NATS) {
          throw e;
        }

        LOG.warn("NATS request for operation {} to service {} failed - falling back to HTTP",
            target.operation(), target.serviceId(), e);
      }
    }

    return httpRequestManager.request(request);
  }

  private boolean useNats(ExtensionServiceRequestTarget target) {
    return switch (transportMode) {
      case HTTP -> false;
      case NATS -> true;
      case AUTO -> serviceSupportsNats(target);
    };
  }

  private boolean serviceSupportsNats(ExtensionServiceRequestTarget target) {
    var service = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getExtensionsServiceStorage()
        .getElementById(target.serviceId());

    if (service == null || service.getTags() == null) {
      return false;
    }

    return service.getTags().stream().anyMatch(tag ->
        tag.getPrefix() == SpServiceTagPrefix.CUSTOM
            && ExtensionServiceBrokerTopics.TRANSPORT_TAG_NATS.equals(tag.getValue())
    );
  }

  private boolean isServiceLoad(ExtensionServiceRequestTarget target) {
    return ExtensionServiceBrokerOperations.SERVICE_LOAD.operationId().equals(target.operation());
  }

}
