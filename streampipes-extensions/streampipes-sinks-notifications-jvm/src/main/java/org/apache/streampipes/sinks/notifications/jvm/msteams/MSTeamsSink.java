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
import org.apache.streampipes.extensions.api.pe.config.IDataSinkConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.shared.PlaceholderExtractor;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.sink.DataSinkConfiguration;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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

    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 2000;
    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int SOCKET_TIMEOUT_MS = 10_000;
    private static final int CONNECTION_REQUEST_TIMEOUT_MS = 5_000;

    private static final Logger LOG = LoggerFactory.getLogger(MSTeamsSink.class);

    private String messageContent;
    private boolean isSimpleMessageMode;
    private URI webhookUrl;
    private ObjectMapper objectMapper;
    private CloseableHttpClient httpClient;

    public MSTeamsSink() {
        super();
        this.objectMapper = JacksonSerializer
                .getObjectMapper(Map.of(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true));
    }

    @Override
    public IDataSinkConfiguration declareConfig() {
        var builder = declareModelWithoutSilentPeriod();
        return DataSinkConfiguration.create(MSTeamsSink::new, builder.build());
    }

    @Override
    public void onPipelineStarted(IDataSinkParameters parameters, EventSinkRuntimeContext runtimeContext) {
        super.onPipelineStarted(parameters, runtimeContext);

        this.objectMapper = JacksonSerializer
                .getObjectMapper(Map.of(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true));

        var extractor = parameters.extractor();
        webhookUrl = validateWebhookUrl(extractor.secretValue(KEY_WEBHOOK_URL));

        var selectedAlternative = extractor.selectedAlternativeInternalId(KEY_MESSAGE_TYPE_ALTERNATIVES);
        if (selectedAlternative.equals(KEY_MESSAGE_ADVANCED)) {
            isSimpleMessageMode = false;
            messageContent = extractor.singleValueParameter(KEY_MESSAGE_ADVANCED_CONTENT, String.class);
        } else {
            isSimpleMessageMode = true;
            messageContent = extractor.singleValueParameter(KEY_MESSAGE_SIMPLE_CONTENT, String.class);
        }

        var selectedProxyAlternative = extractor.selectedAlternativeInternalId(KEY_PROXY_ALTERNATIVES);
        var requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT_MS)
                .setSocketTimeout(SOCKET_TIMEOUT_MS).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MS).build();

        if (selectedProxyAlternative.equals(KEY_PROXY_DISABLED)) {
            this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            LOG.info("MS Teams sink initialized (no proxy), webhook host={}", webhookUrl.getHost());
        } else {
            var proxyUrl = extractor.singleValueParameter(KEY_PROXY_URL, String.class);
            this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                    .setProxy(HttpHost.create(proxyUrl)).build();
            LOG.info("MS Teams sink initialized via proxy {}", proxyUrl);
        }
    }

    @Override
    public void onNotificationEvent(Event event) {

        // This sink allows to use placeholders for event properties when defining the
        // message content in the UI
        // Therefore, we need to replace these placeholders based on the actual event
        // before actually sending the message
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
        return DataSinkBuilder.create(ID, 1).withLocales(Locales.EN)
                .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
                .category(DataSinkType.NOTIFICATION)
                .requiredStream(
                        StreamRequirementsBuilder.create().requiredProperty(EpRequirements.anyProperty()).build())
                .requiredSecret(Labels.withId(KEY_WEBHOOK_URL))
                .requiredAlternatives(Labels.withId(KEY_PROXY_ALTERNATIVES),
                        Alternatives.from(Labels.withId(KEY_PROXY_DISABLED)),
                        Alternatives.from(Labels.withId(KEY_PROXY_ENABLED),
                                StaticProperties.group(Labels.withId(KEY_PROXY_GROUP),
                                        StaticProperties.stringFreeTextProperty(Labels.withId(KEY_PROXY_URL)))))
                .requiredAlternatives(Labels.withId(KEY_MESSAGE_TYPE_ALTERNATIVES),
                        Alternatives.from(Labels.withId(KEY_MESSAGE_SIMPLE),
                                StaticProperties.stringFreeTextProperty(Labels.withId(KEY_MESSAGE_SIMPLE_CONTENT), true,
                                        true),
                                true),
                        Alternatives.from(Labels.withId(KEY_MESSAGE_ADVANCED), StaticProperties
                                .stringFreeTextProperty(Labels.withId(KEY_MESSAGE_ADVANCED_CONTENT), true, true)));
    }

    @Override
    public void onPipelineStopped() {
        if (httpClient != null) {
            try {
                httpClient.close();
                LOG.info("MS Teams sink stopped, HTTP client closed");
            } catch (IOException e) {
                LOG.warn("Error closing MS Teams HTTP client: {}", e.getMessage());
            }
        }
    }

    protected String createMessageFromSimpleContent(String messageContent) {
        var card = objectMapper.createObjectNode();
        card.put("$schema", "http://adaptivecards.io/schemas/adaptive-card.json");
        card.put("type", "AdaptiveCard");
        card.put("version", "1.4");

        var textBlock = objectMapper.createObjectNode();
        textBlock.put("type", "TextBlock");
        textBlock.put("text", messageContent);
        textBlock.put("wrap", true);
        card.putArray("body").add(textBlock);

        var attachment = objectMapper.createObjectNode();
        attachment.put("contentType", "application/vnd.microsoft.card.adaptive");
        attachment.set("content", card);

        var message = objectMapper.createObjectNode();
        message.put("type", "message");
        message.putArray("attachments").add(attachment);

        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException("Could not serialize MS Teams message content", e);
        }
    }

    /**
     * Creates a message for MS Teams from a JSON string, specifically designed for
     * use with Adaptive Cards.
     * <p>
     * This method takes a JSON string as input, which is expected to represent the
     * content of the message. The content is directly forwarded to MS Teams,
     * allowing for the utilization of Adaptive Cards. Adaptive Cards provide a
     * flexible and interactive way to present content in Microsoft Teams. Learn
     * more about Adaptive Cards:
     * <a href="https://learn.microsoft.com/en-us/adaptive-cards/">here</a>
     * </p>
     *
     * @param messageContent The JSON string representing the content of the
     *                       message.
     * @return The original JSON string, unchanged.
     * @throws SpRuntimeException If the provided message is not a valid JSON
     *                            string.
     */
    protected String createMessageFromAdvancedContent(String messageContent) {
        try {
            objectMapper.readValue(messageContent, Object.class);
        } catch (JsonProcessingException e) {
            throw new SpRuntimeException(
                    "Advanced message content provided is not a valid JSON string: %s".formatted(messageContent), e);
        }
        return messageContent;
    }

    /**
     * Sends a payload to the configured MS Teams webhook, retrying transient
     * failures with a fixed delay.
     * <p>
     * A request is retried up to {@value #MAX_ATTEMPTS} times (waiting
     * {@value #RETRY_DELAY_MS} ms between attempts) when the call fails with an I/O
     * error or the webhook responds with HTTP 429 or a 5xx status. A 4xx status
     * other than 429 is treated as permanent (e.g. an invalid webhook, revoked
     * token, or malformed card) and fails immediately without retrying.
     * </p>
     * <p>
     * If the calling thread is interrupted (for example because the pipeline is
     * being stopped), the method restores the interrupt flag and returns quietly
     * without throwing, since an aborted send during shutdown is not a failure.
     * </p>
     *
     * @param client  The HTTP client used to send the payload.
     * @param payload The payload to be sent to the webhook.
     * @param url     The URL of the webhook to which the payload will be sent.
     * @throws SpRuntimeException If the webhook rejects the message with a
     *                            permanent (non-429 4xx) status, or if all retry
     *                            attempts are exhausted without success.
     */
    void sendPayloadToWebhook(CloseableHttpClient client, String payload, URI url) {
        var post = new HttpPost(url);
        post.setEntity(new StringEntity(payload, ContentType.APPLICATION_JSON));

        SpRuntimeException last = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            LOG.debug("Sending notification to MS Teams (attempt {}/{})", attempt, MAX_ATTEMPTS);

            try (CloseableHttpResponse response = client.execute(post)) {
                int status = response.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    if (attempt > 1) {
                        LOG.info("MS Teams notification succeeded on attempt {}/{}", attempt, MAX_ATTEMPTS);
                    } else {
                        LOG.debug("MS Teams notification sent (HTTP {})", status);
                    }
                    return;
                }

                if (status >= 400 && status < 500 && status != 429) {
                    // client error: retrying won't help (bad webhook, revoked token, malformed
                    // card)
                    LOG.error("MS Teams rejected the message with HTTP {} - not retrying", status);
                    throw new SpRuntimeException("Teams rejected the message: HTTP " + status);
                }

                // 5xx or 429 -> transient, worth retrying
                LOG.warn("MS Teams returned HTTP {} (attempt {}/{})", status, attempt, MAX_ATTEMPTS);
                last = new SpRuntimeException("Teams returned HTTP " + status);

            } catch (IOException e) {
                if (Thread.currentThread().isInterrupted()) {
                    LOG.debug("MS Teams request aborted because the pipeline is stopping - ignoring");
                    return; // clean shutdown, not a failure
                }
                LOG.warn("MS Teams request failed (attempt {}/{}): {}", attempt, MAX_ATTEMPTS, e.getMessage());
                last = new SpRuntimeException("Sending notification to MS Teams failed.", e);
            }

            if (attempt < MAX_ATTEMPTS) {
                LOG.info("Retrying MS Teams notification in {} ms", RETRY_DELAY_MS);
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOG.debug("Interrupted while waiting to retry - pipeline stopping, giving up");
                    return;
                }
            }
        }

        LOG.error("Giving up on MS Teams notification after {} attempts", MAX_ATTEMPTS);
        // 'last' is always assigned before reaching this point, but guard defensively.
        throw last != null ? last : new SpRuntimeException("Sending notification to MS Teams failed.");
    }

    /**
     * Validates a webhook URL to ensure it is not null, not empty, and has a valid
     * URL format.
     *
     * @param webhookUrl The webhook URL to be validated.
     * @throws SpRuntimeException If the webhook URL is null or empty, or if it is
     *                            not a valid URL.
     */
    protected URI validateWebhookUrl(String webhookUrl) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            throw new SpRuntimeException("Given webhook URL is empty");
        }
        try {
            URI uri = new URI(webhookUrl);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new SpRuntimeException("The given webhook URL is not absolute or has no host");
            }
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                throw new SpRuntimeException("The given webhook URL must use http or https");
            }
            return uri;
        } catch (URISyntaxException e) {
            throw new SpRuntimeException("The given webhook URL is not valid", e);
        }
    }
}