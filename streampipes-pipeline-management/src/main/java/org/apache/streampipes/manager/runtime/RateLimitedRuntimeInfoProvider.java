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

package org.apache.streampipes.manager.runtime;

import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class RateLimitedRuntimeInfoProvider {

  private static final int MAX_PREVIEW_TIME_CYCLES = 360;
  private static final int MAX_FREQUENCY = 500;

  private final DataStreamRuntimeInfoProvider runtimeInfoProvider;
  private final ObjectMapper objectMapper;
  private final ClientDisconnectCallback callback;
  private final Runnable expiryCallback;
  private final BooleanSupplier active;
  private final int previewCycles;
  private final long intervalMillis;


  public RateLimitedRuntimeInfoProvider(DataStreamRuntimeInfoProvider runtimeInfoProvider,
                                        ClientDisconnectCallback callback) {
    this(runtimeInfoProvider, callback, callback::onClientDisconnect, () -> true);
  }

  /**
   * @param callback       called when the client disconnects, streaming fails or {@code active} turns false
   * @param expiryCallback called instead of {@code callback} when the stream ends because it reached its
   *                       maximum duration, so that the client may re-attach to the same source
   * @param active         checked before each cycle; streaming ends as soon as it returns false,
   *                       e.g. after the preview was stopped explicitly
   */
  public RateLimitedRuntimeInfoProvider(DataStreamRuntimeInfoProvider runtimeInfoProvider,
                                        ClientDisconnectCallback callback,
                                        Runnable expiryCallback,
                                        BooleanSupplier active) {
    this(runtimeInfoProvider, callback, expiryCallback, active, MAX_PREVIEW_TIME_CYCLES, MAX_FREQUENCY);
  }

  RateLimitedRuntimeInfoProvider(DataStreamRuntimeInfoProvider runtimeInfoProvider,
                                ClientDisconnectCallback callback,
                                int previewCycles,
                                long intervalMillis) {
    this(runtimeInfoProvider, callback, callback::onClientDisconnect, () -> true, previewCycles, intervalMillis);
  }

  RateLimitedRuntimeInfoProvider(DataStreamRuntimeInfoProvider runtimeInfoProvider,
                                ClientDisconnectCallback callback,
                                Runnable expiryCallback,
                                BooleanSupplier active,
                                int previewCycles,
                                long intervalMillis) {
    this.expiryCallback = expiryCallback;
    this.active = active;
    this.previewCycles = previewCycles;
    this.intervalMillis = intervalMillis;
    this.runtimeInfoProvider = runtimeInfoProvider;
    this.objectMapper = JacksonSerializer.getObjectMapper(Map.of(
      DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true,
      SerializationFeature.INDENT_OUTPUT, false
    ));
    this.callback = callback;
  }

  public void streamOutput(OutputStream outputStream) {
    streamOutput(outputStream, outputStream::flush);
  }

  public void streamOutput(OutputStream outputStream, Flushable responseFlusher) {
    var expired = false;
    try {
      runtimeInfoProvider.startConsuming();
      for (int i = 0; i < previewCycles; i++) {
        if (!active.getAsBoolean()) {
          return;
        }
        var messages = runtimeInfoProvider.getLatestEvents();
        if (!messages.isEmpty()) {
          try {
            outputStream.write((objectMapper.writeValueAsString(messages) + "\n").getBytes(StandardCharsets.UTF_8));
            // Streaming HTTP output streams may suppress flush(); flush the response explicitly.
            responseFlusher.flush();
          } catch (IOException ignored) {
            return;
          }
        }
        TimeUnit.MILLISECONDS.sleep(intervalMillis);
      }
      expired = true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      try {
        runtimeInfoProvider.close();
      } finally {
        if (expired) {
          expiryCallback.run();
        } else {
          callback.onClientDisconnect();
        }
      }
    }
  }

}
