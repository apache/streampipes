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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class TestMSTeamsSink {

  @Test
  public void createMessageFromSimpleContent() {
    var messageContent = "This is test";
    var sink = new MSTeamsSink();

    assertEquals(MSTeamsSink.SIMPLE_MESSAGE_TEMPLATE.formatted(messageContent),
            sink.createMessageFromSimpleContent(messageContent));
  }

  @Test
  public void createMessageFromAdvancedContent() {
    var messageContent = "{\"text\": \"Hi this is a message from Apache StreamPipes\"}";

    var sink = new MSTeamsSink();
    Assertions.assertEquals(messageContent, sink.createMessageFromAdvancedContent(messageContent));
  }

  @Test
  public void createMessageFromAdvancedContentCheckException() {
    var messageContent = "invalid-complex-input";

    var sink = new MSTeamsSink();

    assertThrows(SpRuntimeException.class, () -> sink.createMessageFromAdvancedContent(messageContent));
  }

  @Test
  public void sendPayloadToWebhook() throws IOException {

    var mockedClient = mock(HttpClient.class);
    var mockedResponse = mock(CloseableHttpResponse.class);
    var mockedStatusLine = mock(StatusLine.class);
    var argumentCaptor = ArgumentCaptor.forClass(HttpPost.class);

    when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
    when(mockedClient.execute(any())).thenReturn(mockedResponse);

    var payload = "This is a test";
    var webhook = "https://webhook.com";
    var sink = new MSTeamsSink();

    sink.sendPayloadToWebhook(mockedClient, payload, webhook);
    verify(mockedClient, times(1)).execute(argumentCaptor.capture());

    var capturedPost = argumentCaptor.getValue();

    Assertions.assertNotNull(capturedPost);
    Assertions.assertEquals(webhook, capturedPost.getURI().toString());
    Assertions.assertEquals(ContentType.APPLICATION_JSON.toString(),
            capturedPost.getEntity().getContentType().getValue());
    Assertions.assertEquals(payload, EntityUtils.toString(capturedPost.getEntity()));
  }

  @Test
  public void sendPayloadToWebhookBadResponse() throws IOException {
    var mockedClient = mock(HttpClient.class);
    var mockedResponse = mock(CloseableHttpResponse.class);
    var mockedStatusLine = mock(StatusLine.class);

    when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
    when(mockedResponse.getStatusLine()).thenReturn(mockedStatusLine);
    when(mockedClient.execute(any())).thenReturn(mockedResponse);

    var sink = new MSTeamsSink();
    var payload = "<a>invalid</a>";
    var url = "https://webhook.com";

    assertThrows(SpRuntimeException.class, () -> sink.sendPayloadToWebhook(mockedClient, payload, url));
  }

  @Test
  public void validateWebhookUrl() {
    var sink = new MSTeamsSink();
    assertThrows(SpRuntimeException.class, () -> sink.validateWebhookUrl(""));
    assertThrows(SpRuntimeException.class, () -> sink.validateWebhookUrl("some-string"));
  }
}
