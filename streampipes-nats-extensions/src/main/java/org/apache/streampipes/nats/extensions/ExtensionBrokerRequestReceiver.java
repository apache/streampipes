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
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerRequestManagement;
import org.apache.streampipes.extensions.management.monitoring.ServiceMonitorManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerErrorEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerRequestEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerResponseEnvelope;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerTopics;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceTransportMode;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionBrokerRequestReceiver {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionBrokerRequestReceiver.class);

  private static final String ADAPTER_STATE_CHANGE_OPERATION = "ADAPTER_STATE_CHANGE";
  private static final String SERVICE_LOAD_OPERATION = "SERVICE_LOAD";
  private static final String STATE_CHANGE_START = "start";
  private static final String STATE_CHANGE_STOP = "stop";
  private static final int HTTP_STATUS_OK = 200;
  private static final int HTTP_STATUS_BAD_REQUEST = 400;
  private static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
  private static final int HTTP_STATUS_NOT_IMPLEMENTED = 501;

  private final ObjectMapper objectMapper;
  private final ServiceMonitorManagement serviceMonitorManagement;
  private final AdapterWorkerRequestManagement adapterWorkerRequestManagement;

  private Connection natsConnection;
  private Dispatcher dispatcher;

  public ExtensionBrokerRequestReceiver() {
    this(new ServiceMonitorManagement(), new AdapterWorkerRequestManagement());
  }

  public ExtensionBrokerRequestReceiver(ServiceMonitorManagement serviceMonitorManagement,
                                        AdapterWorkerRequestManagement adapterWorkerRequestManagement) {
    this.objectMapper = JacksonSerializer.getObjectMapper();
    this.serviceMonitorManagement = serviceMonitorManagement;
    this.adapterWorkerRequestManagement = adapterWorkerRequestManagement;
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
      response = error(null, HTTP_STATUS_INTERNAL_SERVER_ERROR, e);
    }

    publishResponse(replyTo, response);
  }

  private ExtensionServiceBrokerResponseEnvelope handleRequest(ExtensionServiceBrokerRequestEnvelope request,
                                                               String topic) {
    try {
      if (SERVICE_LOAD_OPERATION.equals(request.getOperation())) {
        var payload = objectMapper.writeValueAsString(serviceMonitorManagement.getCurrentReport());
        return new ExtensionServiceBrokerResponseEnvelope(
            request.getRequestId(),
            HTTP_STATUS_OK,
            payload,
            null
        );
      }

      if (ADAPTER_STATE_CHANGE_OPERATION.equals(request.getOperation())) {
        return handleAdapterStateChangeRequest(request, topic);
      }

      return new ExtensionServiceBrokerResponseEnvelope(
          request.getRequestId(),
          HTTP_STATUS_NOT_IMPLEMENTED,
          null,
          new ExtensionServiceBrokerErrorEnvelope(
              "UnsupportedOperation",
              "No broker handler available for operation " + request.getOperation()
          )
      );
    } catch (Exception e) {
      return error(request.getRequestId(), HTTP_STATUS_INTERNAL_SERVER_ERROR, e);
    }
  }

  private ExtensionServiceBrokerResponseEnvelope handleAdapterStateChangeRequest(
      ExtensionServiceBrokerRequestEnvelope request,
      String topic
  ) throws Exception {
    if (request.getPayload() == null || request.getPayload().isBlank()) {
      return new ExtensionServiceBrokerResponseEnvelope(
          request.getRequestId(),
          HTTP_STATUS_BAD_REQUEST,
          null,
          new ExtensionServiceBrokerErrorEnvelope("InvalidPayload", "Missing adapter payload")
      );
    }

    var adapterDescription = objectMapper.readValue(request.getPayload(), AdapterDescription.class);
    var command = extractStateChangeCommand(topic);

    try {
      if (STATE_CHANGE_START.equals(command)) {
        var payload = objectMapper.writeValueAsString(adapterWorkerRequestManagement.invokeAdapter(adapterDescription));
        return new ExtensionServiceBrokerResponseEnvelope(request.getRequestId(), HTTP_STATUS_OK, payload, null);
      }

      if (STATE_CHANGE_STOP.equals(command)) {
        var payload = objectMapper.writeValueAsString(adapterWorkerRequestManagement.stopAdapter(adapterDescription));
        return new ExtensionServiceBrokerResponseEnvelope(request.getRequestId(), HTTP_STATUS_OK, payload, null);
      }

      return new ExtensionServiceBrokerResponseEnvelope(
          request.getRequestId(),
          HTTP_STATUS_BAD_REQUEST,
          null,
          new ExtensionServiceBrokerErrorEnvelope(
              "InvalidCommand",
              "Unknown adapter state change command in topic " + topic
          )
      );
    } catch (AdapterException e) {
      return new ExtensionServiceBrokerResponseEnvelope(
          request.getRequestId(),
          HTTP_STATUS_INTERNAL_SERVER_ERROR,
          objectMapper.writeValueAsString(e),
          new ExtensionServiceBrokerErrorEnvelope(e.getClass().getSimpleName(), e.getMessage())
      );
    }
  }

  private String extractStateChangeCommand(String topic) {
    int separatorIndex = topic.lastIndexOf('.');
    if (separatorIndex < 0 || separatorIndex + 1 >= topic.length()) {
      return "";
    }

    return topic.substring(separatorIndex + 1);
  }

  private ExtensionServiceBrokerResponseEnvelope error(String requestId, int statusCode, Exception e) {
    return new ExtensionServiceBrokerResponseEnvelope(
        requestId,
        statusCode,
        null,
        new ExtensionServiceBrokerErrorEnvelope(e.getClass().getSimpleName(), e.getMessage())
    );
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
}
