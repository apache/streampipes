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
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.CsvParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.ImageParser;
import org.apache.streampipes.extensions.management.connect.adapter.parser.JsonParsers;
import org.apache.streampipes.extensions.management.connect.adapter.parser.xml.XmlParser;
import org.apache.streampipes.model.connect.guess.SampleData;
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
    boolean replayOnce = extractUserInputsAndReturnValueOfReplayOnce(extractor);
    determineTimestampRuntimeName(extractor);
    validateTimestampFieldInInputEvent(extractor);

    startAdapterReplayThread(extractor, collector, adapterRuntimeContext, replayOnce);
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
      executor.scheduleWithFixedDelay(
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
      throw new AdapterException("Could not find a timestamp field in event schema. "
                                     + "The file replay adapter requires a Unix timestamp to be present in the "
                                     + "original input data.");
    } else {
      timestampRuntimeName = timestampField.get()
                                           .getRuntimeName();
    }
  }

  protected void validateTimestampFieldInInputEvent(IAdapterParameterExtractor extractor)
      throws AdapterException {
    var inputEvents = extractor.getAdapterDescription()
                               .getTransformationConfig()
                               .getInputs();

    if (inputEvents == null || inputEvents.isEmpty()) {
      throw new AdapterException("Could not validate timestamp field in original input event. "
                                     + "No sample input event is available. The file replay adapter requires a Unix "
                                     + "timestamp to be present in the original input data.");
    }

    var timestampFieldValue = inputEvents.get(0).get(timestampRuntimeName);

    if (!(timestampFieldValue instanceof Number)) {
      throw new AdapterException("The timestamp field in the original input event must be numeric. "
                                     + "The file replay adapter requires a Unix timestamp to be present in the "
                                     + "original input data. Field: %s, value: %s".formatted(
          timestampRuntimeName,
          timestampFieldValue
      ));
    }
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
    // The parse method does not throw AdapterExceptions, so event-level errors are logged in the callback.
    parser.parse(inputStream, (event) -> {
      try {
        processEvent(collector, event);
      } catch (AdapterException e) {
        adapterRuntimeContext
            .getLogger()
            .error(e);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  protected void processEvent(
      IEventCollector collector,
      Map<String, Object> event
  ) throws AdapterException, InterruptedException {
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
    var timestampFieldValue = event.get(timestampRuntimeName);

    if (timestampFieldValue instanceof Long) {
      actualEventTimestamp = (Long) timestampFieldValue;
    } else if (timestampFieldValue instanceof Integer) {
      actualEventTimestamp = (Integer) timestampFieldValue;
    }

    if (actualEventTimestamp == -1 && !replaceTimestamp) {
      throw new AdapterException("Timestamp field could not be parsed, skipping event. "
                                     + "The file replay adapter requires a Unix timestamp to be present in the "
                                     + "original input data. Value: %s".formatted(event.get(timestampRuntimeName)));
    }

    return actualEventTimestamp;
  }

  private void reduceReplaySpeedIfRequired(long actualEventTimestamp) throws InterruptedException {
    long sleepTime;
    if (timestampLastEvent != -1 && actualEventTimestamp != -1) {
      sleepTime = (long) ((actualEventTimestamp - timestampLastEvent) / speedUp);
    } else {
      sleepTime = 1;
    }
    // speed up is set to Float.MAX_VALUE when user selected fastest option
    if (sleepTime > 0 && speedUp != Float.MAX_VALUE) {
        Thread.sleep(sleepTime);
    }
  }

  private void replaceTimestampIfRequired(Map<String, Object> event) {
    if (replaceTimestamp) {
      event.put(timestampRuntimeName, System.currentTimeMillis());
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext) {
    executor.shutdownNow();
    LOG.info("Stopped file stream adapter for file");
  }

  @Override
  public SampleData onSampleDataRequested(
      IAdapterParameterExtractor extractor,
      IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    var inputStream = getFileAsInputStreamFromEndpoint(extractor);
    return extractor.selectedParser()
                    .getSampleData(inputStream);
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

  protected void setTimestampRuntimeName(String timestampRuntimeName) {
    this.timestampRuntimeName = timestampRuntimeName;
  }

  protected void setReplaceTimestamp(boolean replaceTimestamp) {
    this.replaceTimestamp = replaceTimestamp;
  }

}
