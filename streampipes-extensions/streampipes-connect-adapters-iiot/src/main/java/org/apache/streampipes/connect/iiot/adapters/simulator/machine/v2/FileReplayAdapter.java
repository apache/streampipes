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

package org.apache.streampipes.connect.iiot.adapters.simulator.machine.v2;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.iiot.utils.FileProtocolUtils;
import org.apache.streampipes.extensions.management.connect.AdapterInterface;
import org.apache.streampipes.extensions.management.connect.adapter.parser.CsvParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.ImageParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.JsonParsers;
import org.apache.streampipes.extensions.management.connect.adapter.parser.xml.XmlParser;
import org.apache.streampipes.extensions.management.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.management.context.IAdapterRuntimeContext;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.StreamPipesErrorMessage;
import org.apache.streampipes.model.connect.adapter.AdapterConfiguration;
import org.apache.streampipes.model.connect.adapter.IEventCollector;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.extractor.IAdapterParameterExtractor;
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

public class FileReplayAdapter implements AdapterInterface {

  private static final Logger LOG = LoggerFactory.getLogger(FileReplayAdapter.class);

  private static final String ID = "org.apache.streampipes.connect.iiot.adapters.simulator.machine.v2.file";
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
  private boolean replayOnce;
  private boolean replaceTimestamp;
  private String timestampRuntimeName;

  private float speedUp;

  @Override
  public AdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID)
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
    replayOnce = extractor
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

    // adapt preprocessing pipeline
    if (replaceTimestamp) {
      // get timestamp field

      var timestampField = extractor
          .getAdapterDescription()
          .getEventSchema()
          .getEventProperties()
          .stream()
          .filter(eventProperty ->
              eventProperty.getDomainProperties().contains(URI.create(SO.DATE_TIME)))
          .findFirst();

      if (!timestampField.isPresent()) {
        throw new AdapterException("Could not find a timestamp field in event schema");
      } else {
        timestampRuntimeName = timestampField.get().getRuntimeName();
      }
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
        if (replaceTimestamp) {
          event.put(timestampRuntimeName, System.currentTimeMillis());
        }

        collector.collect(event);
      });

    } catch (AdapterException e) {
      adapterRuntimeContext
          .getLogger()
          .addErrorMessage(extractor.getAdapterDescription().getElementId(),
              SpLogEntry.from(System.currentTimeMillis(), StreamPipesErrorMessage.from(e)));
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
