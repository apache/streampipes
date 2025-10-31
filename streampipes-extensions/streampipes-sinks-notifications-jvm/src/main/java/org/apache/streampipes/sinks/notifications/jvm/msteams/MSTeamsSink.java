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

package org.apache.streampipes.sinks.notifications.jvm.msteams;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.shared.PlaceholderExtractor;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MSTeamsSink extends StreamPipesNotificationSink {

  public static final String ID = "org.apache.streampipes.sinks.notifications.jvm.msteams";

  private static final String KEY_MESSAGE_ADVANCED = "messageAdvanced";
  private static final String KEY_MESSAGE_ADVANCED_CONTENT = "messageContentAdvanced";
  private static final String KEY_MESSAGE_SIMPLE = "messageSimple";
  private static final String KEY_MESSAGE_SIMPLE_CONTENT = "messageContentSimple";
  private static final String KEY_MESSAGE_TYPE_ALTERNATIVES = "messageType";
  private static final String KEY_WEBHOOK_URL = "webhookUrl";
  public static final String KEY_PROXY_ALTERNATIVES = "proxy";
  public static final String KEY_PROXY_DISABLED = "proxyDisabled";
  public static final String KEY_PROXY_ENABLED = "proxyEnabled";
  public static final String KEY_PROXY_GROUP = "proxyConfigurationGroup";
  public static final String KEY_PROXY_URL = "proxyUrl";
  protected static final String SIMPLE_MESSAGE_TEMPLATE = "{\"text\": \"%s\"}";

  private String messageContent;
  private boolean isSimpleMessageMode;
  private String webhookUrl;
  private ObjectMapper objectMapper;
  private HttpClient httpClient;

  public MSTeamsSink() {
    super();
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void onInvocation(
      SinkParams parameters,
      EventSinkRuntimeContext runtimeContext
  ) throws SpRuntimeException {
    super.onInvocation(parameters, runtimeContext);

    this.objectMapper = new ObjectMapper();

    var extractor = parameters.extractor();
    webhookUrl = extractor.secretValue(KEY_WEBHOOK_URL);

    validateWebhookUrl(webhookUrl);

    var selectedAlternative = extractor.selectedAlternativeInternalId(KEY_MESSAGE_TYPE_ALTERNATIVES);
    if (selectedAlternative.equals(KEY_MESSAGE_ADVANCED)) {
      isSimpleMessageMode = false;
      messageContent = extractor.singleValueParameter(KEY_MESSAGE_ADVANCED_CONTENT, String.class);
    } else {
      isSimpleMessageMode = true;
      messageContent = extractor.singleValueParameter(KEY_MESSAGE_SIMPLE_CONTENT, String.class);
    }

    var selectedProxyAlternative = extractor.selectedAlternativeInternalId(KEY_PROXY_ALTERNATIVES);
    if (selectedProxyAlternative.equals(KEY_PROXY_DISABLED)) {
      this.httpClient = HttpClients.createDefault();
    } else {
      var proxyUrl = extractor.singleValueParameter(KEY_PROXY_URL, String.class);
      this.httpClient = HttpClientBuilder
          .create()
          .setProxy(HttpHost.create(proxyUrl))
          .build();
    }
  }

  @Override
  public void onNotificationEvent(Event event) {

    // This sink allows to use placeholders for event properties when defining the message content in the UI
    // Therefore, we need to replace these placeholders based on the actual event before actually sending the message
    var processedMessageContent = PlaceholderExtractor.replacePlaceholders(event, messageContent);

    String teamsMessageContent;
    if (isSimpleMessageMode) {
      teamsMessageContent = createMessageFromSimpleContent(processedMessageContent);
    } else {
      teamsMessageContent = createMessageFromAdvancedContent(processedMessageContent);
    }
    sendPayloadToWebhook(httpClient, teamsMessageContent, webhookUrl);
  }

  @Override
  public DataSinkBuilder declareModelWithoutSilentPeriod() {
    return DataSinkBuilder
        .create(ID, 1)
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .category(DataSinkType.NOTIFICATION)
        .requiredStream(
            StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build()
        )
        .requiredSecret(Labels.withId(KEY_WEBHOOK_URL))
        .requiredAlternatives(
            Labels.withId(KEY_PROXY_ALTERNATIVES),
            Alternatives.from(Labels.withId(KEY_PROXY_DISABLED)),
            Alternatives.from(Labels.withId(KEY_PROXY_ENABLED),
                StaticProperties.group(Labels.withId(KEY_PROXY_GROUP),
                    StaticProperties.stringFreeTextProperty(Labels.withId(KEY_PROXY_URL))
                )
            ))
        .requiredAlternatives(
            Labels.withId(KEY_MESSAGE_TYPE_ALTERNATIVES),
            Alternatives.from(
                Labels.withId(KEY_MESSAGE_SIMPLE),
                StaticProperties.stringFreeTextProperty(
                    Labels.withId(KEY_MESSAGE_SIMPLE_CONTENT),
                    true,
                    true
                ),
                true
            ),
            Alternatives.from(
                Labels.withId(KEY_MESSAGE_ADVANCED),
                StaticProperties.stringFreeTextProperty(
                    Labels.withId(KEY_MESSAGE_ADVANCED_CONTENT),
                    true,
                    true
                )
            )
        );
  }

  @Override
  public void onDetach() {
    // nothing to do
  }

  /**
   * Creates a JSON string intended for the MS Teams Webhook URL based on the provided plain message content.
   * <p>
   * This method utilizes a basic approach for constructing messages to be sent to MS Teams.
   * If you intend to provide text in the form of Adaptive Cards, consider using
   * {@link #createMessageFromAdvancedContent(String)} for a more advanced and interactive message format.
   * </p>
   *
   * @param messageContent The plain message content to be included in the Teams message.
   * @return A JSON string formatted using a predefined template with the provided message content.
   */
  protected String createMessageFromSimpleContent(String messageContent) {
    return SIMPLE_MESSAGE_TEMPLATE.formatted(messageContent);
  }

  /**
   * Creates a message for MS Teams from a JSON string, specifically designed for use with Adaptive Cards.
   * <p>
   * This method takes a JSON string as input, which is expected to represent the content of the message.
   * The content is directly forwarded to MS Teams, allowing for the utilization of Adaptive Cards.
   * Adaptive Cards provide a flexible and interactive way to present content in Microsoft Teams.
   * Learn more about Adaptive Cards: <a href="https://learn.microsoft.com/en-us/adaptive-cards/">here</a>
   * </p>
   *
   * @param messageContent The JSON string representing the content of the message.
   * @return The original JSON string, unchanged.
   * @throws SpRuntimeException If the provided message is not a valid JSON string.
   */
  protected String createMessageFromAdvancedContent(String messageContent) {
    try {
      objectMapper.readValue(messageContent, Object.class);
    } catch (JsonProcessingException e) {
      throw new SpRuntimeException(
          "Advanced message content provided is not a valid JSON string: %s".formatted(messageContent),
          e
      );
    }
    return messageContent;
  }

  /**
   * Sends a payload to a webhook using the provided HTTP client, payload, and webhook URL.
   *
   * @param httpClient The HTTP client used to send the payload.
   * @param payload    The payload to be sent to the webhook.
   * @param webhookUrl The URL of the webhook to which the payload will be sent.
   * @throws SpRuntimeException If an I/O error occurs while sending the payload to the webhook or
   *                            the payload sent is not accepted by the API.
   */
  protected void sendPayloadToWebhook(HttpClient httpClient, String payload, String webhookUrl) {
    try {
      var contentEntity = new StringEntity(payload);
      contentEntity.setContentType(ContentType.APPLICATION_JSON.toString());

      var postRequest = new HttpPost(webhookUrl);
      postRequest.setEntity(contentEntity);

      var result = httpClient.execute(postRequest);
      if (result.getStatusLine()
          .getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
        throw new SpRuntimeException(
            "The provided message payload was not accepted by the MS Teams API: %s"
                .formatted(payload)
        );
      }
    } catch (IOException e) {
      throw new SpRuntimeException("Sending notification to MS Teams failed.", e);
    }
  }

  /**
   * Validates a webhook URL to ensure it is not null, not empty, and has a valid URL format.
   *
   * @param webhookUrl The webhook URL to be validated.
   * @throws SpRuntimeException If the webhook URL is null or empty, or if it is not a valid URL.
   */
  protected void validateWebhookUrl(String webhookUrl) {
    if (webhookUrl == null || webhookUrl.isEmpty()) {
      throw new SpRuntimeException("Given webhook URL is empty");
    }
    try {
      new URL(webhookUrl);
    } catch (MalformedURLException e) {
      throw new SpRuntimeException("The given webhook is not a valid URL: %s".formatted(webhookUrl));
    }
  }
}
