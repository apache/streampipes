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

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestMSTeamsSink {

    @Test
    public void createMessageFromSimpleContent() {
        var sink = new MSTeamsSink();

        var expectedTeamsMessage = """
                {
                  "type" : "message",
                  "attachments" : [ {
                    "contentType" : "application/vnd.microsoft.card.adaptive",
                    "content" : {
                      "$schema" : "http://adaptivecards.io/schemas/adaptive-card.json",
                      "type" : "AdaptiveCard",
                      "version" : "1.4",
                      "body" : [ {
                        "type" : "TextBlock",
                        "text" : "This is test",
                        "wrap" : true
                      } ]
                    }
                  } ]
                }""";

        var createdTeamsMessage = sink.createMessageFromSimpleContent("This is test");

        assertEquals(
                expectedTeamsMessage.replace("\r\n", "\n"),
                createdTeamsMessage.replace("\r\n", "\n")
            );
        }

    @Test
    public void createMessageFromAdvancedContent() {
        var messageContent = "{\"text\": \"Hi this is a message from Apache StreamPipes\"}";

        var sink = new MSTeamsSink();
        assertEquals(messageContent, sink.createMessageFromAdvancedContent(messageContent));
    }

    @Test
    public void createMessageFromAdvancedContentCheckException() {
        var messageContent = "invalid-complex-input";

        var sink = new MSTeamsSink();

        assertThrows(SpRuntimeException.class, () -> sink.createMessageFromAdvancedContent(messageContent));
    }

    @Test
    public void sendPayloadToWebhook() throws IOException, URISyntaxException {

        var mockedClient = mock(CloseableHttpClient.class);
        var mockedResponse = mock(CloseableHttpResponse.class);
        var mockedStatusLine = mock(StatusLine.class);
        var argumentCaptor = ArgumentCaptor.forClass(HttpPost.class);

        when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
        when(mockedClient.execute(any())).thenReturn(mockedResponse);

        var payload = "This is a test";
        var webhook = "https://webhook.com";
        var sink = new MSTeamsSink();

        sink.sendPayloadToWebhook(mockedClient, payload, new URI(webhook));
        verify(mockedClient, times(1)).execute(argumentCaptor.capture());

        var capturedPost = argumentCaptor.getValue();

        Assertions.assertNotNull(capturedPost);
        assertEquals(webhook, capturedPost.getURI().toString());
        assertEquals(ContentType.APPLICATION_JSON.toString(), capturedPost.getEntity().getContentType().getValue());
        assertEquals(payload, EntityUtils.toString(capturedPost.getEntity()));
    }

    @Test
    public void sendPayloadToWebhookBadResponse() throws IOException {
        CloseableHttpClient mockedClient = mock(CloseableHttpClient.class);
        var mockedResponse = mock(CloseableHttpResponse.class);
        var mockedStatusLine = mock(StatusLine.class);

        when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
        when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
        when(mockedClient.execute(any())).thenReturn(mockedResponse);

        var sink = new MSTeamsSink();
        var payload = "<a>invalid</a>";
        var url = "https://webhook.com";

        assertThrows(SpRuntimeException.class, () -> sink.sendPayloadToWebhook(mockedClient, payload, new URI(url)));

        // A 4xx (other than 429) is permanent: it must fail on the first attempt, no
        // retries.
        verify(mockedClient, times(1)).execute(any());
    }

    @Test
    public void sendPayloadToWebhookRetriesOnServerErrorThenGivesUp() throws IOException {
        var mockedClient = mock(CloseableHttpClient.class);
        var mockedResponse = mock(CloseableHttpResponse.class);
        var mockedStatusLine = mock(StatusLine.class);

        // Every attempt returns a 500 -> transient, should be retried up to
        // MAX_ATTEMPTS.
        when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
        when(mockedClient.execute(any())).thenReturn(mockedResponse);

        var sink = new MSTeamsSink();

        assertThrows(SpRuntimeException.class,
                () -> sink.sendPayloadToWebhook(mockedClient, "payload", new URI("https://webhook.com")));

        // After exhausting all attempts the call must have been made MAX_ATTEMPTS
        // times.
        verify(mockedClient, times(3)).execute(any());
    }

    @Test
    public void sendPayloadToWebhookRecoversAfterTransientFailure() throws IOException {
        var mockedClient = mock(CloseableHttpClient.class);
        var failResponse = mock(CloseableHttpResponse.class);
        var failStatusLine = mock(StatusLine.class);
        var okResponse = mock(CloseableHttpResponse.class);
        var okStatusLine = mock(StatusLine.class);

        when(failStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_SERVICE_UNAVAILABLE);
        when(failResponse.getStatusLine()).thenReturn(failStatusLine);
        when(okStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(okResponse.getStatusLine()).thenReturn(okStatusLine);

        // First call fails with 503, second call succeeds.
        when(mockedClient.execute(any())).thenReturn(failResponse).thenReturn(okResponse);

        var sink = new MSTeamsSink();

        assertDoesNotThrow(() -> sink.sendPayloadToWebhook(mockedClient, "payload", new URI("https://webhook.com")));

        // One failed attempt + one successful retry = two executions, no third.
        verify(mockedClient, times(2)).execute(any());
    }

    @Test
    public void validateWebhookUrl() {
        var sink = new MSTeamsSink();
        assertThrows(SpRuntimeException.class, () -> sink.validateWebhookUrl(""));
        assertThrows(SpRuntimeException.class, () -> sink.validateWebhookUrl("some-string"));
    }
}