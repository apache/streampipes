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
import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.mail.SpEmail;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/mail")
public class EmailResource extends AbstractAuthGuardedRestResource {

  private final ISpCoreConfigurationStorage configurationStorage;

  public EmailResource(ISpCoreConfigurationStorage configurationStorage) {
    this.configurationStorage = configurationStorage;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<?> sendEmail(@RequestBody SpEmail email) {
    var configuration = configurationStorage.get();
    if (configuration.getEmailConfig().isEmailConfigured()) {
      try {
        new MailSender(configuration).sendEmail(email);
        return ok();
      } catch (Exception e) {
        return badRequest(e);
      }
    } else {
      return serverError(
          "Could not send email - no valid mail configuration provided in StreamPipes (go to settings -> mail)");
    }
  }

  public boolean hasWriteAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_WRITE_PIPELINE_VALUE);
  }
}
