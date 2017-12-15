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
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class EmailController extends StandaloneEventSinkDeclarer<EmailParameters> {

    private static final String FROM_EMAIL_ADDRESS = "from_email";
    private static final String TO_EMAIL_ADRESS = "to_email";


    @Override
    public DataSinkDescription declareModel() {
        return DataSinkBuilder.create("email_sink", "Email Notification", "Email bot to send notifications emails")
                .category(DataSinkType.NOTIFICATION)
                .iconUrl(ActionConfig.getIconUrl("email_senke_icon"))
                .requiredTextParameter(FROM_EMAIL_ADDRESS, "From", "Sender E-mail address")
                .requiredTextParameter(TO_EMAIL_ADRESS, "To", "Receiver E-mail address  ")
                .requiredPropertyStream1(EpRequirements.anyProperty())
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }

    @Override
    public ConfiguredEventSink<EmailParameters, EventSink<EmailParameters>> onInvocation(DataSinkInvocation graph) {
        DataSinkParameterExtractor extractor = getExtractor(graph);

        String fromEmail = extractor.singleValueParameter(FROM_EMAIL_ADDRESS, String.class);
        String toEmail = extractor.singleValueParameter(TO_EMAIL_ADRESS, String.class);

        EmailParameters params = new EmailParameters(graph, fromEmail, toEmail);

        return null;
    }
}

