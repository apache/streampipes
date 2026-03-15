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

import org.apache.streampipes.extensions.management.migration.AdapterMigrationHandler;
import org.apache.streampipes.extensions.management.migration.DataProcessorMigrationHandler;
import org.apache.streampipes.extensions.management.migration.DataSinkMigrationHandler;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.nats.extensions.ExtensionBrokerOperationHandler;
import org.apache.streampipes.nats.extensions.ExtensionBrokerRequestContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Locale;

public class MigrationOperationHandler implements ExtensionBrokerOperationHandler {

  private static final String OPERATION = "MIGRATION";
  private static final String TOPIC_OPERATION_SEGMENT = "migration";
  private static final String TYPE_ADAPTER = "adapter";
  private static final String TYPE_PROCESSOR = "processor";
  private static final String TYPE_SINK = "sink";

  private final ObjectMapper objectMapper;
  private final AdapterMigrationHandler adapterMigrationHandler;
  private final DataProcessorMigrationHandler dataProcessorMigrationHandler;
  private final DataSinkMigrationHandler dataSinkMigrationHandler;

  public MigrationOperationHandler(ObjectMapper objectMapper,
                                   AdapterMigrationHandler adapterMigrationHandler,
                                   DataProcessorMigrationHandler dataProcessorMigrationHandler,
                                   DataSinkMigrationHandler dataSinkMigrationHandler) {
    this.objectMapper = objectMapper;
    this.adapterMigrationHandler = adapterMigrationHandler;
    this.dataProcessorMigrationHandler = dataProcessorMigrationHandler;
    this.dataSinkMigrationHandler = dataSinkMigrationHandler;
  }

  @Override
  public String operation() {
    return OPERATION;
  }

  @Override
  public ExtensionServiceBrokerResponseEnvelope handle(ExtensionServiceBrokerRequestEnvelope request,
                                                       ExtensionBrokerRequestContext context) throws Exception {
    if (ExtensionBrokerResponseFactory.isBlank(request.getPayload())) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidPayload",
          "Missing migration payload"
      );
    }

    var operationSegments = ExtensionBrokerTopicParser.extractOperationSegments(
        context.topic(),
        context.subscriptionBaseTopic()
    );
    if (operationSegments.isEmpty() || !TOPIC_OPERATION_SEGMENT.equals(operationSegments.get(0))) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Could not resolve migration type from topic " + context.topic()
      );
    }

    var migrationType = ExtensionBrokerTopicParser.extractTail(context.topic(), context.subscriptionBaseTopic(), 1)
        .toLowerCase(Locale.ROOT);
    if (ExtensionBrokerResponseFactory.isBlank(migrationType)) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Missing migration type in topic " + context.topic()
      );
    }

    MigrationResult<?> migrationResult;
    try {
      migrationResult = switch (migrationType) {
        case TYPE_ADAPTER -> adapterMigrationHandler.handleMigration(
            objectMapper.readValue(
                request.getPayload(),
                new TypeReference<>() {
                }
            )
        );
        case TYPE_PROCESSOR -> dataProcessorMigrationHandler.handleMigration(
            objectMapper.readValue(
                request.getPayload(),
                new TypeReference<>() {
                }
            )
        );
        case TYPE_SINK -> dataSinkMigrationHandler.handleMigration(
            objectMapper.readValue(
                request.getPayload(),
                new TypeReference<>() {
                }
            )
        );
        default -> null;
      };
    } catch (IOException e) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidPayload",
          "Invalid migration payload"
      );
    }

    if (migrationResult == null) {
      return ExtensionBrokerResponseFactory.badRequest(
          request.getRequestId(),
          "InvalidTopic",
          "Unsupported migration type in topic " + context.topic()
      );
    }

    var payload = objectMapper.writeValueAsString(migrationResult);
    return ExtensionBrokerResponseFactory.ok(request.getRequestId(), payload);
  }
}
