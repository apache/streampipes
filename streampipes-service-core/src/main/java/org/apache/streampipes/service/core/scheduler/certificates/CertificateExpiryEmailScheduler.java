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

package org.apache.streampipes.service.core.scheduler.certificates;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.mail.MailSender;
import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.mail.SpEmail;
import org.apache.streampipes.model.opcua.Certificate;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class CertificateExpiryEmailScheduler implements SchedulingConfigurer {

  private static final Logger LOG = LoggerFactory.getLogger(CertificateExpiryEmailScheduler.class);

  private static final String SUBJECT = "Upcoming certificate expirations — action required";

  private final ISpCoreConfigurationStorage coreConfigurationStorage;
  private final IUserStorage userStorage;

  public CertificateExpiryEmailScheduler(ISpCoreConfigurationStorage coreConfigurationStorage,
                                         IUserStorage userStorage) {
    this.coreConfigurationStorage = coreConfigurationStorage;
    this.userStorage = userStorage;
  }

  public void checkForExpiringCertificates() {

    var certificateExpiryEmailDays = Environments.getEnvironment().getCertificateExpiryEmailDays().getValueOrDefault();
    if (certificateExpiryEmailDays != null) {
      executeIfEnvVariableIsConfigured(certificateExpiryEmailDays);
    } else {
      LOG.debug("No certificate expiry email notification configured.");
    }
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    var env = Environments.getEnvironment();
    taskRegistrar.addTriggerTask(

        this::checkForExpiringCertificates,
        triggerContext -> new CronTrigger(env.getCertificateExpiryCron()
                                             .getValueOrDefault())
            .nextExecution(triggerContext)
    );
  }

  private void executeIfEnvVariableIsConfigured(String certificateExpiryEmailDays) {
    LOG.info("Certificate expiration email notification CRON Job triggered.");
    var expirePeriodsInDays = parseCommaSeparatedIntegers(certificateExpiryEmailDays);

    var expiringCertificates = getExpiringCertificates(expirePeriodsInDays);

    if (checkIfEmailShouldBeSent(expiringCertificates)) {
      var adminEmails = getEmailAddressesOfAdmins();

      var message = new CertificateExpiryEmailComposer().composeMessage(expiringCertificates);
      sendEmail(adminEmails, message);
      LOG.info("Certificate expiration email notification email sent to all admins.");
    }

    LOG.info("Certificate expiration email notification CRON Job finished.");
  }

  private boolean checkIfEmailShouldBeSent(Map<Integer, List<Certificate>> expiringCertificates) {
    return expiringCertificates.values()
                               .stream()
                               .anyMatch(list -> !list.isEmpty());
  }

  private List<String> getEmailAddressesOfAdmins() {
    return userStorage
        .getAllUserAccounts()
        .stream()
        .filter(u -> u.getRoles().contains(DefaultRole.ROLE_ADMIN.name()))
        .map(Principal::getUsername)
        .collect(Collectors.toList());

  }

  /**
   * Returns a map with one list entry of certificates per requested period (days).
   */
  private Map<Integer, List<Certificate>> getExpiringCertificates(List<Integer> expirePeriodsInDays) {
    return new ExpiringCertificateFinder()
        .findCertificates(expirePeriodsInDays);
  }

  private void sendEmail(List<String> recipients, String message) {
    var email = SpEmail.from(recipients, SUBJECT, message);
    try {
      new MailSender(coreConfigurationStorage.get()).sendEmail(email);
    } catch (IOException e) {
      LOG.error("Failed to send certificate expiry email to {}", recipients, e);
    }

  }

  private List<Integer> parseCommaSeparatedIntegers(String csv) {
    if (csv == null || csv.isBlank()) {
      return List.of();
    }

    return Arrays.stream(csv.split(","))
                 .map(String::trim)
                 .filter(s -> !s.isEmpty())
                 .flatMap(s -> {
                   try {
                     return Stream.of(Integer.parseInt(s));
                   } catch (NumberFormatException e) {
                     LOG.warn("Invalid integer in env variable SP_CERTIFICATE_EXPIRY_EMAIL_DAYS: '{}'", s);
                     return Stream.empty();
                   }
                 })
                 .toList();
  }
}
