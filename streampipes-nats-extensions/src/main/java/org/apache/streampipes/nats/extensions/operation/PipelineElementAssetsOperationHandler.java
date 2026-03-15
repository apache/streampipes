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

import org.apache.streampipes.extensions.management.connect.AdapterAssetManagement;
import org.apache.streampipes.extensions.management.pe.DataProcessorPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataSinkPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataStreamPipelineElementManagement;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

public class PipelineElementAssetsOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = "PIPELINE_ELEMENT_ASSETS";
  private static final String TOPIC_OPERATION_SEGMENT = "pipeline-element-assets";
  private static final String PROVIDER_DATA_PROCESSOR = "DATA_PROCESSOR";
  private static final String PROVIDER_DATA_SINK = "DATA_SINK";
  private static final String PROVIDER_DATA_STREAM = "DATA_STREAM";
  private static final String PROVIDER_ADAPTER = "ADAPTER";

  private final DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement;
  private final DataSinkPipelineElementManagement dataSinkPipelineElementManagement;
  private final DataStreamPipelineElementManagement dataStreamPipelineElementManagement;
  private final AdapterAssetManagement adapterAssetManagement;

  public PipelineElementAssetsOperationHandler(
      DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement,
      DataSinkPipelineElementManagement dataSinkPipelineElementManagement,
      DataStreamPipelineElementManagement dataStreamPipelineElementManagement,
      AdapterAssetManagement adapterAssetManagement
  ) {
    this.dataProcessorPipelineElementManagement = dataProcessorPipelineElementManagement;
    this.dataSinkPipelineElementManagement = dataSinkPipelineElementManagement;
    this.dataStreamPipelineElementManagement = dataStreamPipelineElementManagement;
    this.adapterAssetManagement = adapterAssetManagement;
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

    byte[] assetBytes;
    if (PROVIDER_DATA_PROCESSOR.equals(provider)) {
      assetBytes = dataProcessorPipelineElementManagement.getAssets(appId);
    } else if (PROVIDER_DATA_SINK.equals(provider)) {
      assetBytes = dataSinkPipelineElementManagement.getAssets(appId);
    } else if (PROVIDER_DATA_STREAM.equals(provider)) {
      assetBytes = dataStreamPipelineElementManagement.getAssets(appId);
    } else if (PROVIDER_ADAPTER.equals(provider)) {
      assetBytes = adapterAssetManagement.getAssets(appId).get();
    } else {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Unsupported provider for pipeline asset request: " + provider
      );
    }

    return ExtensionBrokerResponseFactory.okBytes(request.getRequestId(), assetBytes);
  }
}
