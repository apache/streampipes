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
import org.apache.streampipes.model.configuration.TransportStrategy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MailConfigurationBuilderTest {

  private final MailConfigurationBuilder builder = new MailConfigurationBuilder();

  @Test
  public void toTransportStrategyMapsSmtpWithoutTls() {
    Assertions.assertEquals(
        org.simplejavamail.api.mailer.config.TransportStrategy.SMTP,
        builder.toTransportStrategy(TransportStrategy.SMTP)
    );
  }

  @Test
  public void toTransportStrategyMapsSmtps() {
    Assertions.assertEquals(
        org.simplejavamail.api.mailer.config.TransportStrategy.SMTPS,
        builder.toTransportStrategy(TransportStrategy.SMTPS)
    );
  }

  @Test
  public void toTransportStrategyMapsStartTls() {
    Assertions.assertEquals(
        org.simplejavamail.api.mailer.config.TransportStrategy.SMTP_TLS,
        builder.toTransportStrategy(TransportStrategy.SMTP_TLS)
    );
  }

  @Test
  public void toTransportStrategyRejectsMissingStrategy() {
    var exception = Assertions.assertThrows(
        NullPointerException.class,
        () -> builder.toTransportStrategy(null)
    );

    Assertions.assertEquals("Transport strategy must be configured", exception.getMessage());
  }

  @Test
  public void buildMailerFromConfigDisablesStartTlsForSmtp() {
    var mailer = builder.buildMailerFromConfig(makeConfig(TransportStrategy.SMTP));

    Assertions.assertEquals(
        org.simplejavamail.api.mailer.config.TransportStrategy.SMTP,
        mailer.getTransportStrategy()
    );
    Assertions.assertEquals("false", mailer.getSession().getProperty("mail.smtp.starttls.enable"));
    Assertions.assertEquals("false", mailer.getSession().getProperty("mail.smtp.starttls.required"));
  }

  @Test
  public void buildMailerFromConfigRequiresStartTlsForSmtpTls() {
    var mailer = builder.buildMailerFromConfig(makeConfig(TransportStrategy.SMTP_TLS));

    Assertions.assertEquals(
        org.simplejavamail.api.mailer.config.TransportStrategy.SMTP_TLS,
        mailer.getTransportStrategy()
    );
    Assertions.assertEquals("true", mailer.getSession().getProperty("mail.smtp.starttls.enable"));
    Assertions.assertEquals("true", mailer.getSession().getProperty("mail.smtp.starttls.required"));
  }

  private EmailConfig makeConfig(TransportStrategy transportStrategy) {
    var config = new EmailConfig();
    config.setTransportStrategy(transportStrategy);
    config.setSmtpServerHost("smtp.example.org");
    config.setSmtpServerPort(25);
    return config;
  }
}
