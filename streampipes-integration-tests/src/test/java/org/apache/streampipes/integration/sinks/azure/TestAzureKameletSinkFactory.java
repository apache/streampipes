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

import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.connectors.camel.kamelet.assets.KameletSinkAssetResolver;
import org.apache.streampipes.extensions.connectors.camel.kamelet.factory.KameletSinkFactory;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;
import org.apache.streampipes.extensions.connectors.camel.kamelet.provider.KameletTemplateProvider;

public class TestAzureKameletSinkFactory extends KameletSinkFactory {

  private static final String AZURE_COSMOS_DB_SINK = "azure-cosmosdb-sink";

  public TestAzureKameletSinkFactory(KameletTemplateProvider templateProvider,
                                     String appIdPrefix,
                                     String assetRoot) {
    super(templateProvider, appIdPrefix, assetRoot);
  }

  @Override
  protected IStreamPipesPipelineElement<?> createSink(KameletTemplate template) {
    if (!AZURE_COSMOS_DB_SINK.equals(template.name())) {
      return super.createSink(template);
    }

    String appId = makeAppId(template.name());
    return new TestAzureCosmosDbApacheCamelKameletSink(
        appId,
        template.name(),
        getTemplateProvider(),
        new KameletSinkAssetResolver(appId, template.name(), getAssetRoot(), getTemplateProvider())
    );
  }
}
