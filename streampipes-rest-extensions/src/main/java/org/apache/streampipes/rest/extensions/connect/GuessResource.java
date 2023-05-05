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
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.management.connect.GuessManagement;
import org.apache.streampipes.extensions.management.context.AdapterContextGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/v1/worker/guess")
public class GuessResource extends AbstractSharedRestInterface {

  private static final Logger logger = LoggerFactory.getLogger(GuessResource.class);

  private final GuessManagement guessManagement;

  public GuessResource() {
    this.guessManagement = new GuessManagement(new AdapterContextGenerator().makeGuessSchemaContext());
  }

  public GuessResource(GuessManagement guessManagement) {
    this.guessManagement = guessManagement;
  }

  @POST
  @JacksonSerialized
  @Path("/schema")
  @Produces(MediaType.APPLICATION_JSON)
  public Response guessSchema(AdapterDescription adapterDescription) {

    try {
      GuessSchema result = guessManagement.guessSchema(adapterDescription);

      return ok(result);
    } catch (ParseException e) {
      logger.error("Error while parsing events: ", e);
      return serverError(e);
    } catch (AdapterException e) {
      logger.error("Error while guessing schema for AdapterDescription: {}, {}", adapterDescription.getElementId(),
          e.getMessage());
      return serverError(e);
    }

  }
}

