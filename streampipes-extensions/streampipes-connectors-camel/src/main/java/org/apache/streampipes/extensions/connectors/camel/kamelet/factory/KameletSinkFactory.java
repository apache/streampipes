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

package org.apache.streampipes.extensions.connectors.camel.kamelet.factory;

import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.connectors.camel.kamelet.assets.KameletSinkAssetResolver;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;
import org.apache.streampipes.extensions.connectors.camel.kamelet.provider.KameletTemplateProvider;
import org.apache.streampipes.extensions.connectors.camel.sink.GeneratedApacheCamelKameletSink;

import java.util.List;

public class KameletSinkFactory {

  private final KameletTemplateProvider templateProvider;
  private final String appIdPrefix;
  private final String assetRoot;

  public KameletSinkFactory(KameletTemplateProvider templateProvider,
                            String appIdPrefix,
                            String assetRoot) {
    this.templateProvider = templateProvider;
    this.appIdPrefix = appIdPrefix;
    this.assetRoot = assetRoot;
  }

  public List<IStreamPipesPipelineElement<?>> createSinks() {
    return templateProvider.getTemplates()
        .stream()
        .map(this::createSink)
        .toList();
  }

  protected IStreamPipesPipelineElement<?> createSink(KameletTemplate template) {
    String appId = makeAppId(template.name());
    return new GeneratedApacheCamelKameletSink(
        appId,
        template.name(),
        templateProvider,
        new KameletSinkAssetResolver(appId, template.name(), assetRoot, templateProvider)
    );
  }

  protected String makeAppId(String templateName) {
    return appIdPrefix + "." + templateName;
  }

  protected KameletTemplateProvider getTemplateProvider() {
    return templateProvider;
  }

  protected String getAssetRoot() {
    return assetRoot;
  }
}
