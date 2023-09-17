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

package org.apache.streampipes.manager.monitoring.pipeline;


import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.monitoring.SpEndpointMonitoringInfo;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ExtensionsServiceLogExecutor implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsServiceLogExecutor.class);

  private static final String LOG_PATH = "/monitoring";

  public void run() {
    triggerUpdate();
  }

  public void triggerUpdate() {
    List<String> serviceEndpoints = getActiveExtensionsEndpoints();

    serviceEndpoints.forEach(serviceEndpoint -> {
      try {
        String response = makeRequest(serviceEndpoint).execute().returnContent().asString();
        SpEndpointMonitoringInfo monitoringInfo = parseLogResponse(response);
        ExtensionsLogProvider.INSTANCE.addMonitoringInfos(monitoringInfo);
      } catch (IOException e) {
        LOG.info("Could not fetch log info from endpoint {}", serviceEndpoint);
      }
    });
  }

  private Request makeRequest(String serviceEndpointUrl) {
    return ExtensionServiceExecutions.extServiceGetRequest(makeLogUrl(serviceEndpointUrl));
  }

  private Principal getServiceAdmin() {
    return new SpResourceManager().manageUsers().getServiceAdmin();
  }

  private List<String> getActiveExtensionsEndpoints() {
    return SpServiceDiscovery.getServiceDiscovery().getServiceEndpoints(
        DefaultSpServiceTypes.EXT,
        true,
        List.of(DefaultSpServiceTags.PE.asString(), DefaultSpServiceTags.CONNECT_WORKER.asString())
    );
  }

  private String makeLogUrl(String baseUrl) {
    return baseUrl + LOG_PATH;
  }

  private SpEndpointMonitoringInfo parseLogResponse(String response) throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().readValue(response, SpEndpointMonitoringInfo.class);
  }
}
