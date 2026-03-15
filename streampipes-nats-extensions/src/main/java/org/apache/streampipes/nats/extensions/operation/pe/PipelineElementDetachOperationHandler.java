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
import org.apache.streampipes.model.extensions.transport.ExtensionServicePipelineDetachRequest;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerTopicParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PipelineElementDetachOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_DETACH.operationId();
  private static final String TOPIC_OPERATION_SEGMENT =
      ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_DETACH.firstTopicSegment();
  private static final String PROVIDER_DATA_PROCESSOR = ExtensionBrokerConstants.DATA_PROCESSOR;
  private static final String PROVIDER_DATA_SINK = ExtensionBrokerConstants.DATA_SINK;

  private final ObjectMapper objectMapper;
  private final DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement;
  private final DataSinkPipelineElementManagement dataSinkPipelineElementManagement;

  public PipelineElementDetachOperationHandler(
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
          "Missing detach payload"
      );
    }

    ExtensionServicePipelineDetachRequest detachRequest;
    try {
      detachRequest = objectMapper.readValue(request.getPayload(), ExtensionServicePipelineDetachRequest.class);
    } catch (IOException e) {
      return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
          request.getRequestId(),
          "Invalid detach payload"
      );
    }

    if (ExtensionBrokerResponseFactory.isBlank(detachRequest.getElementId())
        || ExtensionBrokerResponseFactory.isBlank(detachRequest.getRunningInstanceId())) {
      return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
          request.getRequestId(),
          "Detach payload is missing elementId or runningInstanceId"
      );
    }

    var provider = ExtensionBrokerTopicParser.extractProvider(
        context.topic(),
        context.subscriptionBaseTopic(),
        TOPIC_OPERATION_SEGMENT
    );
    Response response;

    if (PROVIDER_DATA_PROCESSOR.equals(provider)) {
      response = dataProcessorPipelineElementManagement.detach(
          detachRequest.getElementId(),
          detachRequest.getRunningInstanceId()
      );
    } else if (PROVIDER_DATA_SINK.equals(provider)) {
      response = dataSinkPipelineElementManagement.detach(
          detachRequest.getElementId(),
          detachRequest.getRunningInstanceId()
      );
    } else {
      return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
          request.getRequestId(),
          "Unsupported provider for pipeline detach: " + provider
      );
    }

    var payload = objectMapper.writeValueAsString(response);
    return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
  }
}
