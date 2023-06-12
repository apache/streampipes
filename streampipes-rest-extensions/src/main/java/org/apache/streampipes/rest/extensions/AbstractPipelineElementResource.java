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
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.util.SpMediaType;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

  @GET
  @Path("{appId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public NamedStreamPipesEntity getDescription(@PathParam("appId") String appId) {
    return prepareElement(appId);
  }

  @GET
  @Path("{appId}/assets")
  @Produces(SpMediaType.APPLICATION_ZIP)
  public Response getAssets(@PathParam("appId") String appId) {
    List<String> includedAssets = getDeclarerById(appId).declareConfig().getDescription().getIncludedAssets();
    try {
      return ok(new AssetZipGenerator(appId, includedAssets).makeZip());
    } catch (IOException e) {
      e.printStackTrace();
      return serverError();
    }
  }

  @GET
  @Path("{appId}/assets/icon")
  @Produces("image/png")
  public Response getIconAsset(@PathParam("appId") String appId) throws IOException {
    try {
      URL iconUrl = Resources.getResource(makeIconPath(appId));
      return ok(Resources.toByteArray(iconUrl));
    } catch (IllegalArgumentException e) {
      LOG.warn("No icon resource found for pipeline element {}", appId);
      return Response.status(400).build();
    }
  }

  @GET
  @Path("{id}/assets/documentation")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getDocumentationAsset(@PathParam("id") String elementId) throws IOException {
    try {
      URL documentationUrl = Resources.getResource(makeDocumentationPath(elementId));
      return ok(Resources.toString(documentationUrl, Charsets.UTF_8));
    } catch (IllegalArgumentException e) {
      LOG.warn("No documentation resource found for pipeline element {}", elementId);
      return Response.status(400).build();
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
          desc = new LabelGenerator(desc).generateLabels();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (desc instanceof ConsumableStreamPipesEntity) {
        Collection<TransportProtocol> supportedProtocols =
            DeclarersSingleton.getInstance().getSupportedProtocols();
        Collection<TransportFormat> supportedFormats =
            DeclarersSingleton.getInstance().getSupportedFormats();

        if (supportedProtocols.size() > 0 && supportedFormats.size() > 0) {
          // Overwrite existing grounding from default provided by declarers singleton
          ((ConsumableStreamPipesEntity) desc)
              .setSupportedGrounding(makeGrounding(supportedProtocols, supportedFormats));
        }
      }
    }

    return desc;
  }

  private EventGrounding makeGrounding(Collection<TransportProtocol> supportedProtocols,
                                       Collection<TransportFormat> supportedFormats) {
    EventGrounding grounding = new EventGrounding();
    grounding.setTransportProtocols(new ArrayList<>(supportedProtocols));
    grounding.setTransportFormats(new ArrayList<>(supportedFormats));

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
