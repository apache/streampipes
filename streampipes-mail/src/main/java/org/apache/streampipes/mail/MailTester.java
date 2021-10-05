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
package org.apache.streampipes.mail;

import org.apache.streampipes.config.backend.model.EmailConfig;
import org.apache.streampipes.mail.config.MailConfigurationBuilder;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;

public class MailTester {

  public void sendTestMail(EmailConfig emailConfig) {
    Mailer mailer = new MailConfigurationBuilder().buildMailerFromConfig(emailConfig);

    mailer.sendMail(makeTestMail(emailConfig));
  }

  private Email makeTestMail(EmailConfig emailConfig) {
    return EmailBuilder
            .startingBlank()
            .appendText("Hello from StreamPipes")
            .from(emailConfig.getSenderAddress())
            .to(emailConfig.getTestRecipientAddress())
            .buildEmail();
  }

}
