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

import org.apache.streampipes.mail.MailSender;
import org.apache.streampipes.model.mail.SpEmail;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("v2/mail")
public class EmailResource extends AbstractAuthGuardedRestResource {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response sendEmail(SpEmail email) {
    if (getSpCoreConfigurationStorage().get().getEmailConfig().isEmailConfigured()) {
      try {
        new MailSender().sendEmail(email);
        return ok();
      } catch (Exception e) {
        return badRequest(e);
      }
    } else {
      return serverError(
          "Could not send email - no valid mail configuration provided in StreamPipes (go to settings -> mail)");
    }
  }
}
