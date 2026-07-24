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

package org.apache.streampipes.nats.extensions.operation.connect;

import org.apache.streampipes.extensions.management.monitoring.AdapterHealthCheckManager;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerTopicParser;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AdapterHealthOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = ExtensionServiceBrokerOperations.ADAPTER_HEALTH.operationId();
  private static final String TOPIC_OPERATION_SEGMENT =
      ExtensionServiceBrokerOperations.ADAPTER_HEALTH.firstTopicSegment();
  private static final String TRIGGER_COMMAND = "trigger";

  private final ObjectMapper objectMapper;

  public AdapterHealthOperationHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
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
    if (operationSegments.isEmpty() || !TOPIC_OPERATION_SEGMENT.equals(operationSegments.get(0))) {
      return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
          request.getRequestId(),
          "Invalid topic for adapter health operation: " + context.topic()
      );
    }

    if (operationSegments.size() == 1) {
      var payload = objectMapper.writeValueAsString(
          AdapterHealthCheckManager.INSTANCE.getAllHealthStatuses()
      );
      return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
    }

    if (operationSegments.size() == 3 && TRIGGER_COMMAND.equals(operationSegments.get(2))) {
      AdapterHealthCheckManager.INSTANCE.triggerHealthCheck(operationSegments.get(1));
      return ExtensionBrokerResponseFactory.ok(request.getRequestId(), null);
    }

    return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
        request.getRequestId(),
        "Invalid topic for adapter health operation: " + context.topic()
    );
  }
}
