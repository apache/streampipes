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

public class MailConfigurationBuilder {

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


    return builder.buildMailer();

  }

  private TransportStrategy toTransportStrategy(
      org.apache.streampipes.model.configuration.TransportStrategy strategy) {
    if (strategy == org.apache.streampipes.model.configuration.TransportStrategy.SMTP) {
      return TransportStrategy.SMTP;
    } else if (strategy == org.apache.streampipes.model.configuration.TransportStrategy.SMTPS) {
      return TransportStrategy.SMTPS;
    } else {
      return TransportStrategy.SMTP_TLS;
    }
  }
}
