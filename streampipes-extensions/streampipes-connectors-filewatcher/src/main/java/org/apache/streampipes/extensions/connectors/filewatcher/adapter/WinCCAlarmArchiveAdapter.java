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
  private static final String POLL_INTERVAL_SECONDS = "poll-interval-seconds";
  private static final String INTER_EVENT_DELAY_MS = "inter-event-delay-ms";

  private static final String SEGMENTED_CIRCULAR_LOG_ON = "segmented-circular-log-on";
  private static final String SEGMENTED_CIRCULAR_LOG_OFF = "segmented-circular-log-off";
  private static final CsvParserSettings WINCC_CSV_SETTINGS = new CsvParserSettings(true, ';');
  private static final WinCCAlarmEventMapper EVENT_MAPPER = new WinCCAlarmEventMapper();

  private PullAdapterScheduler scheduler;
  private FileSetWatcher watcher;
  private String adapterElementId;
  private IEventCollector collector;
  private PollingSettings pollingSettings;

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, 0, WinCCAlarmArchiveAdapter::new)
        .withAssets(ExtensionAssetType.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredTextParameter(Labels.from(
            DIRECTORY_PATH,
            "Archive directory",
            "Absolute path to the WinCC archive directory that contains the exported alarm CSV files."
        ))
        .requiredTextParameter(Labels.from(
            ARCHIVE_BASE_NAME,
            "Archive base name",
            "Base file name of the WinCC alarm archive, for example Meldungsarchiv."
        ))
        .requiredSingleValueSelection(
            Labels.from(
                SEGMENTED_CIRCULAR_LOG_ENABLED,
                "Segmented circular log",
                "Whether WinCC writes the alarm archive as a segmented circular log with rotating CSV segments."
            ),
            Options.from(
                new Tuple2<>("Enabled", SEGMENTED_CIRCULAR_LOG_ON),
                new Tuple2<>("Disabled", SEGMENTED_CIRCULAR_LOG_OFF)
            )
        )
        .requiredIntegerParameter(Labels.from(
            ARCHIVE_SEGMENT_COUNT,
            "Segment count",
            "Number of archive segments in segmented circular log mode. Ignored when the segmented circular log is disabled."
        ))
        .requiredIntegerParameter(Labels.from(
            POLL_INTERVAL_SECONDS,
            "Polling interval (seconds)",
            "How often the WinCC archive files should be scanned for new alarm entries."
        ))
        .requiredIntegerParameter(Labels.from(
            INTER_EVENT_DELAY_MS,
            "Inter-event delay (ms)",
            "Delay between replayed alarm events in milliseconds. Use 0 to disable throttling."
        ), 0)
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
        EVENT_MAPPER
    );
    LOG.debug(
        "Starting WinCC alarm archive adapter '{}': directory='{}', pattern='{}', pollIntervalSeconds={}, "
            + "singleFileGrowthMode={}, interEventDelayMs={}.",
        adapterElementId,
        config.directory(),
        config.filePattern().pattern(),
        config.pollIntervalSeconds(),
        config.singleFileGrowthMode(),
        config.interEventDelayMs()
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
            .sample(EVENT_MAPPER.map(rawSample))
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
    var interval = staticPropertyExtractor.singleValueParameter(POLL_INTERVAL_SECONDS, Integer.class);
    var interEventDelayMs = staticPropertyExtractor.singleValueParameter(INTER_EVENT_DELAY_MS, Integer.class);
    if (interEventDelayMs < 0) {
      throw new AdapterException("Inter-event delay must be greater than or equal to 0.");
    }

    Pattern filePattern = SEGMENTED_CIRCULAR_LOG_ON.equals(segmentedCircularLogMode)
        ? buildSegmentPattern(baseName, segmentCount)
        : buildSingleSegmentPattern(baseName);

    return new FileWatcherConfig(
        directory,
        filePattern,
        WINCC_CSV_SETTINGS,
        interval,
        SEGMENTED_CIRCULAR_LOG_OFF.equals(segmentedCircularLogMode),
        interEventDelayMs
    );
  }

  private String normalizeBaseName(String configuredBaseName) {
    String trimmedBaseName = configuredBaseName.trim();
    if (trimmedBaseName.endsWith(".csv")) {
      return trimmedBaseName.substring(0, trimmedBaseName.length() - 4);
    }

    return trimmedBaseName;
  }

  private Pattern buildSingleSegmentPattern(String baseName) {
    return Pattern.compile(Pattern.quote(baseName) + "1\\.csv", Pattern.CASE_INSENSITIVE);
  }

  private Pattern buildSegmentPattern(String baseName, int segmentCount) throws AdapterException {
    if (segmentCount < 1) {
      throw new AdapterException("Segment count must be at least 1 when the segmented circular log is enabled.");
    }

    return Pattern.compile(
        Pattern.quote(baseName) + "(" + buildAllowedSegments(segmentCount) + ")\\.csv",
        Pattern.CASE_INSENSITIVE
    );
  }

  private String buildAllowedSegments(int segmentCount) {
    StringBuilder builder = new StringBuilder();
    for (int i = 1; i <= segmentCount; i++) {
      if (i > 1) {
        builder.append("|");
      }
      builder.append(i);
    }

    return builder.toString();
  }
}
