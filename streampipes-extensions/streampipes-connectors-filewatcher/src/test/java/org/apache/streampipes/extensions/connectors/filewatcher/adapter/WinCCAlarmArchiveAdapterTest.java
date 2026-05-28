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

package org.apache.streampipes.extensions.connectors.filewatcher.adapter;

import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileWatcherConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WinCCAlarmArchiveAdapterTest {

  @TempDir
  Path tempDir;

  @Test
  void shouldUseConfiguredStartIndexForSegmentedPattern() throws Exception {
    var config = invokeToConfig("segmented-circular-log-on", 3, 0);

    assertTrue(config.filePattern().matcher("Meldungsarchiv0.csv").matches());
    assertTrue(config.filePattern().matcher("Meldungsarchiv2.csv").matches());
    assertFalse(config.filePattern().matcher("Meldungsarchiv3.csv").matches());
  }

  @Test
  void shouldUseConfiguredStartIndexForSingleFilePattern() throws Exception {
    var config = invokeToConfig("segmented-circular-log-off", 3, 0);

    assertTrue(config.filePattern().matcher("Meldungsarchiv0.csv").matches());
    assertFalse(config.filePattern().matcher("Meldungsarchiv1.csv").matches());
  }

  private FileWatcherConfig invokeToConfig(String segmentedCircularLogMode,
                                           int segmentCount,
                                           int startIndex) throws Exception {
    WinCCAlarmArchiveAdapter adapter = new WinCCAlarmArchiveAdapter();
    Map<String, Object> values = Map.of(
        "directory-path", tempDir.toString(),
        "archive-base-name", "Meldungsarchiv",
        "archive-segment-count", segmentCount,
        "archive-segment-start-index", startIndex,
        "poll-interval-seconds", 1,
        "inter-event-delay-ms", 0,
        "timezone-id", "UTC"
    );

    IStaticPropertyExtractor staticPropertyExtractor = (IStaticPropertyExtractor) Proxy.newProxyInstance(
        IStaticPropertyExtractor.class.getClassLoader(),
        new Class<?>[]{IStaticPropertyExtractor.class},
        (proxy, method, args) -> {
          if ("singleValueParameter".equals(method.getName())) {
            return values.get(args[0]);
          }
          if ("selectedSingleValueInternalName".equals(method.getName())) {
            return segmentedCircularLogMode;
          }
          throw new UnsupportedOperationException("Unexpected extractor method: " + method.getName());
        }
    );

    IAdapterParameterExtractor extractor = (IAdapterParameterExtractor) Proxy.newProxyInstance(
        IAdapterParameterExtractor.class.getClassLoader(),
        new Class<?>[]{IAdapterParameterExtractor.class},
        (proxy, method, args) -> {
          if ("getStaticPropertyExtractor".equals(method.getName())) {
            return staticPropertyExtractor;
          }
          if ("getAdapterDescription".equals(method.getName())) {
            return null;
          }
          throw new UnsupportedOperationException("Unexpected adapter extractor method: " + method.getName());
        }
    );

    Method toConfigMethod = WinCCAlarmArchiveAdapter.class.getDeclaredMethod(
        "toConfig",
        IAdapterParameterExtractor.class
    );
    toConfigMethod.setAccessible(true);

    return (FileWatcherConfig) toConfigMethod.invoke(adapter, extractor);
  }
}
