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
package org.apache.streampipes.mail.config;

import org.apache.streampipes.model.configuration.EmailConfig;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.internal.MailerRegularBuilderImpl;

import java.util.Objects;

public class MailConfigurationBuilder {

  private static final String STARTTLS_ENABLE_PROPERTY = "mail.smtp.starttls.enable";
  private static final String STARTTLS_REQUIRED_PROPERTY = "mail.smtp.starttls.required";

  public Mailer buildMailerFromConfig(EmailConfig config) {
    MailerRegularBuilderImpl builder = MailerBuilder
        .withTransportStrategy(toTransportStrategy(config.getTransportStrategy()));

    if (config.isUsesAuthentication()) {
      builder.withSMTPServer(
          config.getSmtpServerHost(),
          config.getSmtpServerPort(),
          config.getSmtpUsername(),
          config.getSmtpPassword()
      );
    } else {
      builder.withSMTPServer(config.getSmtpServerHost(), config.getSmtpServerPort());
    }

    if (config.isUsesProxy()) {
      if (config.isUsesProxyAuthentication()) {
        builder.withProxy(
            config.getProxyHost(),
            config.getProxyPort(),
            config.getProxyUser(),
            config.getProxyPassword()
        );
      } else {
        builder.withProxy(config.getProxyHost(), config.getProxyPort());
      }
    }

    disableStartTlsForPlainSmtp(config, builder);

    return builder.buildMailer();

  }

  private void disableStartTlsForPlainSmtp(EmailConfig config,
                                           MailerRegularBuilderImpl builder) {
    if (config.getTransportStrategy() == org.apache.streampipes.model.configuration.TransportStrategy.SMTP) {
      builder.withProperty(STARTTLS_ENABLE_PROPERTY, "false");
      builder.withProperty(STARTTLS_REQUIRED_PROPERTY, "false");
    }
  }

  TransportStrategy toTransportStrategy(
      org.apache.streampipes.model.configuration.TransportStrategy strategy) {
    return switch (Objects.requireNonNull(strategy, "Transport strategy must be configured")) {
      case SMTP -> TransportStrategy.SMTP;
      case SMTPS -> TransportStrategy.SMTPS;
      case SMTP_TLS -> TransportStrategy.SMTP_TLS;
    };
  }
}
