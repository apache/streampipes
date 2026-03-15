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
import org.apache.streampipes.extensions.management.pe.DataProcessorPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataSinkPipelineElementManagement;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class OutputSchemaOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = ExtensionServiceBrokerOperations.OUTPUT_SCHEMA.operationId();
  private static final String TOPIC_OPERATION_SEGMENT =
      ExtensionServiceBrokerOperations.OUTPUT_SCHEMA.firstTopicSegment();
  private static final String PROVIDER_DATA_PROCESSOR = ExtensionBrokerConstants.Provider.DATA_PROCESSOR;
  private static final String PROVIDER_DATA_SINK = ExtensionBrokerConstants.Provider.DATA_SINK;

  private final ObjectMapper objectMapper;
  private final DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement;
  private final DataSinkPipelineElementManagement dataSinkPipelineElementManagement;

  public OutputSchemaOperationHandler(
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
          "Missing output schema request payload"
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
    var appId = ExtensionBrokerTopicParser.extractTail(context.topic(), context.subscriptionBaseTopic(), 2);
    if (ExtensionBrokerResponseFactory.isBlank(appId)) {
      return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
          request.getRequestId(),
          "Missing appId in topic " + context.topic()
      );
    }

    try {
      Object response;
      if (PROVIDER_DATA_PROCESSOR.equals(provider)) {
        var invocation = parseProcessorInvocation(request.getPayload());
        if (invocation == null) {
          return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
              request.getRequestId(),
              "Invalid data processor invocation payload"
          );
        }
        response = dataProcessorPipelineElementManagement.fetchOutputStrategy(appId, invocation);
      } else if (PROVIDER_DATA_SINK.equals(provider)) {
        var invocation = parseSinkInvocation(request.getPayload());
        if (invocation == null) {
          return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
              request.getRequestId(),
              "Invalid data sink invocation payload"
          );
        }
        response = dataSinkPipelineElementManagement.fetchOutputStrategy(appId, invocation);
      } else {
        return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
            request.getRequestId(),
            "Unsupported provider for output schema operation: " + provider
        );
      }

      return ExtensionBrokerResponseFactory.ok(request.getRequestId(), objectMapper.writeValueAsString(response));
    } catch (SpRuntimeException | SpConfigurationException e) {
      var fallbackResponse = new Response(appId, false);
      return ExtensionBrokerResponseFactory.ok(
          request.getRequestId(),
          objectMapper.writeValueAsString(fallbackResponse)
      );
    }
  }

  private DataProcessorInvocation parseProcessorInvocation(String payload) {
    try {
      return objectMapper.readValue(payload, DataProcessorInvocation.class);
    } catch (IOException e) {
      return null;
    }
  }

  private DataSinkInvocation parseSinkInvocation(String payload) {
    try {
      return objectMapper.readValue(payload, DataSinkInvocation.class);
    } catch (IOException e) {
      return null;
    }
  }
}
