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

package org.apache.streampipes.nats.extensions.operation;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.management.connect.RuntimeResolvableManagement;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerErrorEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RuntimeOptionsOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = "RUNTIME_OPTIONS";
  private static final String TOPIC_OPERATION_SEGMENT = "adapter-runtime-options";

  private final ObjectMapper objectMapper;
  private final RuntimeResolvableManagement runtimeResolvableManagement;

  public RuntimeOptionsOperationHandler(ObjectMapper objectMapper,
                                        RuntimeResolvableManagement runtimeResolvableManagement) {
    this.objectMapper = objectMapper;
    this.runtimeResolvableManagement = runtimeResolvableManagement;
  }

  @Override
  public String operation() {
    return OPERATION;
  }

  @Override
  public ExtensionServiceBrokerResponseEnvelope handle(ExtensionServiceBrokerRequestEnvelope request,
                                                       ExtensionBrokerRequestContext context) throws Exception {
    if (ExtensionBrokerResponseFactory.isBlank(request.getPayload())) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidPayload",
          "Missing runtime options request payload"
      );
    }

    var operationSegments = ExtensionBrokerTopicParser.extractOperationSegments(
        context.topic(),
        context.subscriptionBaseTopic()
    );
    if (operationSegments.isEmpty() || !TOPIC_OPERATION_SEGMENT.equals(operationSegments.get(0))) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Could not resolve appId from topic " + context.topic()
      );
    }

    var appId = ExtensionBrokerTopicParser.extractTail(context.topic(), context.subscriptionBaseTopic(), 1);
    if (ExtensionBrokerResponseFactory.isBlank(appId)) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Missing appId in topic " + context.topic()
      );
    }

    RuntimeOptionsRequest runtimeOptionsRequest;
    try {
      runtimeOptionsRequest = objectMapper.readValue(request.getPayload(), RuntimeOptionsRequest.class);
    } catch (IOException e) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidPayload",
          "Invalid runtime options request payload"
      );
    }

    try {
      var response = runtimeResolvableManagement.fetchConfigurations(appId, runtimeOptionsRequest);
      return ExtensionBrokerResponseFactory.ok(request.getRequestId(), objectMapper.writeValueAsString(response));
    } catch (SpConfigurationException e) {
      return new ExtensionServiceBrokerResponseEnvelope(
          request.getRequestId(),
          ExtensionBrokerResponseFactory.HTTP_STATUS_BAD_REQUEST,
          objectMapper.writeValueAsString(e),
          new ExtensionServiceBrokerErrorEnvelope(e.getClass().getSimpleName(), e.getMessage())
      );
    } catch (SpRuntimeException e) {
      return ExtensionBrokerResponseFactory.error(
          request.getRequestId(),
          ExtensionBrokerResponseFactory.HTTP_STATUS_INTERNAL_SERVER_ERROR,
          e
      );
    }
  }
}
