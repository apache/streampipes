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

import org.apache.streampipes.model.client.user.RegistrationData;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Path("/v2/restore-password")
public class RestorePasswordResource extends AbstractRestResource {

  @GET
  @Path("{recoveryCode}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response checkTokenValidity(@PathParam("recoveryCode") String recoveryCode) {
    try {
      getSpResourceManager().manageUsers().checkPasswordRecoveryCode(recoveryCode);
      return ok();
    } catch (IllegalArgumentException e) {
      return badRequest();
    }
  }

  @POST
  @Path("{recoveryCode}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response changePassword(@PathParam("recoveryCode") String recoveryCode,
                                 RegistrationData registrationData) {
    try {
      getSpResourceManager().manageUsers().changePassword(recoveryCode, registrationData);
      return ok();
    } catch (IllegalArgumentException e) {
      return badRequest();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return fail();
    }
  }

}
