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
package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.mail.MailTester;
import org.apache.streampipes.model.configuration.EmailConfig;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import org.simplejavamail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@Path("/v2/admin/mail-config")
@Component
public class EmailConfigurationResource extends AbstractAuthGuardedRestResource {

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response getMailConfiguration() {
    return ok(getSpCoreConfigurationStorage().get().getEmailConfig());
  }

  @PUT
  @JacksonSerialized
  @Consumes(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response updateMailConfiguration(EmailConfig config) {
    config.setEmailConfigured(true);
    if (!config.isProxyPassEncrypted() && config.isUsesProxyAuthentication()) {
      config.setProxyPassword(SecretEncryptionManager.encrypt(config.getProxyPassword()));
      config.setProxyPassEncrypted(true);
    }

    if (!config.isSmtpPassEncrypted() && config.isUsesAuthentication()) {
      config.setSmtpPassword(SecretEncryptionManager.encrypt(config.getSmtpPassword()));
      config.setSmtpPassEncrypted(true);
    }
    var storage = getSpCoreConfigurationStorage();
    var cfg = storage.get();
    cfg.setEmailConfig(config);
    storage.updateElement(cfg);

    return ok();
  }

  @POST
  @Path("/test")
  @Consumes(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response sendTestMail(EmailConfig config) {
    try {
      new MailTester().sendTestMail(config);
      return ok();
    } catch (MailException | IllegalArgumentException | IOException e) {
      return badRequest(e);
    }
  }
}
