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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.fluent.Request;
import org.apache.streampipes.model.monitoring.SpEndpointMonitoringInfo;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;

import java.io.IOException;
import java.util.List;

public class ExtensionsServiceLogExecutor implements Runnable {

  private static final String LOG_PATH = "/monitoring";

  public void run() {
    List<String> serviceEndpoints = getActiveExtensionsEndpoints();

    serviceEndpoints.forEach(serviceEndpoint -> {
      try {
        String response = Request.Get(makeLogUrl(serviceEndpoint)).execute().returnContent().asString();
        SpEndpointMonitoringInfo monitoringInfo = parseLogResponse(response);
        ExtensionsLogProvider.INSTANCE.addMonitoringInfos(monitoringInfo);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private List<String> getActiveExtensionsEndpoints() {
    return SpServiceDiscovery.getServiceDiscovery().getServiceEndpoints(
        DefaultSpServiceGroups.EXT,
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
