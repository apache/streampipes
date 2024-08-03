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

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class RateLimitedRuntimeInfoProvider {

  private static final int MAX_PREVIEW_TIME_CYCLES = 360;
  private static final int MAX_FREQUENCY = 500;

  private final DataStreamRuntimeInfoProvider runtimeInfoProvider;

  public RateLimitedRuntimeInfoProvider(DataStreamRuntimeInfoProvider runtimeInfoProvider) {
    this.runtimeInfoProvider = runtimeInfoProvider;
  }

  public void streamOutput(OutputStream outputStream) {
    runtimeInfoProvider.startConsuming();
    try {
      for (int i = 0; i < MAX_PREVIEW_TIME_CYCLES; i++) {
        String message = runtimeInfoProvider.getLatestEvent();
        if (message != null) {
          try {
            outputStream.write((message + "\n").getBytes());
            outputStream.flush();
          } catch (IOException ignored) {
          }
        }
        TimeUnit.MILLISECONDS.sleep(MAX_FREQUENCY);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      closeResources(runtimeInfoProvider);
    }
  }

  private void closeResources(DataStreamRuntimeInfoProvider fetcher) {
    fetcher.close();
  }
}
