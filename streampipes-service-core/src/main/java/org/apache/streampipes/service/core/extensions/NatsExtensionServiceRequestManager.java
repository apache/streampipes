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
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.util.AuthTokenUtils;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerErrorEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServicePipelineDetachRequest;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NatsExtensionServiceRequestManager {

  private static final int INTERNAL_SERVER_ERROR = 500;

  private final ObjectMapper objectMapper;
  private final CoreNatsRequestReplyClient natsRequestReplyClient;
  private final String topicPrefix;

  public NatsExtensionServiceRequestManager(CoreNatsRequestReplyClient natsRequestReplyClient,
                                            String topicPrefix) {
    this.objectMapper = JacksonSerializer.getObjectMapper();
    this.natsRequestReplyClient = natsRequestReplyClient;
    this.topicPrefix = topicPrefix;
  }

  public ExtensionServiceOperationResult requestServiceLoad(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestMigration(ExtensionServiceRequestTarget target,
                                                          String payload) throws IOException {
    return request(target, payload, AuthTokenUtils.getAuthTokenForCurrentUser());
  }

  public ExtensionServiceOperationResult requestDescriptionUpdate(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestExtensionDescription(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestFunctionStop(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestContainerProvidedOptions(ExtensionServiceRequestTarget target,
                                                                         String payload) throws IOException {
    return request(target, payload, AuthTokenUtils.getAuthTokenForCurrentUser());
  }

  public ExtensionServiceOperationResult requestAdapterStateChange(ExtensionServiceRequestTarget target,
                                                                   String elementId,
                                                                   String payload) throws IOException {
    return request(target, payload, AuthTokenUtils.getAuthToken(elementId));
  }

  public ExtensionServiceOperationResult requestPipelineElementInvocation(ExtensionServiceRequestTarget target,
                                                                          String pipelineId,
                                                                          String payload) throws IOException {
    return request(target, payload, AuthTokenUtils.getAuthToken(pipelineId));
  }

  public ExtensionServiceOperationResult requestPipelineElementDetach(ExtensionServiceRequestTarget target,
                                                                      String pipelineId) throws IOException {
    var payload = makeDetachPayload(target);
    return request(target, payload, AuthTokenUtils.getAuthToken(pipelineId));
  }

  public ExtensionServiceOperationResult requestPipelineElementAssets(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestAdapterAssets(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestAdapterIconAsset(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestAdapterDocumentationAsset(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestRuntimeOptions(ExtensionServiceRequestTarget target,
                                                               String payload) throws IOException {
    return request(target, payload, AuthTokenUtils.getAuthTokenForCurrentUser());
  }

  public ExtensionServiceOperationResult requestSampleData(ExtensionServiceRequestTarget target,
                                                           String payload) throws IOException {
    return request(target, payload, AuthTokenUtils.getAuthTokenForCurrentUser());
  }

  public ExtensionServiceOperationResult requestExtensionInstanceHealth(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestServiceHealth(ExtensionServiceRequestTarget target)
      throws IOException {
    return request(target, null, null);
  }

  public ExtensionServiceOperationResult requestOutputSchema(ExtensionServiceRequestTarget target,
                                                             String payload) throws IOException {
    return request(target, payload, null);
  }

  private ExtensionServiceOperationResult request(ExtensionServiceRequestTarget target,
                                                  String payload,
                                                  String authToken) throws IOException {
    String topic = target.toTopic(topicPrefix);

    var requestEnvelope = new ExtensionServiceBrokerRequestEnvelope(
        UUID.randomUUID().toString(),
        ExtensionServiceBrokerOperations.byOperationId(target.operation().name()).operationId(),
        payload,
        authToken
    );

    byte[] responseBytes = natsRequestReplyClient.request(topic, objectMapper.writeValueAsBytes(requestEnvelope));
    var responseEnvelope = objectMapper.readValue(responseBytes, ExtensionServiceBrokerResponseEnvelope.class);

    return toOperationResult(responseEnvelope);
  }

  private String makeDetachPayload(ExtensionServiceRequestTarget target) throws IOException {
    var pathSegments = target.pathSegments();
    if (pathSegments.size() < 2) {
      throw new IOException("Could not create detach request payload from request target path segments");
    }

    String elementId = pathSegments.get(pathSegments.size() - 2);
    String runningInstanceId = pathSegments.get(pathSegments.size() - 1);

    if (isBlank(elementId) || isBlank(runningInstanceId)) {
      throw new IOException("Detach request payload is missing elementId or runningInstanceId");
    }

    return objectMapper.writeValueAsString(new ExtensionServicePipelineDetachRequest(elementId, runningInstanceId));
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
    if (responseEnvelope.getPayloadBytes() != null) {
      return responseEnvelope.getPayloadBytes();
    }

    if (responseEnvelope.getPayload() != null) {
      return responseEnvelope.getPayload().getBytes(StandardCharsets.UTF_8);
    }

    ExtensionServiceBrokerErrorEnvelope error = responseEnvelope.getError();
    if (error != null) {
      return objectMapper.writeValueAsBytes(error);
    }

    return null;
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
