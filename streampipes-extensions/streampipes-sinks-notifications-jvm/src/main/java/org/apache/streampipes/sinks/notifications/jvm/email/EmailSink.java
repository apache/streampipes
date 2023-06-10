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

package org.apache.streampipes.sinks.notifications.jvm.email;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.mail.SpEmail;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.shared.PlaceholderExtractor;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import java.time.Instant;
import java.util.Collections;

public class EmailSink extends StreamPipesDataSink {

  private static final String TO_EMAIL_ADRESS = "to_email";
  private static final String EMAIL_SUBJECT = "email_subject";
  private static final String EMAIL_CONTENT = "email_content";
  private static final String SILENT_PERIOD = "silent-period";

  private SpEmail preparedEmail;
  private long silentPeriodInSeconds;
  private long lastMailEpochSecond = -1;

  private String originalContent;

  private IStreamPipesClient client;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.notifications.jvm.email")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.NOTIFICATION)
        .requiredTextParameter(Labels.withId(TO_EMAIL_ADRESS))
        .requiredTextParameter(Labels.withId(EMAIL_SUBJECT))
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredHtmlInputParameter(Labels.withId(EMAIL_CONTENT))
        .requiredIntegerParameter(Labels.withId(SILENT_PERIOD))
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    String toEmail = extractor.singleValueParameter(TO_EMAIL_ADRESS, String.class);
    String subject = extractor.singleValueParameter(EMAIL_SUBJECT, String.class);
    Integer silentPeriod = extractor.singleValueParameter(SILENT_PERIOD, Integer.class);

    this.preparedEmail = new SpEmail();
    this.preparedEmail.setRecipients(Collections.singletonList(toEmail));
    this.preparedEmail.setSubject(subject);

    this.silentPeriodInSeconds = silentPeriod * 60;
    this.client = runtimeContext.getStreamPipesClient();
    this.originalContent = extractor.singleValueParameter(EMAIL_CONTENT, String.class);
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    if (shouldSendMail()) {
      String message = PlaceholderExtractor.replacePlaceholders(event, this.originalContent);
      this.preparedEmail.setMessage(message);
      this.client.deliverEmail(this.preparedEmail);
      this.lastMailEpochSecond = Instant.now().getEpochSecond();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  private boolean shouldSendMail() {
    if (this.lastMailEpochSecond == -1) {
      return true;
    } else {
      return Instant.now().getEpochSecond() >= (this.lastMailEpochSecond + this.silentPeriodInSeconds);
    }
  }
}

