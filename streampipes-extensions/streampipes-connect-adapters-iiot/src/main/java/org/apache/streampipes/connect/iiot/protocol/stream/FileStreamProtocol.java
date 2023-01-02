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
import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.SendToPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.guess.SchemaGuesser;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToBrokerReplayAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToJmsAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToKafkaAdapterSink;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.SendToMqttAdapterSink;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileStreamProtocol extends Protocol {

  private static Logger logger = LoggerFactory.getLogger(FileStreamProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.file";

  //private String filePath;
  private String selectedFileName;
  // private String timestampKey;
  private boolean replaceTimestamp;
  private float speedUp;
  private int timeBetweenReplay;

  private Thread task;
  private boolean running;


  public FileStreamProtocol() {
  }

  public FileStreamProtocol(IParser parser, IFormat format, String selectedFileName,
                            boolean replaceTimestamp, float speedUp, int timeBetweenReplay) {
    super(parser, format);
    this.selectedFileName = selectedFileName;
    this.replaceTimestamp = replaceTimestamp;
    this.speedUp = speedUp;
    this.timeBetweenReplay = timeBetweenReplay;
  }

  @Override
  public void run(IAdapterPipeline adapterPipeline) {
    String timestampKey = getTimestampKey(eventSchema.getEventProperties(), "");

    // exchange adapter pipeline sink with special purpose replay sink for file replay
    if (adapterPipeline.getPipelineSink() instanceof SendToKafkaAdapterSink) {
      adapterPipeline.changePipelineSink(new SendToBrokerReplayAdapterSink(
          (SendToKafkaAdapterSink) adapterPipeline.getPipelineSink(),
          timestampKey,
          replaceTimestamp,
          speedUp));

    } else if (adapterPipeline.getPipelineSink() instanceof SendToMqttAdapterSink) {
      adapterPipeline.changePipelineSink(new SendToBrokerReplayAdapterSink(
          (SendToMqttAdapterSink) adapterPipeline.getPipelineSink(),
          timestampKey,
          replaceTimestamp,
          speedUp));

    } else if (adapterPipeline.getPipelineSink() instanceof SendToJmsAdapterSink) {
      adapterPipeline.changePipelineSink(new SendToBrokerReplayAdapterSink(
          (SendToJmsAdapterSink) adapterPipeline.getPipelineSink(),
          timestampKey,
          replaceTimestamp,
          speedUp));
    }

    running = true;
    task = new Thread() {
      @Override
      public void run() {
        while (running) {

          format.reset();
          SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
          InputStream dataInputStream = getDataFromEndpoint();
          try {
            if (dataInputStream != null) {
              parser.parse(dataInputStream, stk);
            } else {
              logger.warn("Could not read data from file.");
            }
          } catch (ParseException e) {
            logger.error("Error while parsing: " + e.getMessage());
          }

          try {
            Thread.sleep(timeBetweenReplay * 1000);
          } catch (InterruptedException e) {
            logger.error("Error while waiting for next replay round" + e.getMessage());
          }
        }
      }
    };
    task.start();
  }


  @Override
  public void stop() {
    running = false;
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
//    String replaceTimestampString = extractor.selectedSingleValueOption("replaceTimestamp");
    boolean replaceTimestamp = replaceTimestampStringList.size() != 0;

    float speedUp = extractor.singleValueParameter("speed", Float.class);

    int timeBetweenReplay = 1;

    String fileName = extractor.selectedFilename("filePath");
    return new FileStreamProtocol(parser, format, fileName, replaceTimestamp, speedUp, timeBetweenReplay);
  }

  private String getTimestampKey(List<EventProperty> eventProperties, String prefixKey) {
    String result = null;
    for (EventProperty eventProperty : eventProperties) {
      if (eventProperty instanceof EventPropertyPrimitive && eventProperty.getDomainProperties() != null) {
        for (int i = eventProperty.getDomainProperties().size() - 1; i >= 0; i--) {
          if (eventProperty.getDomainProperties().get(0).toString().equals("http://schema.org/DateTime")) {
            result = prefixKey + eventProperty.getRuntimeName();
          }
        }
      } else if (eventProperty instanceof EventPropertyNested
          && ((EventPropertyNested) eventProperty).getEventProperties() != null) {
        result = getTimestampKey(((EventPropertyNested) eventProperty).getEventProperties(),
            prefixKey + eventProperty.getRuntimeName() + ".");
      } else if (eventProperty instanceof EventPropertyList
          && ((EventPropertyList) eventProperty).getEventProperty() != null) {
        result = getTimestampKey(Arrays.asList(((EventPropertyList) eventProperty).getEventProperty()),
            prefixKey + eventProperty.getRuntimeName() + ".");
      }
      if (result != null) {
        return result;
      }
    }
    return result;
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .sourceType(AdapterSourceType.STREAM)
        .category(AdapterType.Generic)
        .requiredFile(Labels.withId("filePath"), Filetypes.CSV, Filetypes.JSON, Filetypes.XML)
//            .requiredSingleValueSelection(Labels.withId("replaceTimestamp"),
//                Options.from("True", "False"))
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
  public List<Map<String, Object>> getNElements(int n) throws ParseException {
    List<Map<String, Object>> result = new ArrayList<>();

    InputStream dataInputStream = getDataFromEndpoint();

    List<byte[]> dataByteArray = parser.parseNEvents(dataInputStream, n);

    // Check that result size is n. Currently, just an error is logged. Maybe change to an exception
    if (dataByteArray.size() < n) {
      logger.error("Error in File Protocol! User required: " + n + " elements but the resource just had: "
          + dataByteArray.size());
    }

    for (byte[] b : dataByteArray) {
      result.add(format.parse(b));
    }

    return result;
  }


  @Override
  public String getId() {
    return ID;
  }
}
