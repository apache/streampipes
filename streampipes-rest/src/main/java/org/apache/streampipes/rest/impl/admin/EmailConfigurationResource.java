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
import org.apache.streampipes.model.configuration.EmailTemplateConfig;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import java.io.IOException;

import org.simplejavamail.MailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/admin/mail-config")
public class EmailConfigurationResource extends AbstractAuthGuardedRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<EmailConfig> getMailConfiguration() {
    return ok(getSpCoreConfigurationStorage().get().getEmailConfig());
  }

  @GetMapping(path = "templates", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<EmailTemplateConfig> getMailTemplates() {
    return ok(getSpCoreConfigurationStorage().get().getEmailTemplateConfig());
  }

  @PutMapping(path = "templates", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> updateMailTemplate(@RequestBody EmailTemplateConfig templateConfig) {
    var config = getSpCoreConfigurationStorage().get();
    config.setEmailTemplateConfig(templateConfig);
    getSpCoreConfigurationStorage().updateElement(config);
    return ok();
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> updateMailConfiguration(@RequestBody EmailConfig config) {
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

  @PostMapping(path = "/test", consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> sendTestMail(@RequestBody EmailConfig config) {
    try {
      new MailTester().sendTestMail(config);
      return ok();
    } catch (MailException | IllegalArgumentException | IOException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
  }
}
