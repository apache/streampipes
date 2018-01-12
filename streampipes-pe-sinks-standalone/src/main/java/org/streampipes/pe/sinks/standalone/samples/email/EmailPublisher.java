package org.streampipes.pe.sinks.standalone.samples.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.wrapper.runtime.EventSink;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

public class EmailPublisher implements EventSink<EmailParameters> {

    static Logger LOG = LoggerFactory.getLogger(EmailPublisher.class);

    MimeMessage message;

    @Override
    public void bind(EmailParameters parameters) throws SpRuntimeException {
        String from = ActionConfig.INSTANCE.getEmailFrom();
        String to = parameters.getToEmailAddress();
        String subject = parameters.getSubject();
        String content = parameters.getContent();
        String username = ActionConfig.INSTANCE.getEmailUsername();
        String password = ActionConfig.INSTANCE.getEmailPassword();
        String host = ActionConfig.INSTANCE.getEmailSmtpHost();
        int port = ActionConfig.INSTANCE.getEmailSmtpPort();
        boolean starttls = ActionConfig.INSTANCE.useEmailStarttls();
        boolean ssl = ActionConfig.INSTANCE.useEmailSll();

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
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
        } catch (MessagingException e) {
           LOG.error(e.toString());
        }

    }

    @Override
    public void onEvent(Map<String, Object> event, String sourceInfo) {
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            LOG.error(e.toString());
        }
    }

    @Override
    public void discard() throws SpRuntimeException {
    }
}
