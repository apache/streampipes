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

package org.apache.streampipes.rest.extensions.pe;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataStream;
import org.apache.streampipes.extensions.management.assets.AssetZipGenerator;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.rest.extensions.AbstractPipelineElementResource;
import org.apache.streampipes.svcdiscovery.api.model.SpServicePathPrefix;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.util.Map;

@Path(SpServicePathPrefix.DATA_STREAM)
public class DataStreamPipelineElementResource extends AbstractPipelineElementResource<IStreamPipesDataStream> {

  @Override
  protected Map<String, IStreamPipesDataStream> getElementDeclarers() {
    return DeclarersSingleton.getInstance().getDataStreams();
  }

  @GET
  @Path("{streamId}/assets")
  @Produces("application/zip")
  public jakarta.ws.rs.core.Response getAssets(@PathParam("streamId") String streamId) {
    try {
      return ok(new AssetZipGenerator(streamId,
          getById(streamId)
              .getIncludedAssets()).makeZip());
    } catch (IOException e) {
      e.printStackTrace();
      return jakarta.ws.rs.core.Response.status(500).build();
    }
  }

}
