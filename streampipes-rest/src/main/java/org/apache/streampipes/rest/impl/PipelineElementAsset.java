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
package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;

@Path("/v2/pe")
public class PipelineElementAsset extends AbstractRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(PipelineElementAsset.class);

  @GET
  @Path("{appId}/assets/icon")
  @Produces("image/png")
  public Response getIconAsset(@PathParam("appId") String appId) {
    try {
      return ok(AssetManager.getAssetIcon(appId));
    } catch (IOException e) {
      return fail();
    }
  }

  @GET
  @Path("{appId}/assets/documentation")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getDocumentationAsset(@PathParam("appId") String appId) {
    try {
      return ok(AssetManager.getAssetDocumentation(appId));
    } catch (IOException e) {
      return fail();
    }
  }

  @GET
  @Path("{appId}/assets/{assetName}")
  @Produces("image/png")
  public Response getAsset(@PathParam("appId") String appId, @PathParam("assetName") String
      assetName) {
    try {
      byte[] asset = AssetManager.getAsset(appId, assetName);
      return ok(asset);
    } catch (IOException e) {
      LOG.error("Could not find asset {}", assetName);
      return fail();
    }
  }
}
