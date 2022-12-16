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

import org.apache.streampipes.mail.template.AccountActiviationMailTemplate;
import org.apache.streampipes.mail.template.InitialPasswordMailTemplate;
import org.apache.streampipes.mail.template.PasswordRecoveryMailTemplate;
import org.apache.streampipes.mail.utils.MailUtils;
import org.apache.streampipes.model.mail.SpEmail;

import org.simplejavamail.api.email.Email;

import java.io.IOException;

public class MailSender extends AbstractMailer {

  public void sendEmail(SpEmail mail) {
    Email email = baseEmail()
        .withRecipients(toSimpleRecipientList(mail.getRecipients()))
        .withSubject(mail.getSubject())
        .appendText(mail.getMessage())
        .buildEmail();

    deliverMail(email);
  }

  public void sendAccountActivationMail(String recipientAddress,
                                        String activationCode) throws IOException {
    Email email = baseEmail()
        .withSubject(MailUtils.extractAppName() + " - Account Activation")
        .appendTextHTML(new AccountActiviationMailTemplate(activationCode).generateTemplate())
        .to(recipientAddress)
        .buildEmail();

    deliverMail(email);
  }

  public void sendPasswordRecoveryMail(String recipientAddress,
                                       String recoveryCode) throws IOException {
    Email email = baseEmail()
        .withSubject(MailUtils.extractAppName() + " - Password Recovery")
        .appendTextHTML(new PasswordRecoveryMailTemplate(recoveryCode).generateTemplate())
        .to(recipientAddress)
        .buildEmail();

    deliverMail(email);
  }

  public void sendInitialPasswordMail(String recipientAddress,
                                      String generatedProperty) throws IOException {
    Email email = baseEmail()
        .withSubject(MailUtils.extractAppName() + " - New Account")
        .appendTextHTML(new InitialPasswordMailTemplate(generatedProperty).generateTemplate())
        .to(recipientAddress)
        .buildEmail();

    deliverMail(email);
  }
}
