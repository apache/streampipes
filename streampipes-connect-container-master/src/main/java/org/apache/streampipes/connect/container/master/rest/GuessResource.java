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

package org.apache.streampipes.connect.container.master.rest;

import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.exception.WorkerAdapterException;
import org.apache.streampipes.connect.container.master.management.GuessManagement;
import org.apache.streampipes.connect.rest.AbstractContainerResource;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/api/v1/{username}/master/guess")
public class GuessResource extends AbstractContainerResource {

  private static final Logger logger = LoggerFactory.getLogger(GuessResource.class);

  private GuessManagement guessManagement;

  public GuessResource() {
    this.guessManagement = new GuessManagement();
  }

  public GuessResource(GuessManagement guessManagement) {
    this.guessManagement = guessManagement;
  }

  @POST
  @JacksonSerialized
  @Path("/schema")
  @Produces(MediaType.APPLICATION_JSON)
  public Response guessSchema(AdapterDescription adapterDescription, @PathParam("username") String userName) {

      try {
          GuessSchema result = guessManagement.guessSchema(adapterDescription);

          return ok(result);
      } catch (ParseException e) {
          logger.error("Error while parsing events: ", e);
          return error(Notifications.error(e.getMessage()));
      } catch (WorkerAdapterException e) {
          return error(e.getContent());
      } catch (Exception e) {
          logger.error("Error while guess schema for AdapterDescription: ", e);
          return error(Notifications.error(e.getMessage()));
      }

  }

  public void setGuessManagement(GuessManagement guessManagement) {
    this.guessManagement = guessManagement;
  }

}

