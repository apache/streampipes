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

package org.streampipes.rest.impl;

import com.google.gson.JsonObject;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.manager.setup.Installer;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.client.messages.SetupStatusMessage;
import org.streampipes.model.client.setup.InitialSettings;
import org.streampipes.rest.api.ISetup;
import org.streampipes.rest.notifications.NotificationListener;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/setup")
public class Setup extends AbstractRestInterface implements ISetup {

  @GET
  @Path("/configured")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response isConfigured() {
    JsonObject obj = new JsonObject();
    if (BackendConfig.INSTANCE.isConfigured()) {
      obj.addProperty("configured", true);
      return ok(obj.toString());
    } else {
      obj.addProperty("configured", false);
      return ok(obj.toString());
    }
  }

  @POST
  @Path("/install/{currentInstallationStep}")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response configure(InitialSettings settings, @PathParam("currentInstallationStep") Integer currentInstallationStep) {
    SetupStatusMessage message = new Installer(settings).install(currentInstallationStep);
    if (currentInstallationStep == (message.getInstallationStepCount() - 1)) {
      new NotificationListener().contextInitialized(null);
    }
    return ok(message);
  }

  @PUT
  @Path("/configuration")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response updateConfiguration(InitialSettings settings) {
    try {
      // TODO implement update consul configs
//			ConfigurationManager
//                    .storeWebappConfigurationToProperties(
//                            new File(ConfigurationManager.getStreamPipesConfigFullPath()),
//                           new File(ConfigurationManager.getStreamPipesConfigFileLocation()),
//                            settings);
      return ok(Notifications.success("Configuration updated"));
    } catch (Exception e) {
      e.printStackTrace();
      return ok(Notifications.error("Error"));
    }
  }

  @GET
  @Path("/configuration")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  @Deprecated
  // NOT sure if we need this method
  public Response getConfiguration() {
//        InitialSettings is = new InitialSettings();
//        is.setCouchDBHost(CouchDbConfig.INSTANCE.getHost());
//        is.setSesameUrl(SesameConfig.INSTANCE.getUri());

//         TODO return here the initial configurations
    return ok(true);
  }

}
