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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerSampleDataRequestManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerErrorEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SampleDataOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = "SAMPLE_DATA";
  private static final String TOPIC_OPERATION_SEGMENT = "adapter-sample-data";

  private final ObjectMapper objectMapper;
  private final AdapterWorkerSampleDataRequestManagement adapterWorkerSampleDataRequestManagement;

  public SampleDataOperationHandler(
      ObjectMapper objectMapper,
      AdapterWorkerSampleDataRequestManagement adapterWorkerSampleDataRequestManagement
  ) {
    this.objectMapper = objectMapper;
    this.adapterWorkerSampleDataRequestManagement = adapterWorkerSampleDataRequestManagement;
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
          "Missing adapter description payload"
      );
    }

    var operationSegments = ExtensionBrokerTopicParser.extractOperationSegments(
        context.topic(),
        context.subscriptionBaseTopic()
    );
    if (operationSegments.isEmpty() || !TOPIC_OPERATION_SEGMENT.equals(operationSegments.get(0))) {
      return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
          request.getRequestId(),
          "Invalid topic for sample data operation: " + context.topic()
      );
    }

    AdapterDescription adapterDescription;
    try {
      adapterDescription = objectMapper.readValue(request.getPayload(), AdapterDescription.class);
    } catch (IOException e) {
      return ExtensionBrokerResponseFactory.badRequestInvalidPayload(
          request.getRequestId(),
          "Invalid adapter description payload"
      );
    }

    try {
      var sampleData = adapterWorkerSampleDataRequestManagement.getSampleData(adapterDescription);
      return ExtensionBrokerResponseFactory.ok(request.getRequestId(), objectMapper.writeValueAsString(sampleData));
    } catch (AdapterException | ParseException e) {
      return new ExtensionServiceBrokerResponseEnvelope(
          request.getRequestId(),
          ExtensionBrokerResponseFactory.HTTP_STATUS_INTERNAL_SERVER_ERROR,
          objectMapper.writeValueAsString(SpLogMessage.from(e)),
          new ExtensionServiceBrokerErrorEnvelope(e.getClass().getSimpleName(), e.getMessage())
      );
    }
  }
}
