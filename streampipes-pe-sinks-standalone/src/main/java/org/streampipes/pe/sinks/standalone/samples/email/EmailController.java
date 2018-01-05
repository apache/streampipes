package org.streampipes.pe.sinks.standalone.samples.email;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class EmailController extends StandaloneEventSinkDeclarer<EmailParameters> {

    private static final String TO_EMAIL_ADRESS = "to_email";
    private static final String EMAIL_SUBJECT ="email_subject";


    @Override
    public DataSinkDescription declareModel() {
        return DataSinkBuilder.create("email_sink", "Email Notification", "Email bot to send notifications emails")
                .category(DataSinkType.NOTIFICATION)
                .iconUrl(ActionConfig.getIconUrl("email_senke_icon"))
                .requiredTextParameter(TO_EMAIL_ADRESS, "To", "Receiver E-mail address  ")
                .requiredTextParameter(EMAIL_SUBJECT, "Subject", "The subject of the email")
                .requiredPropertyStream1(EpRequirements.anyProperty())
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }

    @Override
    public ConfiguredEventSink<EmailParameters> onInvocation(DataSinkInvocation graph) {
        DataSinkParameterExtractor extractor = getExtractor(graph);

        String toEmail = extractor.singleValueParameter(TO_EMAIL_ADRESS, String.class);
        String subject = extractor.singleValueParameter(EMAIL_SUBJECT, String.class);

        EmailParameters params = new EmailParameters(graph, toEmail, subject);

        return new ConfiguredEventSink<>(params, EmailPublisher::new);
    }
}

