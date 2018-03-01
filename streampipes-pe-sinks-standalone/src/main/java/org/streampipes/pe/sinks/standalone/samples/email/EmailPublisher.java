package org.streampipes.pe.sinks.standalone.samples.email;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.util.Logger;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.wrapper.runtime.EventSink;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

public class EmailPublisher implements EventSink<EmailParameters> {

    private static Logger LOG;

    private MimeMessage message;
    private String content;

    @Override
    public void bind(EmailParameters parameters) throws SpRuntimeException {
        LOG = parameters.getGraph().getLogger(EmailPublisher.class);

        String from = ActionConfig.INSTANCE.getEmailFrom();
        String to = parameters.getToEmailAddress();
        String subject = parameters.getSubject();
        this.content = parameters.getContent();
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
            this.message = new MimeMessage(session);
            this.message.setFrom(new InternetAddress(from));
            this.message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            this.message.setSubject(subject);
        } catch (MessagingException e) {
           LOG.error(e.toString());
        }

    }

    @Override
    public void onEvent(Map<String, Object> event, String sourceInfo) {
        String contentWithValues = this.content;
        try {
            for (Map.Entry entry: event.entrySet()) {
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
    public void discard() throws SpRuntimeException {
    }
}
