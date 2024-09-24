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
import org.apache.streampipes.connect.shared.preprocessing.generator.StatelessTransformationRuleGeneratorVisitor;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.CsvParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.ImageParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.JsonParsers;
import org.apache.streampipes.extensions.management.connect.adapter.parser.xml.XmlParser;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
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
import java.util.List;
import java.util.Map;
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

  private TimestampTranfsformationRuleDescription timestampTranfsformationRuleDescription;

  private String timestampSourceFieldName;

  private float speedUp;

  private long timestampLastEvent = -1;

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, 0, FileReplayAdapter::new)
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
        .requiredAlternatives(
            Labels.withId(SPEED),
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

    throwExceptionWhenAddTimestampRuleIsSelected(extractor);

    boolean replayOnce = extractUserInputsAndReturnValueOfReplayOnce(extractor);

    determineTimestampRuntimeName(extractor);

    determineSourceTimestampField(extractor);

    startAdapterReplayThread(extractor, collector, adapterRuntimeContext, replayOnce);
  }

  protected static void throwExceptionWhenAddTimestampRuleIsSelected(IAdapterParameterExtractor extractor)
      throws AdapterException {
    boolean ruleExists = extractor.getAdapterDescription()
                                  .getRules()
                                  .stream()
                                  .anyMatch(rule -> rule instanceof AddTimestampRuleDescription);

    if (ruleExists) {
      throw new AdapterException("The file replay adapter requires a valid timestamp within the file. The add "
                                     + "timestamp option in the schema editor is not supported. Please edit the "
                                     + "adater to resolve this problem.");
    }
  }

  private void startAdapterReplayThread(
      IAdapterParameterExtractor extractor,
      IEventCollector collector,
      IAdapterRuntimeContext adapterRuntimeContext,
      boolean replayOnce
  ) {
    executor = Executors.newScheduledThreadPool(1);
    if (replayOnce) {
      executor.schedule(
          () -> getFileFromEndpointAndParseFile(extractor, collector, adapterRuntimeContext),
          0,
          TimeUnit.SECONDS
      );
    } else {
      executor.scheduleAtFixedRate(
          () -> getFileFromEndpointAndParseFile(extractor, collector, adapterRuntimeContext),
          0,
          1,
          TimeUnit.SECONDS);
    }
  }

  private boolean extractUserInputsAndReturnValueOfReplayOnce(IAdapterParameterExtractor extractor) {
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
    return replayOnce;
  }

  private void determineTimestampRuntimeName(IAdapterParameterExtractor extractor) throws AdapterException {
    var timestampField = extractor
        .getAdapterDescription()
        .getEventSchema()
        .getEventProperties()
        .stream()
        .filter(eventProperty -> SO.DATE_TIME.equals(eventProperty.getSemanticType()))
        .findFirst();

    if (timestampField.isEmpty()) {
      throw new AdapterException("Could not find a timestamp field in event schema");
    } else {
      timestampRuntimeName = timestampField.get()
                                           .getRuntimeName();
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

  private void getFileFromEndpointAndParseFile(
      IAdapterParameterExtractor extractor,
      IEventCollector collector,
      IAdapterRuntimeContext adapterRuntimeContext
  ) {
    try {
      var inputStream = getFileAsInputStreamFromEndpoint(extractor);

      parseFile(
          extractor.selectedParser(),
          collector,
          inputStream,
          adapterRuntimeContext
      );

    } catch (AdapterException e) {
      adapterRuntimeContext
          .getLogger()
          .error(e);
    }
  }

  private void parseFile(
      IParser parser,
      IEventCollector collector,
      InputStream inputStream,
      IAdapterRuntimeContext adapterRuntimeContext
  ) {
    // The parse method does not throw AdapterExceptions, that's why the logging is handeled within the catch blog here
    parser.parse(inputStream, (event) -> {
      try {
        processEvent(collector, event);
      } catch (AdapterException e) {
        adapterRuntimeContext
            .getLogger()
            .error(e);
      }
    });
  }

  protected void processEvent(
      IEventCollector collector,
      Map<String, Object> event
  ) throws AdapterException {

    long actualEventTimestamp = getTimestampFromEvent(event);

    reduceReplaySpeedIfRequired(actualEventTimestamp);

    // This must be the last step, because the original timestamp must be used to simulate the replay frequency
    // of the original file
    replaceTimestampIfRequired(event);

    timestampLastEvent = actualEventTimestamp;
    collector.collect(event);
  }

  protected long getTimestampFromEvent(Map<String, Object> event) throws AdapterException {
    long actualEventTimestamp = -1;

    var timestampFieldValue = event.get(timestampSourceFieldName);

    if (timestampFieldValue instanceof Long) {
      actualEventTimestamp = (Long) timestampFieldValue;
    } else if (timestampFieldValue instanceof Integer) {
      actualEventTimestamp = (Integer) timestampFieldValue;
    }

    // transform timestamp if transformation rule is present
    actualEventTimestamp = transformTimestampIfTransformationRuleIsPresent(event, actualEventTimestamp);


    if (actualEventTimestamp == -1 && !replaceTimestamp) {
      throw new AdapterException("Timestamp field could not be parsed, skipping event. "
                                     + "Value: %s".formatted(event.get(timestampSourceFieldName)));
    }

    return actualEventTimestamp;
  }

  private long transformTimestampIfTransformationRuleIsPresent(Map<String, Object> event, long actualEventTimestamp) {
    if (timestampTranfsformationRuleDescription != null) {
      var transformationRuleDescription = timestampTranfsformationRuleDescription;

      var transformationRuleVisitor = new StatelessTransformationRuleGeneratorVisitor();
      transformationRuleVisitor.visit(transformationRuleDescription);
      var timestampTransformationRule = transformationRuleVisitor.getTransformationRules()
                                                                 .get(0);

      actualEventTimestamp = (Long) (
          timestampTransformationRule.apply(event)
                                     .get(timestampSourceFieldName));
    }
    return actualEventTimestamp;
  }

  private void reduceReplaySpeedIfRequired(long actualEventTimestamp) {
    long sleepTime;
    if (timestampLastEvent != -1 && actualEventTimestamp != -1) {
      sleepTime = (long) ((actualEventTimestamp - timestampLastEvent) / speedUp);
    } else {
      sleepTime = 1;
    }
    // speed up is set to Float.MAX_VALUE when user selected fastest option
    if (sleepTime > 0 && speedUp != Float.MAX_VALUE) {
      try {
        Thread.sleep(sleepTime);
      } catch (InterruptedException e) {
        LOG.info("File stream adapter was stopped, the current replay is interrupted", e);
      }
    }
  }

  private void replaceTimestampIfRequired(Map<String, Object> event) {
    if (replaceTimestamp) {
      event.put(timestampSourceFieldName, System.currentTimeMillis());
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext) {
    executor.shutdownNow();
    LOG.info("Stopped file stream adapter for file");
  }

  @Override
  public GuessSchema onSchemaRequested(
      IAdapterParameterExtractor extractor,
      IAdapterGuessSchemaContext adapterGuessSchemaContext
  ) throws AdapterException {
    var inputStream = getFileAsInputStreamFromEndpoint(extractor);
    return extractor.selectedParser()
                    .getGuessSchema(inputStream);
  }

  private InputStream getFileAsInputStreamFromEndpoint(IAdapterParameterExtractor extractor) throws AdapterException {
    var selectedFileName = extractor
        .getStaticPropertyExtractor()
        .selectedFilename(FILE_PATH);

    try {
      return FileProtocolUtils.getFileInputStream(selectedFileName);
    } catch (IOException e) {
      throw new AdapterException("Could not find file: " + selectedFileName, e);
    }
  }

  protected void setTimestampSourceFieldName(String timestampSourceFieldName) {
    this.timestampSourceFieldName = timestampSourceFieldName;
  }

  protected void setReplaceTimestamp(boolean replaceTimestamp) {
    this.replaceTimestamp = replaceTimestamp;
  }

  protected void setTimestampTranfsformationRuleDescription(
      TimestampTranfsformationRuleDescription timestampTranfsformationRuleDescription
  ) {
    this.timestampTranfsformationRuleDescription = timestampTranfsformationRuleDescription;
  }

  /**
   * Removes the timestamp transformation rules from the adapter description.
   *
   * <p>The FileReplay adapter manages timestamp transformations internally to accurately simulate the replay frequency.
   * This is necessary as the timestamp field values are crucial for this simulation. As a result, the timestamp rule
   * description is stored locally within the FileReplay adapter and is applied when the onAdapterStarted method is
   * invoked.</p>
   */
  @Override
  public void preprocessAdapterDescription(AdapterDescription adapterDescription) {

    this.timestampTranfsformationRuleDescription = adapterDescription
        .getRules()
        .stream()
        .filter(rule -> rule instanceof TimestampTranfsformationRuleDescription)
        .map(rule -> (TimestampTranfsformationRuleDescription) rule)
        .findFirst()
        .orElse(null);

    // remove timestamp preprocessing rule
    adapterDescription
        .getRules()
        .removeIf(rule -> rule instanceof TimestampTranfsformationRuleDescription);
  }
}
