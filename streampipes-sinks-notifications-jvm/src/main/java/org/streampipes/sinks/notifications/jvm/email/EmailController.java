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

import org.streampipes.model.DataSinkType;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.builder.DataSinkBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sinks.notifications.jvm.config.SinksNotificationsJvmConfig;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class EmailController extends StandaloneEventSinkDeclarer<EmailParameters> {

  private static final String TO_EMAIL_ADRESS = "to_email";
  private static final String EMAIL_SUBJECT = "email_subject";
  private static final String EMAIL_CONTENT = "email_content";


  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("email_sink", "Email Notification", "Email bot to send notifications emails")
            .category(DataSinkType.NOTIFICATION)
            .iconUrl(SinksNotificationsJvmConfig.getIconUrl("email"))
            .requiredTextParameter(Labels.from(TO_EMAIL_ADRESS, "To", "Receiver E-mail address"))
            .requiredTextParameter(Labels.from(EMAIL_SUBJECT, "Subject", "The subject of the email"))
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredHtmlInputParameter(Labels.from(EMAIL_CONTENT, "Content", "Enter the email text. You can" +
                    "use place holders like #fieldName# to add the value of a stream variable."))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventSink<EmailParameters> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {

    String toEmail = extractor.singleValueParameter(TO_EMAIL_ADRESS, String.class);
    String subject = extractor.singleValueParameter(EMAIL_SUBJECT, String.class);
    String content = extractor.singleValueParameter(EMAIL_CONTENT, String.class);

    EmailParameters params = new EmailParameters(graph, toEmail, subject, content);

    return new ConfiguredEventSink<>(params, () -> new EmailPublisher(params));
  }
}

