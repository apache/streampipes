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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.management.connect.AdapterWorkerManagement;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.init.RunningAdapterInstances;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/v1/worker")
public class AdapterWorkerResource extends AbstractSharedRestInterface {

  private static final Logger logger = LoggerFactory.getLogger(AdapterWorkerResource.class);

  private AdapterWorkerManagement adapterManagement;

  public AdapterWorkerResource() {
    adapterManagement = new AdapterWorkerManagement(
        RunningAdapterInstances.INSTANCE,
        DeclarersSingleton.getInstance()
    );
  }

  public AdapterWorkerResource(AdapterWorkerManagement adapterManagement) {
    this.adapterManagement = adapterManagement;
  }

  @GET
  @JacksonSerialized
  @Path("/running")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getRunningAdapterInstances() {
    return ok(adapterManagement.getAllRunningAdapterInstances());
  }


  @POST
  @JacksonSerialized
  @Path("/stream/invoke")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response invokeAdapter(AdapterDescription adapterStreamDescription) {

    try {
      adapterManagement.invokeAdapter(adapterStreamDescription);
      String responseMessage = "Stream adapter with id " + adapterStreamDescription.getUri() + " successfully started";
      logger.info(responseMessage);
      return ok(Notifications.success(responseMessage));
    } catch (AdapterException e) {
      logger.error("Error while starting adapter with id " + adapterStreamDescription.getUri(), e);
      return serverError(SpLogMessage.from(e));
    }
  }

  @POST
  @JacksonSerialized
  @Path("/stream/stop")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response stopAdapter(AdapterDescription adapterStreamDescription) {

    String responseMessage;
    try {
      if (adapterStreamDescription.isRunning()) {
        adapterManagement.stopAdapter(adapterStreamDescription);
        responseMessage = "Stream adapter with id " + adapterStreamDescription.getElementId() + " successfully stopped";
      } else {
        responseMessage =
            "Stream adapter with id " + adapterStreamDescription.getElementId() + " seems not to be running";
      }
      logger.info(responseMessage);
      return ok(Notifications.success(responseMessage));
    } catch (AdapterException e) {
      logger.error("Error while stopping adapter with id " + adapterStreamDescription.getElementId(), e);
      return serverError(SpLogMessage.from(e));
    }
  }

}
