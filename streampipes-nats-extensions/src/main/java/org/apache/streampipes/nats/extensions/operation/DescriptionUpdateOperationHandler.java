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

import org.apache.streampipes.extensions.management.connect.AdapterDescriptionManagement;
import org.apache.streampipes.extensions.management.pe.DataProcessorPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataSinkPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataStreamPipelineElementManagement;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DescriptionUpdateOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = "DESCRIPTION_UPDATE";
  private static final String TOPIC_OPERATION_SEGMENT = "description-update";
  private static final String PROVIDER_ADAPTER = "ADAPTER";
  private static final String PROVIDER_DATA_PROCESSOR = "DATA_PROCESSOR";
  private static final String PROVIDER_DATA_SINK = "DATA_SINK";
  private static final String PROVIDER_DATA_STREAM = "DATA_STREAM";

  private final ObjectMapper objectMapper;
  private final AdapterDescriptionManagement adapterDescriptionManagement;
  private final DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement;
  private final DataSinkPipelineElementManagement dataSinkPipelineElementManagement;
  private final DataStreamPipelineElementManagement dataStreamPipelineElementManagement;

  public DescriptionUpdateOperationHandler(
      ObjectMapper objectMapper,
      AdapterDescriptionManagement adapterDescriptionManagement,
      DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement,
      DataSinkPipelineElementManagement dataSinkPipelineElementManagement,
      DataStreamPipelineElementManagement dataStreamPipelineElementManagement
  ) {
    this.objectMapper = objectMapper;
    this.adapterDescriptionManagement = adapterDescriptionManagement;
    this.dataProcessorPipelineElementManagement = dataProcessorPipelineElementManagement;
    this.dataSinkPipelineElementManagement = dataSinkPipelineElementManagement;
    this.dataStreamPipelineElementManagement = dataStreamPipelineElementManagement;
  }

  @Override
  public String operation() {
    return OPERATION;
  }

  @Override
  public ExtensionServiceBrokerResponseEnvelope handle(ExtensionServiceBrokerRequestEnvelope request,
                                                       ExtensionBrokerRequestContext context) throws Exception {
    var operationSegments = ExtensionBrokerTopicParser.extractOperationSegments(
        context.topic(),
        context.subscriptionBaseTopic()
    );
    if (operationSegments.size() < 3 || !TOPIC_OPERATION_SEGMENT.equals(operationSegments.get(0))) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Could not resolve provider and appId from topic " + context.topic()
      );
    }

    var provider = operationSegments.get(1);
    var appId = ExtensionBrokerTopicParser.extractTail(context.topic(), context.subscriptionBaseTopic(), 2);
    if (ExtensionBrokerResponseFactory.isBlank(appId)) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Missing appId in topic " + context.topic()
      );
    }

    Object description;
    if (PROVIDER_ADAPTER.equals(provider)) {
      var adapterDescriptionOpt = adapterDescriptionManagement.getAdapterDescription(appId);
      if (adapterDescriptionOpt.isEmpty()) {
        return ExtensionBrokerResponseFactory.notFound(
            request.getRequestId(),
            "NotFound",
            "Could not find adapter with id " + appId
        );
      }

      description = adapterDescriptionOpt.get();
    } else if (PROVIDER_DATA_PROCESSOR.equals(provider)) {
      description = dataProcessorPipelineElementManagement.getDescription(appId);
    } else if (PROVIDER_DATA_SINK.equals(provider)) {
      description = dataSinkPipelineElementManagement.getDescription(appId);
    } else if (PROVIDER_DATA_STREAM.equals(provider)) {
      description = dataStreamPipelineElementManagement.getDescription(appId);
    } else {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Unsupported provider for description update: " + provider
      );
    }

    var payload = objectMapper.writeValueAsString(description);
    return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
  }
}
