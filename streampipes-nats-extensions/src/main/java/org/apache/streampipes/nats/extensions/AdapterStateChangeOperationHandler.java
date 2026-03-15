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

package org.apache.streampipes.nats.extensions;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerRequestManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerErrorEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerTopicParser;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AdapterStateChangeOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = "ADAPTER_STATE_CHANGE";
  private static final String COMMAND_START = "start";
  private static final String COMMAND_STOP = "stop";

  private final ObjectMapper objectMapper;
  private final AdapterWorkerRequestManagement adapterWorkerRequestManagement;

  AdapterStateChangeOperationHandler(ObjectMapper objectMapper,
                                     AdapterWorkerRequestManagement adapterWorkerRequestManagement) {
    this.objectMapper = objectMapper;
    this.adapterWorkerRequestManagement = adapterWorkerRequestManagement;
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
          "Missing adapter payload"
      );
    }

    var adapterDescription = objectMapper.readValue(request.getPayload(), AdapterDescription.class);
    var command = ExtensionBrokerTopicParser.extractLastSegment(context.topic());

    try {
      if (COMMAND_START.equals(command)) {
        var payload = objectMapper.writeValueAsString(adapterWorkerRequestManagement.invokeAdapter(adapterDescription));
        return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
      }

      if (COMMAND_STOP.equals(command)) {
        var payload = objectMapper.writeValueAsString(adapterWorkerRequestManagement.stopAdapter(adapterDescription));
        return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
      }

      return ExtensionBrokerResponseFactory.badRequestInvalidCommand(
          request.getRequestId(),
          "Unknown adapter state change command in topic " + context.topic()
      );
    } catch (AdapterException e) {
      return new ExtensionServiceBrokerResponseEnvelope(
          request.getRequestId(),
          ExtensionBrokerResponseFactory.HTTP_STATUS_INTERNAL_SERVER_ERROR,
          objectMapper.writeValueAsString(e),
          new ExtensionServiceBrokerErrorEnvelope(e.getClass().getSimpleName(), e.getMessage())
      );
    }
  }
}
