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
package org.apache.streampipes.health.monitoring;

import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.model.health.ExtensionInstanceHealth;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class PipelineElementEndpointHealthCheck {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineElementEndpointHealthCheck.class);
  private static final String InstancePath = "/health";

  private final String serviceBaseUrl;

  public PipelineElementEndpointHealthCheck(String serviceBaseUrl) {
    this.serviceBaseUrl = serviceBaseUrl;
  }

  public ExtensionInstanceHealth checkRunningInstances() {
    try {
      var request = ExtensionServiceExecutions.extServiceGetRequest(makeRequestUrl());
      var response = request.execute().returnResponse();
      if (response.getStatusLine().getStatusCode() != 200) {
        return new ExtensionInstanceHealth(Set.of(), Set.of());
      }
      String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      return deserialize(body);

    } catch (IOException e) {
      LOG.error("Extension service {} is unavailable", serviceBaseUrl);
      return new ExtensionInstanceHealth(Set.of(), Set.of());
    }
  }

  private ExtensionInstanceHealth deserialize(String json) throws JsonProcessingException {
    return JacksonSerializer.getObjectMapper().readValue(json, ExtensionInstanceHealth.class);
  }

  private String makeRequestUrl() {
    return serviceBaseUrl + InstancePath;
  }
}
