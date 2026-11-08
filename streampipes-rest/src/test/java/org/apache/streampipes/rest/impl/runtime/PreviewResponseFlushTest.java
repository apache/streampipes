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

package org.apache.streampipes.rest.impl.runtime;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.preview.PipelinePreview;
import org.apache.streampipes.manager.runtime.DataStreamRuntimeInfoProvider;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.resource.management.SpResourceManager;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PreviewResponseFlushTest {

  @Test
  void streamPreviewFlushesEachMessageBeforeStreamingCompletes() throws Exception {
    assertPreviewFlushes(response -> new StreamPreviewResource().getRuntimeInfo(response, new SpDataStream()));
  }

  @Test
  void pipelinePreviewFlushesEachMessageBeforeStreamingCompletes() throws Exception {
    try (var previews = mockConstruction(PipelinePreview.class, (preview, context) ->
        when(preview.getPipelineElementPreviewStreams("preview-id")).thenReturn(Map.of()))) {
      var resource = new PipelinePreviewResource(
          mock(ExtensionServiceRequestManager.class),
          mock(SpResourceManager.class)
      );
      assertPreviewFlushes(response -> resource.getPipelinePreviewResult(response, "preview-id"));
    }
  }

  private void assertPreviewFlushes(Function<HttpServletResponse, StreamingResponseBody> createBody) throws Exception {
    var response = mock(HttpServletResponse.class);
    var servletOutput = mock(ServletOutputStream.class);
    var bufferedBytes = new ByteArrayOutputStream();
    var deliveredBytes = new ByteArrayOutputStream();
    when(response.getOutputStream()).thenReturn(servletOutput);
    doAnswer(invocation -> {
      bufferedBytes.write(invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2));
      return null;
    }).when(servletOutput).write(any(byte[].class), anyInt(), anyInt());
    doAnswer(invocation -> {
      bufferedBytes.writeTo(deliveredBytes);
      bufferedBytes.reset();
      return null;
    }).when(response).flushBuffer();

    var reads = new AtomicInteger();
    var expectedMessage = "{\"adapter\":{\"value\":42}}\n";
    try (var fetchers = mockConstruction(DataStreamRuntimeInfoProvider.class, (fetcher, context) ->
        when(fetcher.getLatestEvents()).thenAnswer(invocation -> {
          int previousMessages = reads.getAndIncrement();
          // Verify delivery while the streaming loop is still running, not just its final flush.
          assertEquals(expectedMessage.repeat(previousMessages), deliveredBytes.toString(StandardCharsets.UTF_8));
          if (previousMessages == 2) {
            throw new PreviewFinishedException();
          }
          return Map.of("adapter", Map.of("value", 42));
        }))) {
      var body = createBody.apply(response);
      // Use the same Spring wrapper as StreamingResponseBodyReturnValueHandler.
      var output = new ServletServerHttpResponse(response).getBody();
      assertThrows(PreviewFinishedException.class, () -> body.writeTo(output));
      verify(response, times(2)).flushBuffer();
      verify(fetchers.constructed().getFirst()).close();
    }
  }

  private static class PreviewFinishedException extends RuntimeException {
  }
}
