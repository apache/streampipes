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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
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
  public ExtensionServiceOperationResult requestContainerProvidedOptions(ExtensionServiceRequestTarget target,
                                                                         String payload) throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestContainerProvidedOptions(target, payload);
    }

    return httpRequestManager.requestContainerProvidedOptions(target, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestMigration(ExtensionServiceRequestTarget target,
                                                          String payload) throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestMigration(target, payload);
    }

    return httpRequestManager.requestMigration(target, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestDescriptionUpdate(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestDescriptionUpdate(target);
    }

    return httpRequestManager.requestDescriptionUpdate(target);
  }

  @Override
  public ExtensionServiceOperationResult requestExtensionDescription(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestExtensionDescription(target);
    }

    return httpRequestManager.requestExtensionDescription(target);
  }

  @Override
  public ExtensionServiceOperationResult requestFunctionStop(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestFunctionStop(target);
    }

    return httpRequestManager.requestFunctionStop(target);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterStateChange(ExtensionServiceRequestTarget target,
                                                                   String elementId,
                                                                   String payload) throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestAdapterStateChange(target, elementId, payload);
    }

    return httpRequestManager.requestAdapterStateChange(target, elementId, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestRuntimeOptions(ExtensionServiceRequestTarget target,
                                                               String payload) throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestRuntimeOptions(target, payload);
    }

    return httpRequestManager.requestRuntimeOptions(target, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestSampleData(ExtensionServiceRequestTarget target,
                                                           String payload) throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestSampleData(target, payload);
    }

    return httpRequestManager.requestSampleData(target, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestExtensionInstanceHealth(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestExtensionInstanceHealth(target);
    }

    return httpRequestManager.requestExtensionInstanceHealth(target);
  }

  @Override
  public ExtensionServiceOperationResult requestServiceHealth(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestServiceHealth(target);
    }

    return httpRequestManager.requestServiceHealth(target);
  }

  @Override
  public ExtensionServiceOperationResult requestServiceLoad(ExtensionServiceRequestTarget target) throws IOException {
    if (useNats(target)) {
      try {
        var response = natsRequestManager.requestServiceLoad(target);
        if (response.isSuccess() || transportMode == CoreExtensionTransportMode.NATS) {
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

    return httpRequestManager.requestServiceLoad(target);
  }

  @Override
  public ExtensionServiceOperationResult requestPipelineElementInvocation(ExtensionServiceRequestTarget target,
                                                                          String pipelineId,
                                                                          String payload) throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestPipelineElementInvocation(target, pipelineId, payload);
    }

    return httpRequestManager.requestPipelineElementInvocation(target, pipelineId, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestPipelineElementDetach(ExtensionServiceRequestTarget target,
                                                                      String pipelineId) throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestPipelineElementDetach(target, pipelineId);
    }

    return httpRequestManager.requestPipelineElementDetach(target, pipelineId);
  }

  @Override
  public ExtensionServiceOperationResult requestPipelineElementAssets(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestPipelineElementAssets(target);
    }

    return httpRequestManager.requestPipelineElementAssets(target);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterAssets(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestAdapterAssets(target);
    }

    return httpRequestManager.requestAdapterAssets(target);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterIconAsset(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestAdapterIconAsset(target);
    }

    return httpRequestManager.requestAdapterIconAsset(target);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterDocumentationAsset(ExtensionServiceRequestTarget target)
      throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestAdapterDocumentationAsset(target);
    }

    return httpRequestManager.requestAdapterDocumentationAsset(target);
  }

  @Override
  public ExtensionServiceOperationResult requestOutputSchema(ExtensionServiceRequestTarget target,
                                                             String payload) throws IOException {
    if (useNats(target)) {
      return natsRequestManager.requestOutputSchema(target, payload);
    }

    return httpRequestManager.requestOutputSchema(target, payload);
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

}
