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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.extensions.management.connect.AdapterAssetManagement;
import org.apache.streampipes.extensions.management.connect.AdapterDescriptionManagement;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerRequestManagement;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerSampleDataRequestManagement;
import org.apache.streampipes.extensions.management.connect.RuntimeResolvableManagement;
import org.apache.streampipes.extensions.management.migration.AdapterMigrationHandler;
import org.apache.streampipes.extensions.management.migration.DataProcessorMigrationHandler;
import org.apache.streampipes.extensions.management.migration.DataSinkMigrationHandler;
import org.apache.streampipes.extensions.management.monitoring.HealthCheckManagement;
import org.apache.streampipes.extensions.management.monitoring.MonitoringManagement;
import org.apache.streampipes.extensions.management.monitoring.ServiceMonitorManagement;
import org.apache.streampipes.extensions.management.pe.DataProcessorPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataSinkPipelineElementManagement;
import org.apache.streampipes.extensions.management.pe.DataStreamPipelineElementManagement;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceTransportMode;
import org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory;
import org.apache.streampipes.nats.extensions.operation.connect.AdapterAssetOperationHandler;
import org.apache.streampipes.nats.extensions.operation.connect.AdapterStateChangeOperationHandler;
import org.apache.streampipes.nats.extensions.operation.connect.RuntimeOptionsOperationHandler;
import org.apache.streampipes.nats.extensions.operation.connect.SampleDataOperationHandler;
import org.apache.streampipes.nats.extensions.operation.function.FunctionStopOperationHandler;
import org.apache.streampipes.nats.extensions.operation.migration.MigrationOperationHandler;
import org.apache.streampipes.nats.extensions.operation.monitoring.ExtensionInstanceHealthOperationHandler;
import org.apache.streampipes.nats.extensions.operation.monitoring.ServiceHealthOperationHandler;
import org.apache.streampipes.nats.extensions.operation.monitoring.ServiceLoadOperationHandler;
import org.apache.streampipes.nats.extensions.operation.pe.ContainerProvidedOptionsOperationHandler;
import org.apache.streampipes.nats.extensions.operation.pe.DescriptionOperationHandler;
import org.apache.streampipes.nats.extensions.operation.pe.OutputSchemaOperationHandler;
import org.apache.streampipes.nats.extensions.operation.pe.PipelineElementAssetsOperationHandler;
import org.apache.streampipes.nats.extensions.operation.pe.PipelineElementDetachOperationHandler;
import org.apache.streampipes.nats.extensions.operation.pe.PipelineElementInvocationOperationHandler;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.streampipes.nats.extensions.operation.ExtensionBrokerResponseFactory.HTTP_STATUS_INTERNAL_SERVER_ERROR;

public class ExtensionBrokerRequestReceiver {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionBrokerRequestReceiver.class);

  private final ObjectMapper objectMapper;
  private final Map<String, ExtensionBrokerOperationHandler> operationHandlers;

  private Connection natsConnection;
  private Dispatcher dispatcher;
  private String subscriptionBaseTopic;

  public ExtensionBrokerRequestReceiver() {
    this(List.of());
  }

  public ExtensionBrokerRequestReceiver(List<ExtensionBrokerOperationHandler> additionalOperationHandlers) {
    this(
        new ServiceMonitorManagement(),
        new AdapterWorkerRequestManagement(),
        new AdapterAssetManagement(),
        new DataProcessorPipelineElementManagement(),
        new DataSinkPipelineElementManagement(),
        new DataStreamPipelineElementManagement(),
        additionalOperationHandlers
    );
  }

  public ExtensionBrokerRequestReceiver(ServiceMonitorManagement serviceMonitorManagement,
                                        AdapterWorkerRequestManagement adapterWorkerRequestManagement,
                                        AdapterAssetManagement adapterAssetManagement,
                                        DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement,
                                        DataSinkPipelineElementManagement dataSinkPipelineElementManagement,
                                        DataStreamPipelineElementManagement dataStreamPipelineElementManagement,
                                        List<ExtensionBrokerOperationHandler> additionalOperationHandlers) {
    this.objectMapper = JacksonSerializer.getObjectMapper();
    this.operationHandlers = createOperationHandlers(
        objectMapper,
        serviceMonitorManagement,
        adapterWorkerRequestManagement,
        adapterAssetManagement,
        dataProcessorPipelineElementManagement,
        dataSinkPipelineElementManagement,
        dataStreamPipelineElementManagement,
        additionalOperationHandlers
    );
  }

  public synchronized boolean start(String serviceId,
                                    ExtensionServiceTransportMode mode,
                                    String topicPrefix) {
    if (!mode.supportsNats()) {
      return false;
    }

    try {
      var env = Environments.getEnvironment();
      String natsUrl = "nats://" + env.getNatsHost().getValueOrDefault()
          + ":" + env.getNatsPort().getValueOrDefault();
      this.natsConnection = Nats.connect(natsUrl);

      this.subscriptionBaseTopic = ExtensionServiceBrokerTopics.serviceTopic(
          topicPrefix,
          serviceId,
          List.of()
      );
      String subscriptionTopic = ExtensionServiceBrokerTopics.serviceWildcard(topicPrefix, serviceId);
      this.dispatcher = natsConnection.createDispatcher(this::onMessage);
      this.dispatcher.subscribe(subscriptionTopic);

      LOG.info("Extension broker receiver listening on topic {}", subscriptionTopic);
      return true;
    } catch (Exception e) {
      LOG.warn("Could not start extension broker receiver", e);
      stop();
      return false;
    }
  }

  public synchronized void stop() {
    if (natsConnection != null && dispatcher != null) {
      natsConnection.closeDispatcher(dispatcher);
      dispatcher = null;
    }

    if (natsConnection != null) {
      try {
        natsConnection.close();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LOG.warn("Interrupted while closing extension broker receiver", e);
      } finally {
        natsConnection = null;
      }
    }

    subscriptionBaseTopic = null;
  }

  private void onMessage(Message message) {
    String replyTo = message.getReplyTo();
    if (replyTo == null || replyTo.isBlank()) {
      return;
    }

    ExtensionServiceBrokerResponseEnvelope response;
    try {
      var request = objectMapper.readValue(message.getData(), ExtensionServiceBrokerRequestEnvelope.class);
      response = handleRequest(request, message.getSubject());
    } catch (Exception e) {
      response = ExtensionBrokerResponseFactory.error(null, HTTP_STATUS_INTERNAL_SERVER_ERROR, e);
    }

    publishResponse(replyTo, response);
  }

  private ExtensionServiceBrokerResponseEnvelope handleRequest(ExtensionServiceBrokerRequestEnvelope request,
                                                               String topic) {
    try {
      var operationHandler = operationHandlers.get(request.getOperation());
      if (operationHandler != null) {
        return operationHandler.handle(request, new ExtensionBrokerRequestContext(topic, subscriptionBaseTopic));
      }

      return ExtensionBrokerResponseFactory.unsupportedOperation(
          request.getRequestId(),
          "No broker handler available for operation " + request.getOperation()
      );
    } catch (Exception e) {
      return ExtensionBrokerResponseFactory.error(request.getRequestId(), HTTP_STATUS_INTERNAL_SERVER_ERROR, e);
    }
  }

  private void publishResponse(String replyTo, ExtensionServiceBrokerResponseEnvelope response) {
    if (natsConnection == null) {
      return;
    }

    try {
      natsConnection.publish(replyTo, objectMapper.writeValueAsBytes(response));
    } catch (Exception e) {
      LOG.warn("Could not publish broker response to subject {}", replyTo, e);
    }
  }

  private Map<String, ExtensionBrokerOperationHandler> createOperationHandlers(
      ObjectMapper objectMapper,
      ServiceMonitorManagement serviceMonitorManagement,
      AdapterWorkerRequestManagement adapterWorkerRequestManagement,
      AdapterAssetManagement adapterAssetManagement,
      DataProcessorPipelineElementManagement dataProcessorPipelineElementManagement,
      DataSinkPipelineElementManagement dataSinkPipelineElementManagement,
      DataStreamPipelineElementManagement dataStreamPipelineElementManagement,
      List<ExtensionBrokerOperationHandler> additionalOperationHandlers
  ) {
    var adapterDescriptionManagement = new AdapterDescriptionManagement();
    var healthCheckManagement = new HealthCheckManagement();
    var monitoringManagement = new MonitoringManagement();
    var runtimeResolvableManagement = new RuntimeResolvableManagement();
    var sampleDataRequestManagement = new AdapterWorkerSampleDataRequestManagement();
    var extensions = additionalOperationHandlers == null ? List.<ExtensionBrokerOperationHandler>of()
        : additionalOperationHandlers;

    return Stream.concat(
            Stream.of(
            new ServiceLoadOperationHandler(objectMapper, serviceMonitorManagement),
            new FunctionStopOperationHandler(objectMapper),
            new ExtensionInstanceHealthOperationHandler(objectMapper, healthCheckManagement),
            new ServiceHealthOperationHandler(objectMapper, monitoringManagement),
            new ContainerProvidedOptionsOperationHandler(
                objectMapper,
                dataProcessorPipelineElementManagement,
                dataSinkPipelineElementManagement
            ),
            new RuntimeOptionsOperationHandler(objectMapper, runtimeResolvableManagement),
            new SampleDataOperationHandler(objectMapper, sampleDataRequestManagement),
            new OutputSchemaOperationHandler(
                objectMapper,
                dataProcessorPipelineElementManagement,
                dataSinkPipelineElementManagement
            ),
            new MigrationOperationHandler(
                objectMapper,
                new AdapterMigrationHandler(),
                new DataProcessorMigrationHandler(),
                new DataSinkMigrationHandler()
            ),
            new DescriptionOperationHandler(
                ExtensionServiceBrokerOperations.DESCRIPTION_UPDATE,
                "description update",
                objectMapper,
                adapterDescriptionManagement,
                dataProcessorPipelineElementManagement,
                dataSinkPipelineElementManagement,
                dataStreamPipelineElementManagement
            ),
            new DescriptionOperationHandler(
                ExtensionServiceBrokerOperations.EXTENSION_DESCRIPTION,
                "extension description",
                objectMapper,
                adapterDescriptionManagement,
                dataProcessorPipelineElementManagement,
                dataSinkPipelineElementManagement,
                dataStreamPipelineElementManagement
            ),
            new AdapterStateChangeOperationHandler(objectMapper, adapterWorkerRequestManagement),
            new PipelineElementAssetsOperationHandler(
                dataProcessorPipelineElementManagement,
                dataSinkPipelineElementManagement,
                dataStreamPipelineElementManagement,
                adapterAssetManagement
            ),
            new PipelineElementInvocationOperationHandler(
                objectMapper,
                dataProcessorPipelineElementManagement,
                dataSinkPipelineElementManagement
            ),
            new PipelineElementDetachOperationHandler(
                objectMapper,
                dataProcessorPipelineElementManagement,
                dataSinkPipelineElementManagement
            ),
            new AdapterAssetOperationHandler<>(
                ExtensionServiceBrokerOperations.ADAPTER_ASSETS,
                "adapter asset request",
                adapterAssetManagement::getAssets,
                ExtensionBrokerResponseFactory::okBytes
            ),
            new AdapterAssetOperationHandler<>(
                ExtensionServiceBrokerOperations.ADAPTER_ICON_ASSET,
                "adapter icon request",
                adapterAssetManagement::getIconAsset,
                ExtensionBrokerResponseFactory::okBytes
            ),
            new AdapterAssetOperationHandler<>(
                ExtensionServiceBrokerOperations.ADAPTER_DOCUMENTATION_ASSET,
                "adapter documentation request",
                adapterAssetManagement::getDocumentationAsset,
                ExtensionBrokerResponseFactory::ok
            )
        ),
            extensions.stream()
        )
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableMap(
            ExtensionBrokerOperationHandler::operation,
            handler -> handler,
            (left, right) -> {
              throw new IllegalStateException(
                  "Duplicate extension broker operation handler for operation " + left.operation()
              );
            }
        ));
  }
}
