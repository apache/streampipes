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

import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperation;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

import java.util.Optional;
import java.util.function.BiFunction;

public class AdapterAssetOperationHandler<T> implements ExtensionBrokerOperationHandler {

  private final String operation;
  private final String topicOperationSegment;
  private final String requestLabel;
  private final AssetProvider<T> assetProvider;
  private final BiFunction<String, T, ExtensionServiceBrokerResponseEnvelope> successResponseFactory;

  public AdapterAssetOperationHandler(
      ExtensionServiceBrokerOperation brokerOperation,
      String requestLabel,
      AssetProvider<T> assetProvider,
      BiFunction<String, T, ExtensionServiceBrokerResponseEnvelope> successResponseFactory
  ) {
    this.operation = brokerOperation.operationId();
    this.topicOperationSegment = brokerOperation.firstTopicSegment();
    this.requestLabel = requestLabel;
    this.assetProvider = assetProvider;
    this.successResponseFactory = successResponseFactory;
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
    if (operationSegments.isEmpty() || !topicOperationSegment.equals(operationSegments.get(0))) {
      return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
          request.getRequestId(),
          "Could not resolve " + requestLabel + " from topic " + context.topic()
      );
    }

    var appId = ExtensionBrokerTopicParser.extractTail(context.topic(), context.subscriptionBaseTopic(), 1);
    if (ExtensionBrokerResponseFactory.isBlank(appId)) {
      return ExtensionBrokerResponseFactory.badRequestInvalidTopic(
          request.getRequestId(),
          "Missing appId in topic " + context.topic()
      );
    }

    var asset = assetProvider.get(appId);
    if (asset.isEmpty()) {
      return ExtensionBrokerResponseFactory.notFound(
          request.getRequestId(),
          "Could not find adapter with id " + appId
      );
    }

    return successResponseFactory.apply(request.getRequestId(), asset.get());
  }

  @FunctionalInterface
  public interface AssetProvider<T> {

    Optional<T> get(String appId) throws Exception;
  }
}
