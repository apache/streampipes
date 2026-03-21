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

import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AdapterAssetOperationHandlerTest {

  @Test
  void shouldReturnAssetPayloadAndUseDecodedAppId() throws Exception {
    String requestId = "req-1";
    String topicPrefix = "sp.extensions.request";
    String serviceId = "org.apache.streampipes.extensions.demo";
    String appId = "org.apache.streampipes.adapter";
    String baseTopic = ExtensionServiceBrokerTopics.serviceTopic(topicPrefix, serviceId, List.of());
    String topic = ExtensionServiceBrokerTopics.serviceTopic(
        topicPrefix,
        serviceId,
        List.of("adapter-assets", appId)
    );

    var capturedAppId = new AtomicReference<String>();
    var handler = new AdapterAssetOperationHandler<>(
        ExtensionServiceBrokerOperations.ADAPTER_ASSETS,
        "adapter asset request",
        candidate -> {
          capturedAppId.set(candidate);
          return Optional.of("payload");
        },
        ExtensionBrokerResponseFactory::ok
    );

    var response = handler.handle(
        new ExtensionServiceBrokerRequestEnvelope(requestId, handler.operation(), null, null),
        new ExtensionBrokerRequestContext(topic, baseTopic)
    );

    assertEquals(ExtensionBrokerResponseFactory.HTTP_STATUS_OK, response.getStatusCode());
    assertEquals("payload", response.getPayload());
    assertEquals(appId, capturedAppId.get());
  }

  @Test
  void shouldReturnNotFoundWhenAssetDoesNotExist() throws Exception {
    String requestId = "req-2";
    String topicPrefix = "sp.extensions.request";
    String serviceId = "org.apache.streampipes.extensions.demo";
    String appId = "org.apache.streampipes.adapter";
    String baseTopic = ExtensionServiceBrokerTopics.serviceTopic(topicPrefix, serviceId, List.of());
    String topic = ExtensionServiceBrokerTopics.serviceTopic(
        topicPrefix,
        serviceId,
        List.of("adapter-assets", appId)
    );

    var handler = new AdapterAssetOperationHandler<>(
        ExtensionServiceBrokerOperations.ADAPTER_ASSETS,
        "adapter asset request",
        candidate -> Optional.empty(),
        ExtensionBrokerResponseFactory::ok
    );

    var response = handler.handle(
        new ExtensionServiceBrokerRequestEnvelope(requestId, handler.operation(), null, null),
        new ExtensionBrokerRequestContext(topic, baseTopic)
    );

    assertEquals(ExtensionBrokerResponseFactory.HTTP_STATUS_NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getError());
  }

  @Test
  void shouldReturnBadRequestForTopicWithWrongOperationSegment() throws Exception {
    String requestId = "req-3";
    String topicPrefix = "sp.extensions.request";
    String serviceId = "org.apache.streampipes.extensions.demo";
    String appId = "org.apache.streampipes.adapter";
    String baseTopic = ExtensionServiceBrokerTopics.serviceTopic(topicPrefix, serviceId, List.of());
    String topic = ExtensionServiceBrokerTopics.serviceTopic(
        topicPrefix,
        serviceId,
        List.of("output-schema", appId)
    );

    var handler = new AdapterAssetOperationHandler<>(
        ExtensionServiceBrokerOperations.ADAPTER_ASSETS,
        "adapter asset request",
        candidate -> Optional.of("payload"),
        ExtensionBrokerResponseFactory::ok
    );

    var response = handler.handle(
        new ExtensionServiceBrokerRequestEnvelope(requestId, handler.operation(), null, null),
        new ExtensionBrokerRequestContext(topic, baseTopic)
    );

    assertEquals(ExtensionBrokerResponseFactory.HTTP_STATUS_BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getError());
  }
}
