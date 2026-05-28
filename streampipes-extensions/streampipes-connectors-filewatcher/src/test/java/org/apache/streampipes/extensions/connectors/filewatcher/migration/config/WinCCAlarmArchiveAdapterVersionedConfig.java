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

package org.apache.streampipes.extensions.connectors.filewatcher.migration.config;

import org.apache.streampipes.extensions.connectors.filewatcher.adapter.WinCCAlarmArchiveAdapter;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;

public class WinCCAlarmArchiveAdapterVersionedConfig {

  private static final String DIRECTORY_PATH = "directory-path";
  private static final String ARCHIVE_BASE_NAME = "archive-base-name";
  private static final String SEGMENTED_CIRCULAR_LOG_ENABLED = "segmented-circular-log-enabled";
  private static final String ARCHIVE_SEGMENT_COUNT = "archive-segment-count";
  private static final String POLL_INTERVAL_SECONDS = "poll-interval-seconds";
  private static final String INTER_EVENT_DELAY_MS = "inter-event-delay-ms";
  private static final String TIMEZONE_ID = "timezone-id";
  private static final String SEGMENTED_CIRCULAR_LOG_ON = "segmented-circular-log-on";
  private static final String SEGMENTED_CIRCULAR_LOG_OFF = "segmented-circular-log-off";

  public static AdapterDescription getWinCCAlarmArchiveAdapterDescriptionV0() {
    return AdapterConfigurationBuilder
        .create(WinCCAlarmArchiveAdapter.ID, 0, WinCCAlarmArchiveAdapter::new)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .withLocales(Locales.EN)
        .requiredTextParameter(Labels.withId(DIRECTORY_PATH))
        .requiredTextParameter(Labels.withId(ARCHIVE_BASE_NAME))
        .requiredSingleValueSelection(
            Labels.withId(SEGMENTED_CIRCULAR_LOG_ENABLED),
            Options.from(
                new Tuple2<>("Enabled", SEGMENTED_CIRCULAR_LOG_ON),
                new Tuple2<>("Disabled", SEGMENTED_CIRCULAR_LOG_OFF)
            )
        )
        .requiredIntegerParameter(Labels.withId(ARCHIVE_SEGMENT_COUNT))
        .requiredIntegerParameter(Labels.withId(POLL_INTERVAL_SECONDS))
        .requiredIntegerParameter(Labels.withId(INTER_EVENT_DELAY_MS), 0)
        .requiredTextParameter(Labels.withId(TIMEZONE_ID), "UTC")
        .buildConfiguration()
        .getAdapterDescription();
  }
}
