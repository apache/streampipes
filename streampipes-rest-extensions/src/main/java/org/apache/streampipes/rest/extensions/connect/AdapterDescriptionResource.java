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

import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@Path("/api/v1/worker/adapters")
public class AdapterDescriptionResource extends AbstractSharedRestInterface {

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getAdapterDescription(@PathParam("id") String id) {
    var adapterDescriptionOpt = DeclarersSingleton.getInstance().getAdapter(id);
    if (adapterDescriptionOpt.isPresent()) {
      try {
        var adapterDescription = adapterDescriptionOpt.get().declareConfig().getAdapterDescription();
        var localizedDescription = applyLocales(adapterDescription);
        return ok(localizedDescription);
      } catch (IOException e) {
        return serverError(e);
      }
    } else {
      return notFound();
    }
  }

  private AdapterDescription applyLocales(AdapterDescription adapterDescription) throws IOException {
    if (adapterDescription.isIncludesLocales()) {
      return new LabelGenerator<>(adapterDescription).generateLabels();
    } else {
      return adapterDescription;
    }
  }
}
