/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.container.api;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.streampipes.commons.Utils;
import org.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.streampipes.container.assets.AssetZipGenerator;
import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.locales.LabelGenerator;
import org.streampipes.container.transform.Transformer;
import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSourceDescription;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class Element<D extends Declarer> {
  public Element() {
  }

  protected abstract Map<String, D> getElementDeclarers();

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String getDescription(@PathParam("id") String elementId) {
    return getJsonLd(elementId);
  }

  @GET
  @Path("{id}/assets")
  @Produces("application/zip")
  public Response getAssets(@PathParam("id") String elementId) {
    List<String> includedAssets = getDeclarerById(elementId).declareModel().getIncludedAssets();
    try {
      return Response
              .ok()
              .entity(new AssetZipGenerator(elementId, includedAssets).makeZip())
              .build();
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(500).build();
    }
  }

  @GET
  @Path("{id}/assets/icon")
  @Produces("image/png")
  public Response getIconAsset(@PathParam("id") String elementId) throws IOException {
    URL iconUrl = Resources.getResource(makeIconPath(elementId));
    return Response
            .ok()
            .entity(Resources.toByteArray(iconUrl))
            .build();
  }

  @GET
  @Path("{id}/assets/documentation")
  @Produces(MediaType.TEXT_PLAIN)
  public String getDocumentationAsset(@PathParam("id") String elementId) throws IOException {
    URL documentationUrl = Resources.getResource(makeDocumentationPath(elementId));
    return Resources.toString(documentationUrl, Charsets.UTF_8);
  }

  protected String getJsonLd(String id) {
    NamedStreamPipesEntity elem = rewrite(getById(id));
    return toJsonLd(elem);
  }

  protected String getJsonLd(NamedStreamPipesEntity desc, String appendix) {
    return toJsonLd(rewrite(desc, appendix));
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
        type = "sepa/";
      } else if (desc instanceof DataSourceDescription) {
        type = "sep/";
      } else if (desc instanceof DataSinkDescription) {
        type = "sec/";
      } else if (desc instanceof SpDataStream) {
        type = "sep/" + appendix + "/";
      }

      String originalId = desc.getUri();
      String uri = DeclarersSingleton.getInstance().getBaseUri() + type + desc.getUri();
      desc.setUri(uri);
      desc.setRdfId(new SupportsRdfId.URIKey(URI.create(uri)));

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
                  + "/"
                  + stream.getUri();
          stream.setUri(baseUri);
          stream.setRdfId(new SupportsRdfId.URIKey(URI.create(baseUri)));
          // TODO remove after full internationalization support has been implemented
          if (stream.isIncludesLocales()) {
            try {
              LabelGenerator lg = new LabelGenerator(desc);
              stream.setName(lg.getElementTitle());
              stream.setDescription(lg.getElementDescription());
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }

    return desc;
  }

  protected String toJsonLd(NamedStreamPipesEntity namedElement) {
    if (namedElement != null) {
      Graph rdfGraph;
      try {
        rdfGraph = Transformer.toJsonLd(namedElement);
        return Utils.asString(rdfGraph);
      } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | InvalidRdfException | RDFHandlerException e) {
        e.printStackTrace();
      }
    }
    return "{}";
  }

  private String makeIconPath(String elementId) {
    return makePath(elementId, GlobalStreamPipesConstants.STD_ICON_NAME);
  }

  private String makeDocumentationPath(String elementId) {
    return makePath(elementId, GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME);
  }

  private String makePath(String elementId, String assetAppendix) {
    return elementId + "/" + assetAppendix;
  }
}
