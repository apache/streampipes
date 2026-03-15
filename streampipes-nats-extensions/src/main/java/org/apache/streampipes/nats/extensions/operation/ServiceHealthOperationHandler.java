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

import org.apache.streampipes.extensions.management.monitoring.MonitoringManagement;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceHealthOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = "SERVICE_HEALTH";
  private static final String TOPIC_OPERATION_SEGMENT = "service-health";

  private final ObjectMapper objectMapper;
  private final MonitoringManagement monitoringManagement;

  public ServiceHealthOperationHandler(ObjectMapper objectMapper,
                                       MonitoringManagement monitoringManagement) {
    this.objectMapper = objectMapper;
    this.monitoringManagement = monitoringManagement;
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
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Invalid topic for service health operation: " + context.topic()
      );
    }

    var payload = objectMapper.writeValueAsString(monitoringManagement.getMonitoringInfos());
    return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
  }
}
