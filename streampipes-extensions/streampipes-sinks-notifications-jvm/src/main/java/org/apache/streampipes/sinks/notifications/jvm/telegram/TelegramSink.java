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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TelegramSink extends StreamPipesDataSink {
  private static final String CHANNEL_NAME_OR_CHAT_ID = "channel-chat-name";
  private static final String MESSAGE_TEXT = "message-text";
  private static final String BOT_API_KEY = "api-key";

  private static final String ENDPOINT = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=%s";
  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
  private static final String HASH_TAG = "#";
  private static final String HTML = "HTML";

  private String apiKey;
  private String channelOrChatId;
  private String message;

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
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    apiKey = extractor.secretValue(BOT_API_KEY);
    channelOrChatId = extractor.singleValueParameter(CHANNEL_NAME_OR_CHAT_ID, String.class);
    message = extractor.singleValueParameter(MESSAGE_TEXT, String.class);
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    try {
      String content = replacePlaceholders(event, this.message);
      content = trimHTML(content);
      content = URLEncoder.encode(content, StandardCharsets.UTF_8);
      String url = String.format(ENDPOINT, this.apiKey, this.channelOrChatId, content, HTML);
      Request request = new Request.Builder().url(url).build();
      try (Response response = HTTP_CLIENT.newCall(request).execute()) {
        if (!response.isSuccessful()) {
          throw new SpRuntimeException("Could not send message. " + response);
        }
      }
    } catch (UnsupportedEncodingException e) {
      throw new SpRuntimeException("Could not encode message.", e);
    } catch (IOException e) {
      throw new SpRuntimeException("Could not send message.", e);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    // Do nothing
  }

  private String replacePlaceholders(Event event, String content) {
    for (Map.Entry<String, Object> entry : event.getRaw().entrySet()) {
      content = content.replaceAll(
          HASH_TAG + entry.getKey() + HASH_TAG,
          String.valueOf(entry.getValue())
      );
    }
    return content;
  }

  private String trimHTML(String content) {
    content = content.replaceAll("(</h[^>]+><h[^>]+>)|(</h[^>]+><[^>]+>)|(</p><p>)", "\n");
    content = content.replaceAll("(<h[^>]+>)|(</p>)|(<p>)|(<span[^>]+>)|(</span>)", "");

    return content;
  }
}
