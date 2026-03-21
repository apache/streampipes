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

package org.apache.streampipes.integration.sinks.azure;

import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;
import org.apache.streampipes.extensions.connectors.camel.kamelet.provider.KameletTemplateProvider;
import org.apache.streampipes.extensions.connectors.camel.sink.ApacheCamelKameletSink;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClientBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.component.azure.cosmosdb.CosmosDbComponent;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public class TestAzureCosmosDbApacheCamelKameletSink extends ApacheCamelKameletSink {

  private static final Set<String> LOCAL_HOSTS = Set.of("localhost", "127.0.0.1");

  private CosmosAsyncClient cosmosAsyncClient;

  public TestAzureCosmosDbApacheCamelKameletSink(String appId,
                                                 String templateName,
                                                 KameletTemplateProvider templateProvider,
                                                 AssetResolver assetResolver) {
    super(appId, templateName, templateProvider, assetResolver);
  }

  @Override
  protected ApacheCamelKameletSink newInstance() {
    return new TestAzureCosmosDbApacheCamelKameletSink(
        getAppId(),
        getTemplateName(),
        getTemplateProvider(),
        getAssetResolver()
    );
  }

  @Override
  protected void customizeCamelContext(CamelContext context,
                                       KameletTemplate template,
                                       Map<String, Object> params) {
    String databaseEndpoint = stringParam(params, "databaseEndpoint");
    if (!isLocalCosmosEndpoint(databaseEndpoint)) {
      return;
    }

    // The Cosmos emulator rejects the direct-mode /addresses lookup, so tests inject a gateway client here.
    String accountKey = stringParam(params, "accountKey");
    this.cosmosAsyncClient = new CosmosClientBuilder()
        .endpoint(databaseEndpoint)
        .key(accountKey)
        .gatewayMode()
        .contentResponseOnWriteEnabled(true)
        .buildAsyncClient();

    CosmosDbComponent cosmosDbComponent = context.getComponent("azure-cosmosdb", CosmosDbComponent.class);
    cosmosDbComponent.getConfiguration().setCosmosAsyncClient(cosmosAsyncClient);
  }

  @Override
  protected void onSinkStopped() {
    if (cosmosAsyncClient != null) {
      cosmosAsyncClient.close();
      cosmosAsyncClient = null;
    }
  }

  private boolean isLocalCosmosEndpoint(String databaseEndpoint) {
    if (databaseEndpoint == null || databaseEndpoint.isBlank()) {
      return false;
    }

    try {
      URI endpointUri = URI.create(databaseEndpoint);
      return LOCAL_HOSTS.contains(endpointUri.getHost());
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private String stringParam(Map<String, Object> params,
                             String key) {
    Object value = params.get(key);
    return value == null ? null : String.valueOf(value);
  }
}
