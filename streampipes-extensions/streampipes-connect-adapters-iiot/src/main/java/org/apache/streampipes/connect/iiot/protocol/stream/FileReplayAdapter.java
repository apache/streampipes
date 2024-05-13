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
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.vocabulary.SO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
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

  private String timestampSourceFieldName;

  private float speedUp;

  private long timestampLastEvent = -1;

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, 0, FileReplayAdapter::new)
        .withSupportedParsers(
            new JsonParsers(),
            new CsvParser(),
            new XmlParser(),
            new ImageParser()
        )
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic)
        .requiredFile(
            Labels.withId(FILE_PATH),
            Filetypes.CSV,
            Filetypes.JSON,
            Filetypes.XML
        )
        .requiredMultiValueSelection(Labels.withId(REPLACE_TIMESTAMP), Options.from(""))
        .requiredSingleValueSelection(
            Labels.withId(REPLAY_ONCE),
            Options.from("no", "yes")
        )
        .requiredAlternatives(Labels.withId(SPEED),
            Alternatives.from(Labels.withId(KEEP_ORIGINAL_TIME), true),
            Alternatives.from(Labels.withId(FASTEST)),
            Alternatives.from(
                Labels.withId(SPEED_UP_FACTOR),
                StaticProperties.group(
                    Labels.withId(SPEED_UP_FACTOR_GROUP),
                    StaticProperties.doubleFreeTextProperty(Labels.withId(
                        SPEED_UP))
                )
            )
        )
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(
      IAdapterParameterExtractor extractor,
      IEventCollector collector,
      IAdapterRuntimeContext adapterRuntimeContext
  ) throws AdapterException {

    // extract user input
    executor = Executors.newScheduledThreadPool(1);
    boolean replayOnce = extractor
        .getStaticPropertyExtractor()
        .selectedSingleValue(REPLAY_ONCE, String.class)
        .equals("yes");
    var replaceTimestampStringList = extractor
        .getStaticPropertyExtractor()
        .selectedMultiValues(REPLACE_TIMESTAMP, String.class);
    replaceTimestamp = !replaceTimestampStringList.isEmpty();
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
            eventProperty.getDomainProperties()
                .contains(URI.create(SO.DATE_TIME)))
        .findFirst();

    if (timestampField.isEmpty()) {
      throw new AdapterException("Could not find a timestamp field in event schema");
    } else {
      timestampRuntimeName = timestampField.get()
          .getRuntimeName();
    }

    determineSourceTimestampField(extractor);

    // start replay adapter
    if (replayOnce) {
      executor.schedule(
          () -> parseFile(extractor, collector, adapterRuntimeContext),
          0,
          TimeUnit.SECONDS
      );
    } else {
      executor.scheduleAtFixedRate(
          () -> parseFile(extractor, collector, adapterRuntimeContext),
          0,
          1,
          TimeUnit.SECONDS
      );
    }
  }

  /**
   * Determines the source field name for the timestamp property based
   * on any renaming rules configured within the adapter.
   * It ensures that the correct field name is used when extracting the timestamp from the raw data.
   *
   * @param extractor An instance of {@code IAdapterParameterExtractor} used for extracting adapter parameters.
   * @throws AdapterException If there are multiple renaming rules detected affecting the timestamp property,
   *                          indicating an invalid configuration.
   */
  private void determineSourceTimestampField(IAdapterParameterExtractor extractor) throws AdapterException {
    var renamingRulesTimestamp = getRenamingRulesTimestamp(extractor);
    if (!renamingRulesTimestamp.isEmpty()) {
      if (renamingRulesTimestamp.size() == 1) {
        timestampSourceFieldName = renamingRulesTimestamp.get(0)
            .getOldRuntimeKey();
      } else {
        throw new AdapterException("Invalid configuration - multiple renaming rules detected which affect the "
            + "timestamp property.");
      }
    } else {
      // no renaming rules can be found, timestamp name does not change
      timestampSourceFieldName = timestampRuntimeName;
    }
  }

  /**
   * Retrieves a list of {@link RenameRuleDescription} affecting the timestamp property.
   *
   * @param extractor An instance of IAdapterParameterExtractor used to extract adapter description and rules.
   * @return A list of {@link RenameRuleDescription} containing rules affecting timestamp property.
   */
  private List<RenameRuleDescription> getRenamingRulesTimestamp(IAdapterParameterExtractor extractor) {
    var renamingRules =
        extractor.getAdapterDescription()
            .getRules()
            .stream()
            .filter(rule -> rule instanceof RenameRuleDescription)
            .map(rule -> (RenameRuleDescription) rule)
            .toList();

    return renamingRules.stream()
        .filter(rule -> rule.getNewRuntimeKey()
            .equals(timestampRuntimeName))
        .toList();
  }

  private void parseFile(
      IAdapterParameterExtractor extractor,
      IEventCollector collector,
      IAdapterRuntimeContext adapterRuntimeContext
  ) {
    try {
      InputStream inputStream = getDataFromEndpoint(extractor
          .getStaticPropertyExtractor()
          .selectedFilename(FILE_PATH));

      extractor.selectedParser()
          .parse(inputStream, (event) -> {

            long actualEventTimestamp = -1;
            try {
              actualEventTimestamp = (long) event.get(timestampSourceFieldName);
            } catch (ClassCastException e) {
              adapterRuntimeContext
                  .getLogger()
                  .error(
                      new AdapterException("The timestamp field is not a unix timestamp in ms, skipping event. Value: %s"
                          .formatted(event.get(timestampSourceFieldName))
                      ));
            }

            if (timestampLastEvent == -1) {
              return;
            } else {
              long sleepTime = (long) ((actualEventTimestamp - timestampLastEvent) / speedUp);
              // speed up is set to Float.MAX_VALUE when user selected fastest option
              if (sleepTime > 0 && speedUp != Float.MAX_VALUE) {
                try {
                  Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                  LOG.info("File stream adapter was stopped, the current replay is interrupted", e);
                }
              }
            }

            timestampLastEvent = actualEventTimestamp;
            if (replaceTimestamp) {
              event.put(timestampSourceFieldName, System.currentTimeMillis());
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
