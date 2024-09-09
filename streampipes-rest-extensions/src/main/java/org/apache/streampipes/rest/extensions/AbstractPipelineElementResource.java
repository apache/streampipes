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

package org.apache.streampipes.rest.extensions;

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.management.assets.AssetZipGenerator;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportProtocol;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractPipelineElementResource<
    T extends IStreamPipesPipelineElement<?>>
    extends AbstractExtensionsResource {

  private static final String SLASH = "/";

  private static final Logger LOG = LoggerFactory.getLogger(AbstractPipelineElementResource.class);

  @GetMapping(path = "{appId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public NamedStreamPipesEntity getDescription(@PathVariable("appId") String appId) {
    return prepareElement(appId);
  }

  @GetMapping(path = "{appId}/assets", produces = "application/zip")
  public ResponseEntity<?> getAssets(@PathVariable("appId") String appId) {
    List<String> includedAssets = getDeclarerById(appId).declareConfig().getDescription().getIncludedAssets();
    try {
      return ok(new AssetZipGenerator(appId, includedAssets).makeZip());
    } catch (IOException e) {
      e.printStackTrace();
      return serverError();
    }
  }

  @GetMapping(path = "{appId}/assets/icon", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> getIconAsset(@PathVariable("appId") String appId) throws IOException {
    try {
      URL iconUrl = Resources.getResource(makeIconPath(appId));
      return ok(Resources.toByteArray(iconUrl));
    } catch (IllegalArgumentException e) {
      LOG.warn("No icon resource found for pipeline element {}", appId);
      return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).build();
    }
  }

  @GetMapping(path = "{id}/assets/documentation", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> getDocumentationAsset(@PathVariable("id") String elementId) throws IOException {
    try {
      URL documentationUrl = Resources.getResource(makeDocumentationPath(elementId));
      return ok(Resources.toString(documentationUrl, Charsets.UTF_8));
    } catch (IllegalArgumentException e) {
      LOG.warn("No documentation resource found for pipeline element {}", elementId);
      return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).build();
    }
  }

  protected NamedStreamPipesEntity prepareElement(String appId) {
    return rewrite(getById(appId));
  }

  protected NamedStreamPipesEntity prepareElement(NamedStreamPipesEntity desc) {
    return rewrite(desc);
  }

  protected T getDeclarerById(String appId) {
    return getElementDeclarers().get(appId);
  }

  protected NamedStreamPipesEntity getById(String appId) {
    IStreamPipesPipelineElement<?> declarer = getElementDeclarers().get(appId);
    return declarer.declareConfig().getDescription();
  }

  protected NamedStreamPipesEntity rewrite(NamedStreamPipesEntity desc) {

    //TODO remove this and find a better solution
    if (desc != null) {
      // TODO remove after full internationalization support has been implemented
      if (desc.isIncludesLocales()) {
        try {
          desc = new LabelGenerator<>(desc).generateLabels();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (desc instanceof ConsumableStreamPipesEntity) {
        Collection<TransportProtocol> supportedProtocols =
            DeclarersSingleton.getInstance().getSupportedProtocols();

        if (!supportedProtocols.isEmpty()) {
          // Overwrite existing grounding from default provided by declarers singleton
          ((ConsumableStreamPipesEntity) desc)
              .setSupportedGrounding(makeGrounding(supportedProtocols));
        }
      }
    }

    return desc;
  }

  private EventGrounding makeGrounding(Collection<TransportProtocol> supportedProtocols) {
    EventGrounding grounding = new EventGrounding();
    grounding.setTransportProtocols(new ArrayList<>(supportedProtocols));

    return grounding;
  }

  private String makeIconPath(String appId) {
    return makePath(appId, GlobalStreamPipesConstants.STD_ICON_NAME);
  }

  private String makeDocumentationPath(String appId) {
    return makePath(appId, GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME);
  }

  private String makePath(String appId, String assetAppendix) {
    return appId + SLASH + assetAppendix;
  }

  protected abstract Map<String, T> getElementDeclarers();
}
