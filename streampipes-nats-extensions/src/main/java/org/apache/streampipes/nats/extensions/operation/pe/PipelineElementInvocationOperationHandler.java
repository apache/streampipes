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
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerTopicParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants.DATA_PROCESSOR;
import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants.DATA_SINK;

public class PipelineElementInvocationOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_INVOCATION.operationId();
  private static final String TOPIC_OPERATION_SEGMENT =
      ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_INVOCATION.firstTopicSegment();

  private final ObjectMapper objectMapper;
  private final DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement;
  private final DataSinkPipelineElementManagement dataSinkPipelineElementManagement;

  public PipelineElementInvocationOperationHandler(
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
          "Missing invocation payload"
      );
    }

    var provider = ExtensionBrokerTopicParser.extractProvider(
        context.topic(),
        context.subscriptionBaseTopic(),
        TOPIC_OPERATION_SEGMENT
    );

    Response response;
    try {
      if (DATA_PROCESSOR.equals(provider)) {
        var invocation = objectMapper.readValue(request.getPayload(), DataProcessorInvocation.class);
        if (ExtensionBrokerResponseFactory.isBlank(invocation.getAppId())) {
          return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
              request.getRequestId(),
              "Missing appId in invocation payload"
          );
        }

        response = dataProcessorPipelineElementManagement.invokeRuntime(invocation.getAppId(), invocation);
      } else if (DATA_SINK.equals(provider)) {
        var invocation = objectMapper.readValue(request.getPayload(), DataSinkInvocation.class);
        if (ExtensionBrokerResponseFactory.isBlank(invocation.getAppId())) {
          return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
              request.getRequestId(),
              "Missing appId in invocation payload"
          );
        }

        response = dataSinkPipelineElementManagement.invokeRuntime(invocation.getAppId(), invocation);
      } else {
        return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
            request.getRequestId(),
            "Unsupported provider for pipeline invocation: " + provider
        );
      }
    } catch (IOException e) {
      return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
          request.getRequestId(),
          "Invalid invocation payload"
      );
    }

    var payload = objectMapper.writeValueAsString(response);
    return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
  }
}
