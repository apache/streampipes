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

import org.apache.streampipes.extensions.management.connect.AdapterDescriptionManagement;
import org.apache.streampipes.extensions.management.pe.DataProcessorPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataSinkPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataStreamPipelineElementManagement;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperation;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerTopicParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants.ADAPTER;
import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants.DATA_PROCESSOR;
import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants.DATA_SINK;
import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerConstants.DATA_STREAM;

public class DescriptionOperationHandler implements ExtensionBrokerOperationHandler {

  private final String operation;
  private final String topicOperationSegment;
  private final String operationLabel;
  private final ObjectMapper objectMapper;
  private final AdapterDescriptionManagement adapterDescriptionManagement;
  private final DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement;
  private final DataSinkPipelineElementManagement dataSinkPipelineElementManagement;
  private final DataStreamPipelineElementManagement dataStreamPipelineElementManagement;

  public DescriptionOperationHandler(
      ExtensionServiceBrokerOperation brokerOperation,
      String operationLabel,
      ObjectMapper objectMapper,
      AdapterDescriptionManagement adapterDescriptionManagement,
      DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement,
      DataSinkPipelineElementManagement dataSinkPipelineElementManagement,
      DataStreamPipelineElementManagement dataStreamPipelineElementManagement
  ) {
    this.operation = brokerOperation.operationId();
    this.topicOperationSegment = brokerOperation.firstTopicSegment();
    this.operationLabel = operationLabel;
    this.objectMapper = objectMapper;
    this.adapterDescriptionManagement = adapterDescriptionManagement;
    this.dataProcessorPipelineElementManagement = dataProcessorPipelineElementManagement;
    this.dataSinkPipelineElementManagement = dataSinkPipelineElementManagement;
    this.dataStreamPipelineElementManagement = dataStreamPipelineElementManagement;
  }

  @Override
  public String operation() {
    return operation;
  }

  @Override
  public ExtensionServiceBrokerResponseEnvelope handle(ExtensionServiceBrokerRequestEnvelope request,
                                                       ExtensionBrokerRequestContext context) throws Exception {
    var operationSegments = ExtensionBrokerTopicParser.extractOperationSegments(
        context.topic(),
        context.subscriptionBaseTopic()
    );
    if (operationSegments.size() < 3 || !topicOperationSegment.equals(operationSegments.get(0))) {
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

    Object description;
    if (ADAPTER.equals(provider)) {
      var adapterDescriptionOpt = adapterDescriptionManagement.getAdapterDescription(appId);
      if (adapterDescriptionOpt.isEmpty()) {
        return ExtensionBrokerResponseFactory.notFound(
            request.getRequestId(),
            "Could not find adapter with id " + appId
        );
      }

      description = adapterDescriptionOpt.get();
    } else if (DATA_PROCESSOR.equals(provider)) {
      description = dataProcessorPipelineElementManagement.getDescription(appId);
    } else if (DATA_SINK.equals(provider)) {
      description = dataSinkPipelineElementManagement.getDescription(appId);
    } else if (DATA_STREAM.equals(provider)) {
      description = dataStreamPipelineElementManagement.getDescription(appId);
    } else {
      return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
          request.getRequestId(),
          "Unsupported provider for " + operationLabel + ": " + provider
      );
    }

    var payload = objectMapper.writeValueAsString(description);
    return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
  }
}
