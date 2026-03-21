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

package org.apache.streampipes.extensions.management.pe;

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.management.assets.AssetZipGenerator;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class AbstractPipelineElementManagement<T extends IStreamPipesPipelineElement<?>> {

  public NamedStreamPipesEntity getDescription(String appId) {
    return prepareElement(appId);
  }

  public byte[] getAssets(String appId) throws IOException {
    var config = getDeclarerById(appId).declareConfig();
    return new AssetZipGenerator(config.getDescription().getIncludedAssets(), config.getAssetResolver()).makeZip();
  }

  public byte[] getIconAsset(String appId) throws IOException {
    return getDeclarerById(appId)
        .declareConfig()
        .getAssetResolver()
        .getAsset(GlobalStreamPipesConstants.STD_ICON_NAME);
  }

  public String getDocumentationAsset(String appId) throws IOException {
    return new String(
        getDeclarerById(appId).declareConfig().getAssetResolver().getAsset(
            GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME),
        StandardCharsets.UTF_8
    );
  }

  protected NamedStreamPipesEntity prepareElement(String appId) {
    var config = getDeclarerById(appId).declareConfig();
    return rewrite(config.getDescription(), config.getAssetResolver());
  }

  protected T getDeclarerById(String appId) {
    return getElementDeclarers().get(appId);
  }

  protected NamedStreamPipesEntity rewrite(NamedStreamPipesEntity description,
                                           AssetResolver assetResolver) {
    if (description != null && description.isIncludesLocales()) {
      try {
        return assetResolver == null
            ? new LabelGenerator<>(description).generateLabels()
            : new LabelGenerator<>(description, true, assetResolver).generateLabels();
      } catch (IOException e) {
        return description;
      }
    }

    return description;
  }

  protected abstract Map<String, T> getElementDeclarers();
}

