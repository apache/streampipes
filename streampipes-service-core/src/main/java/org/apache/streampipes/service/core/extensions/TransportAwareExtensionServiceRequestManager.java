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
import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerErrorEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TransportAwareExtensionServiceRequestManager implements ExtensionServiceRequestManager {

  private static final Logger LOG =
      LoggerFactory.getLogger(TransportAwareExtensionServiceRequestManager.class);
  private static final int INTERNAL_SERVER_ERROR = 500;

  private final ObjectMapper objectMapper;
  private final ExtensionServiceRequestManager httpRequestManager;
  private final CoreNatsRequestReplyClient natsRequestReplyClient;
  private final CoreExtensionTransportMode transportMode;
  private final String topicPrefix;

  public TransportAwareExtensionServiceRequestManager(
      ExtensionServiceRequestManager httpRequestManager,
      CoreNatsRequestReplyClient natsRequestReplyClient,
      CoreExtensionTransportMode transportMode,
      String topicPrefix
  ) {
    this.objectMapper = new ObjectMapper();
    this.httpRequestManager = httpRequestManager;
    this.natsRequestReplyClient = natsRequestReplyClient;
    this.transportMode = transportMode;
    this.topicPrefix = topicPrefix;
  }

  @Override
  public ExtensionServiceOperationResult requestContainerProvidedOptions(ExtensionServiceRequestTarget target,
                                                                         String payload) throws IOException {
    return httpRequestManager.requestContainerProvidedOptions(target, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestMigration(ExtensionServiceRequestTarget target,
                                                          String payload) throws IOException {
    return httpRequestManager.requestMigration(target, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestDescriptionUpdate(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestDescriptionUpdate(target);
  }

  @Override
  public ExtensionServiceOperationResult requestExtensionDescription(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestExtensionDescription(target);
  }

  @Override
  public ExtensionServiceOperationResult requestFunctionStop(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestFunctionStop(target);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterStateChange(ExtensionServiceRequestTarget target,
                                                                   String elementId,
                                                                   String payload) throws IOException {
    if (useNats(target)) {
      var authToken = AuthTokenUtils.getAuthToken(elementId);
      return requestViaNats(target, payload, authToken);
    }

    return httpRequestManager.requestAdapterStateChange(target, elementId, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestRuntimeOptions(ExtensionServiceRequestTarget target,
                                                               String payload) throws IOException {
    return httpRequestManager.requestRuntimeOptions(target, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestSampleData(ExtensionServiceRequestTarget target,
                                                           String payload) throws IOException {
    return httpRequestManager.requestSampleData(target, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestExtensionInstanceHealth(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestExtensionInstanceHealth(target);
  }

  @Override
  public ExtensionServiceOperationResult requestServiceHealth(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestServiceHealth(target);
  }

  @Override
  public ExtensionServiceOperationResult requestServiceLoad(ExtensionServiceRequestTarget target) throws IOException {
    if (useNats(target)) {
      try {
        var response = requestServiceLoadViaNats(target);
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
    return httpRequestManager.requestPipelineElementInvocation(target, pipelineId, payload);
  }

  @Override
  public ExtensionServiceOperationResult requestPipelineElementDetach(ExtensionServiceRequestTarget target,
                                                                      String pipelineId) throws IOException {
    return httpRequestManager.requestPipelineElementDetach(target, pipelineId);
  }

  @Override
  public ExtensionServiceOperationResult requestPipelineElementAssets(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestPipelineElementAssets(target);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterAssets(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestAdapterAssets(target);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterIconAsset(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestAdapterIconAsset(target);
  }

  @Override
  public ExtensionServiceOperationResult requestAdapterDocumentationAsset(ExtensionServiceRequestTarget target)
      throws IOException {
    return httpRequestManager.requestAdapterDocumentationAsset(target);
  }

  @Override
  public ExtensionServiceOperationResult requestOutputSchema(ExtensionServiceRequestTarget target,
                                                             String payload) throws IOException {
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

  private ExtensionServiceOperationResult requestServiceLoadViaNats(ExtensionServiceRequestTarget target)
      throws IOException {
    return requestViaNats(target, null, null);
  }

  private ExtensionServiceOperationResult requestViaNats(ExtensionServiceRequestTarget target,
                                                         String payload,
                                                         String authToken) throws IOException {
    String topic = target.toTopic(topicPrefix);

    var requestEnvelope = new ExtensionServiceBrokerRequestEnvelope(
        UUID.randomUUID().toString(),
        target.operation().name(),
        payload,
        authToken
    );

    byte[] responseBytes = natsRequestReplyClient.request(topic, objectMapper.writeValueAsBytes(requestEnvelope));
    var responseEnvelope = objectMapper.readValue(responseBytes, ExtensionServiceBrokerResponseEnvelope.class);

    return toOperationResult(responseEnvelope);
  }

  private ExtensionServiceOperationResult toOperationResult(ExtensionServiceBrokerResponseEnvelope responseEnvelope)
      throws IOException {
    byte[] body = makeBody(responseEnvelope);
    int statusCode = responseEnvelope.getStatusCode() == 0
        ? INTERNAL_SERVER_ERROR
        : responseEnvelope.getStatusCode();

    return new ExtensionServiceOperationResult(statusCode, body);
  }

  private byte[] makeBody(ExtensionServiceBrokerResponseEnvelope responseEnvelope) throws IOException {
    if (responseEnvelope.getPayload() != null) {
      return responseEnvelope.getPayload().getBytes(StandardCharsets.UTF_8);
    }

    ExtensionServiceBrokerErrorEnvelope error = responseEnvelope.getError();
    if (error != null) {
      return objectMapper.writeValueAsBytes(error);
    }

    return null;
  }
}
