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

package org.apache.streampipes.sinks.notifications.jvm.slack;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class SlackNotificationController extends StandaloneEventSinkDeclarer<SlackNotificationParameters> {

  private static final String CHANNEL_TYPE = "channel-type";
  private static final String RECEIVER = "receiver";
  private static final String CONTENT = "content";
  private static final String AUTH_TOKEN = "auth-token";

  @Override
  public DataSinkDescription declareModel() {

    return DataSinkBuilder.create("org.apache.streampipes.sinks.notifications.jvm.slack")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .category(DataSinkType.NOTIFICATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredTextParameter(Labels.withId(RECEIVER))
        .requiredTextParameter(Labels.withId(CONTENT), false, true)
        .requiredSingleValueSelection(Labels.withId(CHANNEL_TYPE),
            Options.from("User", "Channel"))
        .requiredSecret(Labels.withId(AUTH_TOKEN))
        .build();
  }


  @Override
  public ConfiguredEventSink<SlackNotificationParameters> onInvocation(DataSinkInvocation graph,
                                                                       DataSinkParameterExtractor extractor) {

    String userChannel = extractor.singleValueParameter(RECEIVER, String.class);
    String channelType = extractor.selectedSingleValue(CHANNEL_TYPE, String.class);
    String message = extractor.singleValueParameter(CONTENT, String.class);
    String authToken = extractor.secretValue(AUTH_TOKEN);

    SlackNotificationParameters params = new SlackNotificationParameters(graph, authToken,
        channelType, userChannel, message);

    return new ConfiguredEventSink<>(params, SlackNotification::new);
  }
}
