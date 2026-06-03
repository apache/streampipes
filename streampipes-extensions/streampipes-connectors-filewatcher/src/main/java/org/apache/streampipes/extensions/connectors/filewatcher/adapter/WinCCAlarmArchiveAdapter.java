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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.connectors.filewatcher.model.CsvParserSettings;
import org.apache.streampipes.extensions.connectors.filewatcher.model.FileWatcherConfig;
import org.apache.streampipes.extensions.connectors.filewatcher.runtime.CsvFileReader;
import org.apache.streampipes.extensions.connectors.filewatcher.runtime.FileSetWatcher;
import org.apache.streampipes.extensions.connectors.filewatcher.runtime.FileWatcherCheckpointStore;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.parser.CsvParser;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.Tuple2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class WinCCAlarmArchiveAdapter implements StreamPipesAdapter, IPullAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(WinCCAlarmArchiveAdapter.class);

  public static final String ID = "org.apache.streampipes.connectors.wincc.alarm.archive";

  private static final String DIRECTORY_PATH = "directory-path";
  private static final String ARCHIVE_BASE_NAME = "archive-base-name";
  private static final String SEGMENTED_CIRCULAR_LOG_ENABLED = "segmented-circular-log-enabled";
  private static final String ARCHIVE_SEGMENT_COUNT = "archive-segment-count";
  public static final String ARCHIVE_SEGMENT_START_INDEX = "archive-segment-start-index";
  public static final String CONSIDER_LAST_MODIFIED = "consider-last-modified";
  private static final String POLL_INTERVAL_SECONDS = "poll-interval-seconds";
  private static final String INTER_EVENT_DELAY_MS = "inter-event-delay-ms";
  private static final String TIMEZONE_ID = "timezone-id";

  private static final String SEGMENTED_CIRCULAR_LOG_ON = "segmented-circular-log-on";
  private static final String SEGMENTED_CIRCULAR_LOG_OFF = "segmented-circular-log-off";
  private static final CsvParserSettings WINCC_CSV_SETTINGS = new CsvParserSettings(true, ';');

  private PullAdapterScheduler scheduler;
  private FileSetWatcher watcher;
  private String adapterElementId;
  private IEventCollector collector;
  private PollingSettings pollingSettings;

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, 2, WinCCAlarmArchiveAdapter::new)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .withLocales(Locales.EN)
        .requiredTextParameter(Labels.withId(
            DIRECTORY_PATH
        ))
        .requiredTextParameter(Labels.withId(
            ARCHIVE_BASE_NAME
        ))
        .requiredSingleValueSelection(
            Labels.withId(
                SEGMENTED_CIRCULAR_LOG_ENABLED
            ),
            Options.from(
                new Tuple2<>("Enabled", SEGMENTED_CIRCULAR_LOG_ON),
                new Tuple2<>("Disabled", SEGMENTED_CIRCULAR_LOG_OFF)
            )
        )
        .requiredIntegerParameter(Labels.withId(
            ARCHIVE_SEGMENT_COUNT
        ))
        .requiredIntegerParameter(Labels.withId(
            ARCHIVE_SEGMENT_START_INDEX
        ), 0)
        .requiredSlideToggle(Labels.withId(
            CONSIDER_LAST_MODIFIED
        ), true)
        .requiredIntegerParameter(Labels.withId(
            POLL_INTERVAL_SECONDS
        ))
        .requiredIntegerParameter(Labels.withId(
            INTER_EVENT_DELAY_MS
        ), 0)
        .requiredTextParameter(Labels.withId(
            TIMEZONE_ID
        ), ZoneId.systemDefault().getId())
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    FileWatcherConfig config = toConfig(extractor);
    this.adapterElementId = extractor.getAdapterDescription().getElementId();
    this.collector = collector;
    this.pollingSettings = PollingSettings.from(TimeUnit.SECONDS, config.pollIntervalSeconds());
    this.watcher = new FileSetWatcher(
        config,
        new FileWatcherCheckpointStore(),
        new CsvFileReader(),
        new WinCCAlarmEventMapper(config.timeZone())
    );
    LOG.debug(
        "Starting WinCC alarm archive adapter '{}': directory='{}', pattern='{}', pollIntervalSeconds={}, "
            + "singleFileGrowthMode={}, interEventDelayMs={}, timeZone='{}'.",
        adapterElementId,
        config.directory(),
        config.filePattern().pattern(),
        config.pollIntervalSeconds(),
        config.singleFileGrowthMode(),
        config.interEventDelayMs(),
        config.timeZone().getId()
    );
    this.scheduler = new PullAdapterScheduler();
    this.scheduler.schedule(this, adapterElementId);
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    if (scheduler != null) {
      scheduler.shutdown();
    }
  }

  @Override
  public SampleData onSampleDataRequested(IAdapterParameterExtractor extractor,
                                          IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    FileWatcherConfig config = toConfig(extractor);
    try (var files = Files.list(config.directory())) {
      Path sampleFile = files
          .filter(Files::isRegularFile)
          .filter(path -> config.filePattern().matcher(path.getFileName().toString()).matches())
          .sorted()
          .findFirst()
        .orElseThrow(() -> new AdapterException("No matching WinCC alarm archive files found in " + config.directory()));

      try (var inputStream = Files.newInputStream(sampleFile)) {
        Map<String, Object> rawSample = new CsvParser(true, ';').getSampleData(inputStream).getSamples().get(0);
        return org.apache.streampipes.sdk.builder.adapter.SampleDataBuilder.create()
            .sample(new WinCCAlarmEventMapper(config.timeZone()).map(rawSample))
            .build();
      }
    } catch (IOException e) {
      throw new AdapterException("Could not load sample data from directory " + config.directory(), e);
    }
  }

  @Override
  public void pullData() {
    try {
      watcher.poll(adapterElementId, collector);
    } catch (IOException | RuntimeException e) {
      throw new CompletionException("Could not poll files from WinCC alarm archive adapter", e);
    }
  }

  @Override
  public PollingSettings getPollingInterval() {
    return pollingSettings;
  }

  private FileWatcherConfig toConfig(IAdapterParameterExtractor extractor) throws AdapterException {
    var staticPropertyExtractor = extractor.getStaticPropertyExtractor();
    var directory = Path.of(staticPropertyExtractor.singleValueParameter(DIRECTORY_PATH, String.class));
    if (!Files.isDirectory(directory)) {
      throw new AdapterException("Configured directory does not exist or is not a directory: " + directory);
    }

    var baseName = normalizeBaseName(staticPropertyExtractor.singleValueParameter(ARCHIVE_BASE_NAME, String.class));
    var segmentedCircularLogMode =
        staticPropertyExtractor.selectedSingleValueInternalName(SEGMENTED_CIRCULAR_LOG_ENABLED, String.class);
    var segmentCount = staticPropertyExtractor.singleValueParameter(ARCHIVE_SEGMENT_COUNT, Integer.class);
    var segmentStartIndex = staticPropertyExtractor.singleValueParameter(ARCHIVE_SEGMENT_START_INDEX, Integer.class);
    var considerLastModified = staticPropertyExtractor.slideToggleValue(CONSIDER_LAST_MODIFIED);
    var interval = staticPropertyExtractor.singleValueParameter(POLL_INTERVAL_SECONDS, Integer.class);
    var interEventDelayMs = staticPropertyExtractor.singleValueParameter(INTER_EVENT_DELAY_MS, Integer.class);
    var timeZone = parseTimeZone(staticPropertyExtractor.singleValueParameter(TIMEZONE_ID, String.class));
    if (interEventDelayMs < 0) {
      throw new AdapterException("Inter-event delay must be greater than or equal to 0.");
    }
    if (segmentStartIndex < 0) {
      throw new AdapterException("Segment start index must be greater than or equal to 0.");
    }

    Pattern filePattern = SEGMENTED_CIRCULAR_LOG_ON.equals(segmentedCircularLogMode)
        ? buildSegmentPattern(baseName, segmentStartIndex, segmentCount)
        : buildSingleSegmentPattern(baseName, segmentStartIndex);

    return new FileWatcherConfig(
        directory,
        filePattern,
        WINCC_CSV_SETTINGS,
        interval,
        SEGMENTED_CIRCULAR_LOG_OFF.equals(segmentedCircularLogMode),
        considerLastModified,
        interEventDelayMs,
        timeZone
    );
  }

  private ZoneId parseTimeZone(String configuredTimeZone) throws AdapterException {
    try {
      return ZoneId.of(configuredTimeZone.trim());
    } catch (ZoneRulesException | NullPointerException e) {
      throw new AdapterException("Configured timezone is invalid: " + configuredTimeZone, e);
    }
  }

  private String normalizeBaseName(String configuredBaseName) {
    String trimmedBaseName = configuredBaseName.trim();
    if (trimmedBaseName.endsWith(".csv")) {
      return trimmedBaseName.substring(0, trimmedBaseName.length() - 4);
    }

    return trimmedBaseName;
  }

  private Pattern buildSingleSegmentPattern(String baseName, int segmentStartIndex) {
    return Pattern.compile(Pattern.quote(baseName) + segmentStartIndex + "\\.csv", Pattern.CASE_INSENSITIVE);
  }

  private Pattern buildSegmentPattern(String baseName,
                                      int segmentStartIndex,
                                      int segmentCount) throws AdapterException {
    if (segmentCount < 1) {
      throw new AdapterException("Segment count must be at least 1 when the segmented circular log is enabled.");
    }

    return Pattern.compile(
        Pattern.quote(baseName) + "(" + buildAllowedSegments(segmentStartIndex, segmentCount) + ")\\.csv",
        Pattern.CASE_INSENSITIVE
    );
  }

  private String buildAllowedSegments(int segmentStartIndex, int segmentCount) {
    StringBuilder builder = new StringBuilder();
    int segmentEndExclusive = segmentStartIndex + segmentCount;
    for (int i = segmentStartIndex; i < segmentEndExclusive; i++) {
      if (i > segmentStartIndex) {
        builder.append("|");
      }
      builder.append(i);
    }

    return builder.toString();
  }
}
