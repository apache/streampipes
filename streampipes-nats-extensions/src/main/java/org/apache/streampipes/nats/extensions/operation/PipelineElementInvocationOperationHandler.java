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

import org.apache.streampipes.extensions.management.pe.DataProcessorPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataSinkPipelineElementManagement;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PipelineElementInvocationOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = "PIPELINE_ELEMENT_INVOCATION";
  private static final String TOPIC_OPERATION_SEGMENT = "pipeline-invocation";
  private static final String PROVIDER_DATA_PROCESSOR = "DATA_PROCESSOR";
  private static final String PROVIDER_DATA_SINK = "DATA_SINK";

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
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidPayload",
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
      if (PROVIDER_DATA_PROCESSOR.equals(provider)) {
        var invocation = objectMapper.readValue(request.getPayload(), DataProcessorInvocation.class);
        if (ExtensionBrokerResponseFactory.isBlank(invocation.getAppId())) {
          return ExtensionBrokerResponseFactory.badRequest(
              request.getRequestId(),
              "InvalidPayload",
              "Missing appId in invocation payload"
          );
        }

        response = dataProcessorPipelineElementManagement.invokeRuntime(invocation.getAppId(), invocation);
      } else if (PROVIDER_DATA_SINK.equals(provider)) {
        var invocation = objectMapper.readValue(request.getPayload(), DataSinkInvocation.class);
        if (ExtensionBrokerResponseFactory.isBlank(invocation.getAppId())) {
          return ExtensionBrokerResponseFactory.badRequest(
              request.getRequestId(),
              "InvalidPayload",
              "Missing appId in invocation payload"
          );
        }

        response = dataSinkPipelineElementManagement.invokeRuntime(invocation.getAppId(), invocation);
      } else {
        return ExtensionBrokerResponseFactory.badRequest(
            request.getRequestId(),
            "InvalidPayload",
            "Unsupported provider for pipeline invocation: " + provider
        );
      }
    } catch (IOException e) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidPayload",
          "Invalid invocation payload"
      );
    }

    var payload = objectMapper.writeValueAsString(response);
    return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
  }
}
