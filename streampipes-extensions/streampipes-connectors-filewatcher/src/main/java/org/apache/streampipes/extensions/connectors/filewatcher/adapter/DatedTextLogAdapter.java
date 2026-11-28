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
import org.apache.streampipes.extensions.connectors.filewatcher.runtime.FileSetWatcher;
import org.apache.streampipes.extensions.connectors.filewatcher.runtime.FileWatcherCheckpointStore;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.SampleDataBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class DatedTextLogAdapter implements StreamPipesAdapter, IPullAdapter {

  public static final String ID = "org.apache.streampipes.connectors.dated.text.log";
  private PullAdapterScheduler scheduler;
  private PollingSettings polling;
  private FileSetWatcher watcher;
  private String adapterId;
  private IEventCollector collector;

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, 0, DatedTextLogAdapter::new)
        .withAssets(ExtensionAssetType.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredTextParameter(Labels.withId("directory"))
        .requiredTextParameter(Labels.withId("filename-pattern"), DatedTextLogReader.DEFAULT_FILE_PATTERN)
        .requiredTextParameter(Labels.withId("date-format"), "uuuu_MM_dd")
        .requiredTextParameter(Labels.withId("line-pattern"), DatedTextLogReader.DEFAULT_LINE_PATTERN)
        .requiredTextParameter(Labels.withId("fields"), DatedTextLogReader.DEFAULT_FIELDS)
        .requiredTextParameter(Labels.withId("time-format"), "HH:mm:ss")
        .requiredTextParameter(Labels.withId("charset"), "UTF-16LE")
        .requiredTextParameter(Labels.withId("timezone"), ZoneId.systemDefault().getId())
        .requiredIntegerParameter(Labels.withId("poll-interval-seconds"), 5)
        .buildConfiguration();
  }

  private record Settings(Path directory, DatedTextLogReader reader, int interval) {
  }

  private Settings settings(IAdapterParameterExtractor extractor) throws AdapterException {
    var params = extractor.getStaticPropertyExtractor();
    try {
      var directory = Path.of(params.singleValueParameter("directory", String.class));
      if (!Files.isDirectory(directory)) {
        throw new AdapterException("Log directory does not exist: " + directory);
      }
      int interval = params.singleValueParameter("poll-interval-seconds", Integer.class);
      if (interval < 1) {
        throw new AdapterException("Polling interval must be positive.");
      }
      var reader = new DatedTextLogReader(params.singleValueParameter("filename-pattern", String.class),
          params.singleValueParameter("date-format", String.class), params.singleValueParameter("line-pattern", String.class),
          params.singleValueParameter("fields", String.class), params.singleValueParameter("time-format", String.class),
          params.singleValueParameter("charset", String.class), params.singleValueParameter("timezone", String.class));
      return new Settings(directory, reader, interval);
    } catch (IllegalArgumentException | DateTimeException e) {
      throw new AdapterException("Invalid dated text log configuration: " + e.getMessage(), e);
    }
  }

  @Override
  public SampleData onSampleDataRequested(IAdapterParameterExtractor extractor, IAdapterGuessSchemaContext context)
      throws AdapterException {
    var settings = settings(extractor);
    var reader = settings.reader();
    try (var paths = Files.list(settings.directory())) {
      var matching = paths.filter(Files::isRegularFile)
          .filter(path -> reader.filePattern.matcher(path.getFileName().toString()).matches()).sorted().toList();
      for (Path file : matching) {
        var sample = reader.sample(file);
        if (sample.isPresent()) {
          return SampleDataBuilder.create().sample(sample.get()).build();
        }
      }
      throw new AdapterException("No complete log records found in matching files in " + settings.directory());
    } catch (IOException | IllegalArgumentException e) {
      throw new AdapterException("Could not sample dated text log: " + e.getMessage(), e);
    }
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor, IEventCollector collector,
                               IAdapterRuntimeContext context) throws AdapterException {
    var settings = settings(extractor);
    var reader = settings.reader();
    watcher = new FileSetWatcher(settings.directory(), reader.filePattern, name -> reader.date(name).toEpochDay(),
        reader, new FileWatcherCheckpointStore());
    adapterId = extractor.getAdapterDescription().getElementId();
    this.collector = collector;
    polling = PollingSettings.from(TimeUnit.SECONDS, settings.interval());
    scheduler = new PullAdapterScheduler();
    scheduler.schedule(this, adapterId);
  }

  @Override
  public void pullData() {
    try {
      watcher.poll(adapterId, collector);
    } catch (IOException e) {
      throw new CompletionException("Could not poll dated text logs", e);
    }
  }

  @Override
  public PollingSettings getPollingInterval() {
    return polling;
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext context) {
    if (scheduler != null) {
      scheduler.shutdown();
    }
  }
}
