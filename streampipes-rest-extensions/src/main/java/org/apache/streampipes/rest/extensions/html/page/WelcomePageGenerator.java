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

package org.apache.streampipes.rest.extensions.html.page;

import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.api.assets.DefaultAssetResolver;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataStream;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.rest.extensions.html.model.Description;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class WelcomePageGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(WelcomePageGenerator.class);

  protected List<Description> descriptions;
  protected Collection<IStreamPipesPipelineElement<?>> pipelineElements;
  protected Collection<StreamPipesAdapter> adapters;
  protected String baseUri;

  public WelcomePageGenerator(String baseUri,
                              Collection<IStreamPipesPipelineElement<?>> pipelineElements,
                              Collection<StreamPipesAdapter> adapters) {
    this.pipelineElements = pipelineElements;
    this.adapters = adapters;
    this.baseUri = baseUri;
    this.descriptions = new ArrayList<>();
  }

  public List<Description> buildUris() {
    List<Description> descriptions = new ArrayList<>();

    pipelineElements.forEach(pipelineElement -> descriptions.add(getPipelineElementDescription(pipelineElement)));
    adapters.forEach(adapter -> descriptions.add(getAdapterDescription(adapter)));

    return descriptions;
  }

  private Description getAdapterDescription(StreamPipesAdapter adapter) {
    var entity = adapter.declareConfig().getAdapterDescription();
    return getDescription(
        entity,
        adapter.declareConfig().getAssetResolver(),
        "adapter",
        "api/v1/worker/adapters/"
    );
  }

  private Description getPipelineElementDescription(IStreamPipesPipelineElement<?> declarer) {
    var entity = declarer.declareConfig().getDescription();
    return getDescription(
        entity,
        new DefaultAssetResolver(entity.getAppId()), getType(declarer), getPathPrefix(declarer)
    );
  }

  private Description getDescription(NamedStreamPipesEntity entity,
                                     AssetResolver assetResolver,
                                     String type,
                                     String pathPrefix) {
    Description desc = new Description();
    // TODO remove after full internationalization support has been implemented
    updateLabel(entity, desc, assetResolver);
    desc.setType(type);
    desc.setElementId(entity.getElementId());
    desc.setAppId(entity.getAppId());
    desc.setEditable(!(entity.isInternallyManaged()));
    desc.setIncludesDocs(entity.isIncludesAssets()
        && entity.getIncludedAssets().contains(ExtensionAssetType.DOCUMENTATION));
    desc.setIncludesIcon(entity.isIncludesAssets()
        && entity.getIncludedAssets().contains(ExtensionAssetType.ICON));
    String uri = baseUri + pathPrefix;
    desc.setDescriptionUrl(uri + entity.getAppId());
    return desc;
  }

  private String getPathPrefix(IStreamPipesPipelineElement<?> pipelineElement) {
    if (pipelineElement instanceof IStreamPipesDataSink) {
      return "sec/";
    } else if (pipelineElement instanceof IStreamPipesDataProcessor) {
      return "sepa/";
    } else if (pipelineElement instanceof IStreamPipesDataStream) {
      return "stream/";
    } else {
      return "";
    }
  }

  private String getType(IStreamPipesPipelineElement<?> pipelineElement) {
    var elementDescription = pipelineElement.declareConfig().getDescription();
    if (elementDescription instanceof DataSinkDescription) {
      return "action";
    } else if (elementDescription instanceof SpDataStream) {
      return "stream";
    } else if (elementDescription instanceof DataProcessorDescription) {
      return "sepa";
    } else if (elementDescription instanceof AdapterDescription) {
      return "adapter";
    } else {
      throw new RuntimeException("Could not get type for element " + elementDescription.getClass());
    }
  }

  private void updateLabel(NamedStreamPipesEntity entity,
                           Description desc,
                           AssetResolver assetResolver) {
    if (!entity.isIncludesLocales()) {
      desc.setName(entity.getName());
      desc.setDescription(entity.getDescription());
    } else {
      LabelGenerator lg = new LabelGenerator(entity, true, assetResolver);
      try {
        desc.setName(lg.getElementTitle());
        desc.setDescription(lg.getElementDescription());
      } catch (IOException e) {
        LOG.error("Error while updating description of %s".formatted(entity.getAppId()), e);
      }
    }
  }
}
