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

package org.apache.streampipes.rest.extensions.connect;

import org.apache.streampipes.extensions.management.assets.AssetZipGenerator;
import org.apache.streampipes.extensions.management.connect.ConnectWorkerDescriptionProvider;
import org.apache.streampipes.extensions.management.util.AssetsUtil;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Path("/api/v1/worker/adapters")
public class AdapterAssetResource extends AbstractSharedRestInterface {

  private ConnectWorkerDescriptionProvider connectWorkerDescriptionProvider;

  public AdapterAssetResource() {
    this.connectWorkerDescriptionProvider = new ConnectWorkerDescriptionProvider();
  }


  @GET
  @Path("/{id}/assets")
  @Produces("application/zip")
  public Response getAssets(@PathParam("id") String id) {
    Optional<AdapterDescription> adapterDescription = this.connectWorkerDescriptionProvider.getAdapterDescription(id);
    if (adapterDescription.isPresent()) {
      try {
        return ok(new AssetZipGenerator(id, adapterDescription.get().getIncludedAssets()).makeZip());
      } catch (IOException e) {
        e.printStackTrace();
        return fail();
      }
    } else {
      return fail();
    }

  }

  @GET
  @Path("/{id}/assets/icon")
  @Produces("image/png")
  public Response getIconAsset(@PathParam("id") String elementId) throws IOException {
    URL iconUrl = Resources.getResource(AssetsUtil.makeIconPath(elementId));
    return ok(Resources.toByteArray(iconUrl));
  }

  @GET
  @Path("/{id}/assets/documentation")
  @Produces(MediaType.TEXT_PLAIN)
  public String getDocumentationAsset(@PathParam("id") String elementId) throws IOException {
    URL documentationUrl = Resources.getResource(AssetsUtil.makeDocumentationPath(elementId));
    return Resources.toString(documentationUrl, Charsets.UTF_8);
  }
}
