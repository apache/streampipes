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

package org.apache.streampipes.nats.extensions.operation.pe;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.management.pe.DataProcessorPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataSinkPipelineElementManagement;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerErrorEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerTopicParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants.DATA_PROCESSOR;
import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants.DATA_SINK;

public class ContainerProvidedOptionsOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = ExtensionServiceBrokerOperations.CONTAINER_PROVIDED_OPTIONS.operationId();
  private static final String TOPIC_OPERATION_SEGMENT =
      ExtensionServiceBrokerOperations.CONTAINER_PROVIDED_OPTIONS.firstTopicSegment();

  private final ObjectMapper objectMapper;
  private final DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement;
  private final DataSinkPipelineElementManagement dataSinkPipelineElementManagement;

  public ContainerProvidedOptionsOperationHandler(
      ObjectMapper objectMapper,
      DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement,
      DataSinkPipelineElementManagement dataSinkPipelineElementManagement
  ) {
    this.objectMapper = objectMapper;
    this.dataProcessorPipelineElementManagement = dataProcessorPipelineElementManagement;
    this.dataSinkPipelineElementManagement = dataSinkPipelineElementManagement;
  }

  @Override
  public String operation() {
    return OPERATION;
  }

  @Override
  public ExtensionServiceBrokerResponseEnvelope handle(ExtensionServiceBrokerRequestEnvelope request,
                                                       ExtensionBrokerRequestContext context) throws Exception {
    if (ExtensionBrokerResponseFactory.isBlank(request.getPayload())) {
      return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
          request.getRequestId(),
          "Missing runtime options request payload"
      );
    }

    var operationSegments = ExtensionBrokerTopicParser.extractOperationSegments(
        context.topic(),
        context.subscriptionBaseTopic()
    );
    if (operationSegments.size() < 3 || !TOPIC_OPERATION_SEGMENT.equals(operationSegments.get(0))) {
      return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
          request.getRequestId(),
          "Could not resolve provider and appId from topic " + context.topic()
      );
    }

    var provider = operationSegments.get(1);
    var appId = ExtensionBrokerTopicParser.extractTail(operationSegments, 2);
    if (ExtensionBrokerResponseFactory.isBlank(appId)) {
      return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
          request.getRequestId(),
          "Missing appId in topic " + context.topic()
      );
    }

    RuntimeOptionsRequest runtimeOptionsRequest;
    try {
      runtimeOptionsRequest = objectMapper.readValue(request.getPayload(), RuntimeOptionsRequest.class);
    } catch (IOException e) {
      return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
          request.getRequestId(),
          "Invalid runtime options request payload"
      );
    }

    try {
      Object response;
      if (DATA_PROCESSOR.equals(provider)) {
        response = dataProcessorPipelineElementManagement.fetchConfigurations(appId, runtimeOptionsRequest);
      } else if (DATA_SINK.equals(provider)) {
        response = dataSinkPipelineElementManagement.fetchConfigurations(appId, runtimeOptionsRequest);
      } else {
        return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
            request.getRequestId(),
            "Unsupported provider for container-provided-options: " + provider
        );
      }

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
