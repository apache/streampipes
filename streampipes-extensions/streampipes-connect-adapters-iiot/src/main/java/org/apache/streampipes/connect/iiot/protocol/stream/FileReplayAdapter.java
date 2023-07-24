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

package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.iiot.utils.FileProtocolUtils;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.CsvParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.ImageParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.JsonParsers;
import org.apache.streampipes.extensions.management.connect.adapter.parser.xml.XmlParser;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileReplayAdapter implements StreamPipesAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(FileReplayAdapter.class);

  private static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.file";
  private static final String REPLACE_TIMESTAMP = "replaceTimestamp";
  private static final String SPEED = "speed";
  private static final String FILE_PATH = "filePath";
  private static final String REPLAY_ONCE = "replayOnce";

  private static final String SPEED_UP = "speedUp";
  private static final String KEEP_ORIGINAL_TIME = "keepOriginalTime";
  private static final String SPEED_UP_FACTOR = "speedUpFactor";
  private static final String FASTEST = "fastest";

  private static final String SPEED_UP_FACTOR_GROUP = "speed-up-factor-group";


  private ScheduledExecutorService executor;
  private boolean replaceTimestamp;
  private String timestampRuntimeName;

  private float speedUp;

  private long timestampLastEvent = -1;

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, FileReplayAdapter::new)
        .withSupportedParsers(
            new JsonParsers(),
            new CsvParser(),
            new XmlParser(),
            new ImageParser())
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic)
        .requiredFile(Labels.withId(FILE_PATH), Filetypes.CSV, Filetypes.JSON, Filetypes.XML)
        .requiredMultiValueSelection(Labels.withId(REPLACE_TIMESTAMP), Options.from(""))
        .requiredSingleValueSelection(Labels.withId(REPLAY_ONCE), Options.from("no", "yes"))
        .requiredAlternatives(Labels.withId(SPEED),
            Alternatives.from(Labels.withId(KEEP_ORIGINAL_TIME), true),
            Alternatives.from(Labels.withId(FASTEST)), Alternatives.from(Labels.withId(SPEED_UP_FACTOR),
                StaticProperties.group(Labels.withId(SPEED_UP_FACTOR_GROUP),
                    StaticProperties.doubleFreeTextProperty(Labels.withId(SPEED_UP)))))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {

    // extract user input
    executor = Executors.newScheduledThreadPool(1);
    boolean replayOnce = extractor
        .getStaticPropertyExtractor()
        .selectedSingleValue(REPLAY_ONCE, String.class)
        .equals("yes");
    var replaceTimestampStringList = extractor
        .getStaticPropertyExtractor()
        .selectedMultiValues(REPLACE_TIMESTAMP, String.class);
    replaceTimestamp = replaceTimestampStringList.size() != 0;
    var speedUpAlternative = extractor
        .getStaticPropertyExtractor()
        .selectedAlternativeInternalId(SPEED);
    speedUp = switch (speedUpAlternative) {
      case FASTEST -> Float.MAX_VALUE;
      case SPEED_UP_FACTOR -> extractor
          .getStaticPropertyExtractor()
          .singleValueParameter(SPEED_UP, Float.class);
      default -> 1.0f;
    };

    // get timestamp field
    var timestampField = extractor
        .getAdapterDescription()
        .getEventSchema()
        .getEventProperties()
        .stream()
        .filter(eventProperty ->
            eventProperty.getDomainProperties().contains(URI.create(SO.DATE_TIME)))
        .findFirst();

    if (timestampField.isEmpty()) {
      throw new AdapterException("Could not find a timestamp field in event schema");
    } else {
      timestampRuntimeName = timestampField.get().getRuntimeName();
    }

    // start replay adapter
    if (replayOnce) {
      executor.schedule(() -> parseFile(extractor, collector, adapterRuntimeContext),
          0,
          TimeUnit.SECONDS);
    } else {
      executor.scheduleAtFixedRate(() -> parseFile(extractor, collector, adapterRuntimeContext),
          0,
          1,
          TimeUnit.SECONDS);
    }
  }

  private void parseFile(IAdapterParameterExtractor extractor,
                         IEventCollector collector,
                         IAdapterRuntimeContext adapterRuntimeContext) {
    try {
      InputStream inputStream = getDataFromEndpoint(extractor
          .getStaticPropertyExtractor()
          .selectedFilename(FILE_PATH));

      extractor.selectedParser().parse(inputStream, (event) -> {

        long actualEventTimestamp = -1;
        if (event.get(timestampRuntimeName) instanceof Long) {
          actualEventTimestamp = (long) event.get(timestampRuntimeName);
        } else {
          LOG.error(
              "The timestamp field is not a unix timestamp in ms. Value: %s"
                  .formatted(event.get(timestampRuntimeName)));
        }

        if (timestampLastEvent != -1) {
          long sleepTime = (long) ((actualEventTimestamp - timestampLastEvent) / speedUp);
          // speed up is set to Float.MAX_VALUE when user selected fastest option
          if (sleepTime > 0 && speedUp != Float.MAX_VALUE) {
            try {
              Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
              LOG.info("File stream adapter was stopped, the current replay is interuppted", e);
            }
          }
        }

        timestampLastEvent = actualEventTimestamp;
        if (replaceTimestamp) {
          event.put(timestampRuntimeName, System.currentTimeMillis());
        }

        collector.collect(event);
      });

    } catch (AdapterException e) {
      adapterRuntimeContext
          .getLogger()
          .error(e);
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext) {
    executor.shutdownNow();
    LOG.info("Stopped file stream adapter for file");
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    var inputStream = getDataFromEndpoint(extractor
        .getStaticPropertyExtractor()
        .selectedFilename(FILE_PATH));
    return extractor.selectedParser().getGuessSchema(inputStream);
  }

  private InputStream getDataFromEndpoint(String selectedFileName) throws AdapterException {
    try {
      return FileProtocolUtils.getFileInputStream(selectedFileName);
    } catch (IOException e) {
      throw new AdapterException("Could not find file: " + selectedFileName);
    }
  }
}
