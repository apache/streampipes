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
package org.apache.streampipes.manager.health;

import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PipelineElementEndpointHealthCheck {

  private static final String InstancePath = "/instances";

  private final String endpointUrl;

  public PipelineElementEndpointHealthCheck(String endpointUrl) {
    this.endpointUrl = endpointUrl;
  }

  public List<String> checkRunningInstances() throws IOException {
    var request = ExtensionServiceExecutions.extServiceGetRequest(makeRequestUrl());
    return asList(request.execute().returnContent().toString());
  }

  private List<String> asList(String json) throws JsonProcessingException {
    return Arrays.asList(JacksonSerializer.getObjectMapper().readValue(json, String[].class));
  }

  private String makeRequestUrl() {
    return endpointUrl + InstancePath;
  }
}
