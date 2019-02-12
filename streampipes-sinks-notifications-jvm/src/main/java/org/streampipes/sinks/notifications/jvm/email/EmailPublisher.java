/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sinks.notifications.jvm.email;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.EventConverter;
import org.streampipes.sinks.notifications.jvm.config.SinksNotificationsJvmConfig;
import org.streampipes.wrapper.context.RuntimeContext;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailPublisher implements EventSink<EmailParameters> {

    private static Logger LOG;

    private MimeMessage message;
    private String content;

    @Override
    public void onInvocation(EmailParameters parameters, RuntimeContext runtimeContext) {
        LOG = parameters.getGraph().getLogger(EmailPublisher.class);

        String from = SinksNotificationsJvmConfig.INSTANCE.getEmailFrom();
        String to = parameters.getToEmailAddress();
        String subject = parameters.getSubject();
        this.content = parameters.getContent();
        String username = SinksNotificationsJvmConfig.INSTANCE.getEmailUsername();
        String password = SinksNotificationsJvmConfig.INSTANCE.getEmailPassword();
        String host = SinksNotificationsJvmConfig.INSTANCE.getEmailSmtpHost();
        int port = SinksNotificationsJvmConfig.INSTANCE.getEmailSmtpPort();
        boolean starttls = SinksNotificationsJvmConfig.INSTANCE.useEmailStarttls();
        boolean ssl = SinksNotificationsJvmConfig.INSTANCE.useEmailSll();

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", String.valueOf(port));

        if(starttls)
            properties.put("mail.smtp.starttls.enable","true");
        if(ssl)
            properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            this.message = new MimeMessage(session);
            this.message.setFrom(new InternetAddress(from));
            this.message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            this.message.setSubject(subject);
        } catch (MessagingException e) {
           LOG.error(e.toString());
        }

    }

    @Override
    public void onEvent(Event inputEvent) {
        String contentWithValues = this.content;
        Map<String, Object> inputMap = new EventConverter(inputEvent).toMap();
        try {
            for (Map.Entry entry: inputMap.entrySet()) {
                contentWithValues = contentWithValues.replaceAll("#" + entry.getKey() + "#",
                        entry.getValue().toString());
            }
            this.message.setContent(contentWithValues, "text/html; charset=utf-8");
            Transport.send(message);
            LOG.info("Sent notifaction email");
        } catch (MessagingException e) {
            LOG.error(e.toString());
        }
    }

    @Override
    public void onDetach() throws SpRuntimeException {
    }
}
