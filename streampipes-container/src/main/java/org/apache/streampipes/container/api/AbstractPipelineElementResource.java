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

package org.apache.streampipes.container.api;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.container.assets.AssetZipGenerator;
import org.apache.streampipes.container.declarer.DataStreamDeclarer;
import org.apache.streampipes.container.declarer.Declarer;
import org.apache.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.apache.streampipes.container.init.DeclarersSingleton;
import org.apache.streampipes.container.locales.LabelGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.rest.shared.util.SpMediaType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractPipelineElementResource<D extends Declarer<?>> {

  protected final String DATA_PROCESSOR_PREFIX = "sepa";
  protected final String DATA_SINK_PREFIX = "sec";
  protected final String DATA_SOURCE_PREFIX = "sep";
  private static final String SLASH = "/";

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public NamedStreamPipesEntity getDescription(@PathParam("id") String elementId) {
    return prepareElement(elementId);
  }

  @GET
  @Path("{id}/assets")
  @Produces(SpMediaType.APPLICATION_ZIP)
  public Response getAssets(@PathParam("id") String elementId) {
    List<String> includedAssets = getDeclarerById(elementId).declareModel().getIncludedAssets();
    try {
      return ok(new AssetZipGenerator(elementId, includedAssets).makeZip());
    } catch (IOException e) {
      e.printStackTrace();
      return serverError();
    }
  }

  @GET
  @Path("{id}/assets/icon")
  @Produces("image/png")
  public Response getIconAsset(@PathParam("id") String elementId) throws IOException {
    URL iconUrl = Resources.getResource(makeIconPath(elementId));
    return ok(Resources.toByteArray(iconUrl));
  }

  @GET
  @Path("{id}/assets/documentation")
  @Produces(MediaType.TEXT_PLAIN)
  public String getDocumentationAsset(@PathParam("id") String elementId) throws IOException {
    URL documentationUrl = Resources.getResource(makeDocumentationPath(elementId));
    return Resources.toString(documentationUrl, Charsets.UTF_8);
  }

  protected NamedStreamPipesEntity prepareElement(String id) {
    return rewrite(getById(id));
  }

  protected NamedStreamPipesEntity prepareElement(NamedStreamPipesEntity desc, String appendix) {
    return rewrite(desc, appendix);
  }

  protected D getDeclarerById(String id) {
    return getElementDeclarers().get(id);
  }

  protected NamedStreamPipesEntity getById(String id) {
    NamedStreamPipesEntity desc = null;
    Declarer declarer = getElementDeclarers().get(id);
    //TODO find a better solution to add the event streams to the SepDescription
    if (declarer instanceof SemanticEventProducerDeclarer) {
      DataSourceDescription secDesc = ((SemanticEventProducerDeclarer) declarer).declareModel();
      List<DataStreamDeclarer> eventStreamDeclarers = ((SemanticEventProducerDeclarer) declarer).getEventStreams();
      for (DataStreamDeclarer esd : eventStreamDeclarers) {
        secDesc.addEventStream(esd.declareModel(secDesc));
      }

      desc = secDesc;
    } else {
      desc = declarer.declareModel();
    }

    return desc;
  }

  protected NamedStreamPipesEntity rewrite(NamedStreamPipesEntity desc) {
    return rewrite(desc, "");
  }

  protected NamedStreamPipesEntity rewrite(NamedStreamPipesEntity desc, String appendix) {

    //TODO remove this and find a better solution
    if (desc != null) {
      String type = "";

      if (desc instanceof DataProcessorDescription) {
        type = DATA_PROCESSOR_PREFIX + SLASH;
      } else if (desc instanceof DataSourceDescription) {
        type = DATA_SOURCE_PREFIX + SLASH;
      } else if (desc instanceof DataSinkDescription) {
        type = DATA_SINK_PREFIX + SLASH;
      } else if (desc instanceof SpDataStream) {
        type = DATA_SOURCE_PREFIX + SLASH + appendix + SLASH;
      }

      String originalId = desc.getElementId();
      String uri = DeclarersSingleton.getInstance().getBaseUri() + type + desc.getElementId();
      desc.setElementId(uri);

      // TODO remove after full internationalization support has been implemented
      if (desc.isIncludesLocales()) {
        try {
          desc = new LabelGenerator(desc).generateLabels();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (desc instanceof DataSourceDescription) {
        for (SpDataStream stream : ((DataSourceDescription) desc).getSpDataStreams()) {
          String baseUri = DeclarersSingleton.getInstance().getBaseUri()
                  + type
                  + originalId
                  + SLASH
                  + stream.getElementId();
          stream.setElementId(baseUri);
          stream.setElementId(baseUri);
          // TODO remove after full internationalization support has been implemented
          if (stream.isIncludesLocales()) {
            try {
              LabelGenerator lg = new LabelGenerator(stream);
              stream.setName(lg.getElementTitle());
              stream.setDescription(lg.getElementDescription());
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      } else if (desc instanceof ConsumableStreamPipesEntity) {
        Collection<TransportProtocol> supportedProtocols =
                DeclarersSingleton.getInstance().getSupportedProtocols();
        Collection<TransportFormat> supportedFormats =
                DeclarersSingleton.getInstance().getSupportedFormats();

        ((ConsumableStreamPipesEntity) desc).setElementEndpointHostname(DeclarersSingleton.getInstance().getHostname());
        ((ConsumableStreamPipesEntity) desc).setElementEndpointPort(DeclarersSingleton.getInstance().getPort());
        ((ConsumableStreamPipesEntity) desc).setElementEndpointServiceName(DeclarersSingleton.getInstance().getServiceName());

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

  private String makeIconPath(String elementId) {
    return makePath(elementId, GlobalStreamPipesConstants.STD_ICON_NAME);
  }

  private String makeDocumentationPath(String elementId) {
    return makePath(elementId, GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME);
  }

  private String makePath(String elementId, String assetAppendix) {
    return elementId + SLASH + assetAppendix;
  }

  protected <T> Response ok(T entity) {
    return Response
            .ok()
            .entity(entity)
            .build();
  }

  protected Response clientError() {
    return Response
            .status(400)
            .build();
  }

  protected Response serverError() {
    return Response
            .status(500)
            .build();
  }

  protected abstract Map<String, D> getElementDeclarers();
}
