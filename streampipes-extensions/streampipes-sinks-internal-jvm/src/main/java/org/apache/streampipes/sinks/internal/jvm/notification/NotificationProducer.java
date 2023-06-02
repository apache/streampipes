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

package org.apache.streampipes.sinks.internal.jvm.notification;


import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.Notification;
import org.apache.streampipes.model.graph.DataSinkDescription;
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
import java.util.Date;
import java.util.UUID;

public class NotificationProducer extends StreamPipesDataSink {

  private static final String HASHTAG = "#";
  private static final String TITLE_KEY = "title";
  private static final String CONTENT_KEY = "content";
  private static final String SILENT_PERIOD = "silent-period";

  private String title;
  private String content;

  private long silentPeriodInSeconds;
  private long lastMessageEpochSecond = -1;

  private String correspondingPipelineId;
  private String correspondingUser;

  private IStreamPipesClient client;


  @Override
  public void onInvocation(SinkParams parameters, EventSinkRuntimeContext context) throws
      SpRuntimeException {
    this.title = parameters.extractor().singleValueParameter(TITLE_KEY, String.class);
    this.content = parameters.extractor().singleValueParameter(CONTENT_KEY, String.class);
    this.silentPeriodInSeconds = parameters.extractor().singleValueParameter(SILENT_PERIOD, Integer.class) * 60;
    this.correspondingPipelineId = parameters.getModel().getCorrespondingPipeline();
    this.correspondingUser = parameters.getModel().getCorrespondingUser();
    this.client = context.getStreamPipesClient();
  }

  @Override
  public void onEvent(Event inputEvent) {
    if (shouldSendNotification()) {
      Date currentDate = new Date();
      Notification notification = new Notification();
      notification.setId(UUID.randomUUID().toString());
      notification.setRead(false);
      notification.setTitle(title);
      notification.setMessage(PlaceholderExtractor.replacePlaceholders(inputEvent, content));
      notification.setCreatedAt(currentDate);
      notification.setCreatedAtTimestamp(currentDate.getTime());
      notification.setCorrespondingPipelineId(correspondingPipelineId);
      notification.setTargetedAt(correspondingUser);

      // TODO add targeted user to notification object

      client.notificationsApi().add(notification);
      this.lastMessageEpochSecond = Instant.now().getEpochSecond();
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }

  private boolean shouldSendNotification() {
    if (this.lastMessageEpochSecond == -1) {
      return true;
    } else {
      return Instant.now().getEpochSecond() >= (this.lastMessageEpochSecond + this.silentPeriodInSeconds);
    }
  }

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.internal.jvm.notification")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.INTERNAL, DataSinkType.NOTIFICATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredTextParameter(Labels.withId(TITLE_KEY))
        .requiredHtmlInputParameter(Labels.withId(CONTENT_KEY))
        .requiredIntegerParameter(Labels.withId(SILENT_PERIOD), 10)
        .build();
  }
}
