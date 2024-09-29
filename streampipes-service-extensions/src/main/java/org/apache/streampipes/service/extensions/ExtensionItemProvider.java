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
package org.apache.streampipes.service.extensions;

import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.api.assets.DefaultAssetResolver;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionItemProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionItemProvider.class);

  /**
   * Gather all extensions provided by this service.
   *
   * @return set of extension item descriptions
   */
  public Set<ExtensionItemDescription> getAllItemDescriptions() {
    return Stream.concat(getPipelineElements(), getAdapterDescriptions())
            .map(entity -> entity.toExtensionDescription(false, true, true)).collect(Collectors.toSet());
  }

  private Stream<NamedStreamPipesEntity> getPipelineElements() {
    return DeclarersSingleton.getInstance().getDeclarers().values().stream()
            .map(declarer -> declarer.declareConfig().getDescription())
            .peek(entity -> applyLocales(entity, new DefaultAssetResolver(entity.getAppId())))
            .map(e -> (NamedStreamPipesEntity) e);
  }

  private Stream<NamedStreamPipesEntity> getAdapterDescriptions() {
    return DeclarersSingleton.getInstance().getAdapters().stream().map(StreamPipesAdapter::declareConfig)
            .peek(config -> applyLocales(config.getAdapterDescription(), config.getAssetResolver()))
            .map(IAdapterConfiguration::getAdapterDescription);
  }

  private void applyLocales(NamedStreamPipesEntity entity, AssetResolver assetResolver) {
    try {
      if (entity.isIncludesLocales()) {
        var labelGenerator = new LabelGenerator<>(entity, true, assetResolver);
        entity.setName(labelGenerator.getElementTitle());
        entity.setDescription(labelGenerator.getElementDescription());
      }
    } catch (IOException e) {
      LOG.warn("Could not read locales file for extension {}", entity.getAppId());
    }
  }
}
