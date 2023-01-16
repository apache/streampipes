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

import org.apache.streampipes.connect.iiot.utils.FileProtocolUtils;
import org.apache.streampipes.extensions.api.connect.EmitBinaryEvent;
import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.guess.SchemaGuesser;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.extensions.management.util.EventSchemaUtils;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Filetypes;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.utils.Assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileStreamProtocol extends Protocol {

  private static final Logger logger = LoggerFactory.getLogger(FileStreamProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.file";

  private String selectedFileName;
  private boolean replaceTimestamp;
  private float speedUp;
  private int timeBetweenReplay;

  private ScheduledExecutorService executor;

  public FileStreamProtocol() {
  }

  public FileStreamProtocol(IParser parser,
                            IFormat format,
                            String selectedFileName,
                            boolean replaceTimestamp,
                            float speedUp,
                            int timeBetweenReplay) {
    super(parser, format);
    this.selectedFileName = selectedFileName;
    this.replaceTimestamp = replaceTimestamp;
    this.speedUp = speedUp;
    this.timeBetweenReplay = timeBetweenReplay;
  }

  @Override
  public void run(IAdapterPipeline adapterPipeline) throws AdapterException {
    String timestampKey = getTimestampKey(adapterPipeline.getResultingEventSchema());


    executor = Executors.newScheduledThreadPool(1);
    var eventProcessor = new LocalEventProcessor(adapterPipeline, timestampKey);

    executor.scheduleAtFixedRate(() -> {
      try (InputStream dataInputStream = getDataFromEndpoint()) {
        format.reset();
        parser.parse(dataInputStream, eventProcessor);
      } catch (ParseException | IOException e) {
        logger.error("Error while parsing: " + e.getMessage());
      }
    }, 0, timeBetweenReplay, TimeUnit.SECONDS);
  }

  private class LocalEventProcessor implements EmitBinaryEvent {

    private final IAdapterPipeline adapterPipeline;
    private final String timestampKey;

    private long lastEventTimestamp = -1;

    /**
     * This local class is responsible to parse the events and set the timestamp accordign to the selected replay
     * configurations
     * @param adapterPipeline that transforms the events and send them to the message broker in the end
     * @param timestampKey the runtimeName of the timestamp field
     */
    public LocalEventProcessor(IAdapterPipeline adapterPipeline,
                               String timestampKey) {
      this.adapterPipeline = adapterPipeline;
      this.timestampKey = timestampKey;
      format.reset();
    }

    @Override
    public Boolean emit(byte[] event) {

      var eventMap = format.parse(event);
      if (eventMap != null) {
        long actualEventTimestamp = (long) eventMap.get(timestampKey);

        if (lastEventTimestamp != -1) {
          long sleepTime = (long) ((actualEventTimestamp - lastEventTimestamp) / speedUp);
          if (sleepTime > 0) {
            try {
              Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }

        if (replaceTimestamp) {
          eventMap.put(timestampKey, System.currentTimeMillis());
        }
        lastEventTimestamp = actualEventTimestamp;

        adapterPipeline.process(eventMap);

      }
      return true;
    }
  }

  @Override
  public void stop() {
    executor.shutdownNow();
    logger.info("Stopped file stream adapter for file " + selectedFileName);
  }

  private InputStream getDataFromEndpoint() throws ParseException {


    try {
      return FileProtocolUtils.getFileInputStream(this.selectedFileName);
    } catch (IOException e) {
      throw new ParseException("Could not find file: " + selectedFileName);
    }
  }

  @Override
  public Protocol getInstance(ProtocolDescription protocolDescription, IParser parser, IFormat format) {
    StaticPropertyExtractor extractor =
        StaticPropertyExtractor.from(protocolDescription.getConfig(), new ArrayList<>());

    List<String> replaceTimestampStringList = extractor.selectedMultiValues("replaceTimestamp", String.class);
    boolean replaceTimestamp = replaceTimestampStringList.size() != 0;

    float speedUp = extractor.singleValueParameter("speed", Float.class);

    int timeBetweenReplay = 1;

    String fileName = extractor.selectedFilename("filePath");
    return new FileStreamProtocol(parser, format, fileName, replaceTimestamp, speedUp, timeBetweenReplay);
  }

  private String getTimestampKey(EventSchema eventSchema) throws AdapterException {
    var timestampProperty = EventSchemaUtils.getTimestampProperty(eventSchema);
    if (timestampProperty.isPresent()) {
      return timestampProperty.get().getRuntimeName();
    } else {
      throw new AdapterException("No timestamp present in event schema");
    }

  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .sourceType(AdapterSourceType.STREAM)
        .category(AdapterType.Generic)
        .requiredFile(Labels.withId("filePath"), Filetypes.CSV, Filetypes.JSON, Filetypes.XML)
        .requiredMultiValueSelection(Labels.withId("replaceTimestamp"),
            Options.from(""))
        .requiredFloatParameter(Labels.withId("speed"))
        .build();
  }

  @Override
  public GuessSchema getGuessSchema() throws ParseException {
    InputStream dataInputStream = getDataFromEndpoint();

    List<byte[]> dataByte = parser.parseNEvents(dataInputStream, 2);

    if (parser.supportsPreview()) {
      return SchemaGuesser.guessSchema(parser.getSchemaAndSample(dataByte));
    } else {
      EventSchema eventSchema = parser.getEventSchema(dataByte);
      return SchemaGuesser.guessSchema(eventSchema);
    }
  }


  @Override
  public String getId() {
    return ID;
  }
}
