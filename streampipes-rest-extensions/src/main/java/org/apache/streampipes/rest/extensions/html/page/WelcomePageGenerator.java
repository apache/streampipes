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

import org.apache.streampipes.extensions.api.declarer.DataStreamDeclarer;
import org.apache.streampipes.extensions.api.declarer.Declarer;
import org.apache.streampipes.extensions.api.declarer.InvocableDeclarer;
import org.apache.streampipes.extensions.api.declarer.PipelineTemplateDeclarer;
import org.apache.streampipes.extensions.api.declarer.SemanticEventConsumerDeclarer;
import org.apache.streampipes.extensions.api.declarer.SemanticEventProcessingAgentDeclarer;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.rest.extensions.html.model.Description;
import org.apache.streampipes.sdk.utils.Assets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class WelcomePageGenerator {

  protected List<Description> descriptions;
  protected Collection<Declarer<?>> declarers;
  protected String baseUri;

  public WelcomePageGenerator(String baseUri, Collection<Declarer<?>> declarers) {
    this.declarers = declarers;
    this.baseUri = baseUri;
    this.descriptions = new ArrayList<>();
  }

  public List<Description> getDescriptions() {
    return descriptions;
  }

  public List<Description> buildUris() {
    List<Description> descriptions = new ArrayList<>();

    for (Declarer<?> declarer : declarers) {
      if (declarer instanceof InvocableDeclarer) {
        descriptions.add(getDescription(declarer));
      } else if (declarer instanceof DataStreamDeclarer) {
        descriptions.add(getDescription(declarer));
      } else if (declarer instanceof PipelineTemplateDeclarer) {
        descriptions.add(getDescription(declarer));
      }
    }
    return descriptions;
  }

  private Description getDescription(Declarer<?> declarer) {
    NamedStreamPipesEntity entity = declarer.declareModel();
    Description desc = new Description();
    // TODO remove after full internationalization support has been implemented
    updateLabel(declarer.declareModel(), desc);
    desc.setType(getType(declarer));
    desc.setElementId(entity.getElementId());
    desc.setAppId(entity.getAppId());
    desc.setEditable(!(entity.isInternallyManaged()));
    desc.setIncludesDocs(entity.isIncludesAssets()
        && entity.getIncludedAssets().contains(Assets.DOCUMENTATION));
    desc.setIncludesIcon(entity.isIncludesAssets()
        && entity.getIncludedAssets().contains(Assets.ICON));
    String uri = baseUri;
    if (declarer instanceof SemanticEventConsumerDeclarer) {
      uri += "sec/";
    } else if (declarer instanceof SemanticEventProcessingAgentDeclarer) {
      uri += "sepa/";
    } else if (declarer instanceof DataStreamDeclarer) {
      uri += "stream/";
    } else if (declarer instanceof PipelineTemplateDeclarer) {
      uri += "template/";
    }
    desc.setDescriptionUrl(uri + declarer.declareModel().getAppId());
    //desc.setUri(URI.create(uri + declarer.declareModel().getUri()
    // .replaceFirst("[a-zA-Z]{4}://[a-zA-Z\\.]+:\\d+/", "")));
    return desc;
  }

  private String getType(Declarer<?> declarer) {
    if (declarer.declareModel() instanceof DataSinkDescription) {
      return "action";
    } else if (declarer.declareModel() instanceof SpDataSet) {
      return "set";
    } else if (declarer.declareModel() instanceof SpDataStream) {
      return "stream";
    } else {
      return "sepa";
    }
  }

  private void updateLabel(NamedStreamPipesEntity entity, Description desc) {
    if (!entity.isIncludesLocales()) {
      desc.setName(entity.getName());
      desc.setDescription(entity.getDescription());
    } else {
      LabelGenerator lg = new LabelGenerator(entity);
      try {
        desc.setName(lg.getElementTitle());
        desc.setDescription(lg.getElementDescription());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
