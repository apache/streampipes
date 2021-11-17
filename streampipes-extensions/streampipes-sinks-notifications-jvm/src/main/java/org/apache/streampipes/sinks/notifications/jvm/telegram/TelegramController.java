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

package org.apache.streampipes.sinks.notifications.jvm.telegram;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class TelegramController extends StandaloneEventSinkDeclarer<TelegramParameters> {
    private static final String CHANNEL_NAME_OR_CHAT_ID = "channel-chat-name";
    private static final String MESSAGE_TEXT = "message-text";
    private static final String BOT_API_KEY = "api-key";

    @Override
    public DataSinkDescription declareModel() {
        return DataSinkBuilder.create("org.apache.streampipes.sinks.notifications.jvm.telegram")
                .withLocales(Locales.EN)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .category(DataSinkType.NOTIFICATION)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredProperty(EpRequirements.anyProperty())
                        .build())
                .requiredSecret(Labels.withId(BOT_API_KEY))
                .requiredTextParameter(Labels.withId(CHANNEL_NAME_OR_CHAT_ID))
                .requiredTextParameter(Labels.withId(MESSAGE_TEXT), true, true, true)
                .build();
    }


    @Override
    public ConfiguredEventSink<TelegramParameters> onInvocation(DataSinkInvocation graph,
                                                                DataSinkParameterExtractor extractor) {
        String apiKey = extractor.secretValue(BOT_API_KEY);
        String channelOrChatId = extractor.singleValueParameter(CHANNEL_NAME_OR_CHAT_ID, String.class);
        String message = extractor.singleValueParameter(MESSAGE_TEXT, String.class);
        TelegramParameters params = new TelegramParameters(graph, apiKey, channelOrChatId, message);
        return new ConfiguredEventSink<>(params, TelegramPublisher::new);
    }
}
