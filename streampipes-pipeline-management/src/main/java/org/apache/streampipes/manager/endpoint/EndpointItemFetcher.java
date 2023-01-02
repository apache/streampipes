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

package org.apache.streampipes.manager.endpoint;

import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpoint;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpointItem;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EndpointItemFetcher {
  Logger logger = LoggerFactory.getLogger(EndpointItemFetcher.class);

  private List<ExtensionsServiceEndpoint> extensionsServiceEndpoints;

  public EndpointItemFetcher(List<ExtensionsServiceEndpoint> extensionsServiceEndpoints) {
    this.extensionsServiceEndpoints = extensionsServiceEndpoints;
  }

  public List<ExtensionsServiceEndpointItem> getItems() {
    List<ExtensionsServiceEndpointItem> endpointItems = new ArrayList<>();
    extensionsServiceEndpoints.forEach(e -> endpointItems.addAll(getEndpointItems(e)));
    return endpointItems;
  }

  private List<ExtensionsServiceEndpointItem> getEndpointItems(ExtensionsServiceEndpoint e) {
    try {
      String result = Request.Get(e.getEndpointUrl())
          .addHeader(new BasicHeader("Accept", MediaType.APPLICATION_JSON_VALUE))
          .connectTimeout(1000)
          .execute()
          .returnContent()
          .asString();

      return JacksonSerializer.getObjectMapper()
          .readValue(result, new TypeReference<List<ExtensionsServiceEndpointItem>>() {
          });
    } catch (IOException e1) {
      logger.warn("Processing Element Descriptions could not be fetched from RDF endpoint: " + e.getEndpointUrl());
      return new ArrayList<>();
    }
  }
}
