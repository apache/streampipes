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

package org.apache.streampipes.connect.protocol.stream;

import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.connect.SendToPipeline;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.guess.SchemaGuesser;
import org.apache.streampipes.connect.adapter.model.generic.Format;
import org.apache.streampipes.connect.adapter.model.generic.Parser;
import org.apache.streampipes.connect.adapter.model.generic.Protocol;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.connect.adapter.preprocessing.elements.SendToKafkaAdapterSink;
import org.apache.streampipes.connect.adapter.preprocessing.elements.SendToKafkaReplayAdapterSink;
import org.apache.streampipes.connect.adapter.sdk.ParameterExtractor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.*;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.helpers.AdapterSourceType;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Options;

import java.io.*;
import java.util.*;

public class FileStreamProtocol extends Protocol {

  private static Logger logger = LoggerFactory.getLogger(FileStreamProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.protocol.stream.file";

  private String filePath;
 // private String timestampKey;
  private boolean replaceTimestamp;
  private float speedUp;
  private int timeBetweenReplay;

  private Thread task;
  private boolean running;


  public FileStreamProtocol() {
  }

  public FileStreamProtocol(Parser parser, Format format, String filePath,
                            boolean replaceTimestamp, float speedUp, int timeBetweenReplay) {
    super(parser, format);
    this.filePath = filePath;
    this.replaceTimestamp = replaceTimestamp;
    this.speedUp = speedUp;
    this.timeBetweenReplay = timeBetweenReplay;
  }

  @Override
  public void run(AdapterPipeline adapterPipeline) {
    String timestampKey = getTimestampKey(eventSchema.getEventProperties(), "");
    SendToKafkaAdapterSink adapterPipelineSink = (SendToKafkaAdapterSink) adapterPipeline.getPipelineSink();

    running = true;

    task = new Thread() {
        @Override
        public void run() {
          while (running) {
            adapterPipeline.changePipelineSink(new SendToKafkaReplayAdapterSink(adapterPipelineSink, timestampKey,
                    replaceTimestamp, speedUp));
            format.reset();
            SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
            InputStream data = getDataFromEndpoint();
            try {
              if(data != null) {
                parser.parse(data, stk);
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

  InputStream getDataFromEndpoint() throws ParseException {
    FileReader fr = null;
    InputStream inn = null;
    try {

      fr = new FileReader(filePath);
      BufferedReader br = new BufferedReader(fr);

      inn = new FileInputStream(filePath);

    } catch (FileNotFoundException e) {
        throw new ParseException("Could not find file: " + filePath);
    }

    if (inn == null)
        throw new ParseException("Could not receive Data from file: " + filePath);


    return inn;
  }

  @Override
  public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
    ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());

    List<String> replaceTimestampStringList = extractor.selectedMultiValues("replaceTimestamp");
//    String replaceTimestampString = extractor.selectedSingleValueOption("replaceTimestamp");
    boolean replaceTimestamp = replaceTimestampStringList.size() == 0 ? false : true;

    float speedUp = Float.parseFloat(extractor.singleValue("speed"));

    int timeBetweenReplay = 1;

    FileStaticProperty fileStaticProperty = (FileStaticProperty) extractor.getStaticPropertyByName("filePath");

    String fileUri = fileStaticProperty.getLocationPath();
    return new FileStreamProtocol(parser, format, fileUri, replaceTimestamp, speedUp, timeBetweenReplay);
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
      } else if (eventProperty instanceof EventPropertyNested && ((EventPropertyNested) eventProperty).getEventProperties() != null) {
        result = getTimestampKey(((EventPropertyNested) eventProperty).getEventProperties(),
                prefixKey + eventProperty.getRuntimeName() + ".");
      } else if (eventProperty instanceof EventPropertyList && ((EventPropertyList) eventProperty).getEventProperty() != null) {
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
            .requiredFile(Labels.withId("filePath"))
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

    EventSchema eventSchema = parser.getEventSchema(dataByte);

    GuessSchema result = SchemaGuesser.guessSchma(eventSchema, getNElements(2));

    return result;
  }

  @Override
  public List<Map<String, Object>> getNElements(int n) throws ParseException {
    List<Map<String, Object>> result = new ArrayList<>();

    InputStream dataInputStream = getDataFromEndpoint();

    List<byte[]> dataByteArray = parser.parseNEvents(dataInputStream, n);

    // Check that result size is n. Currently just an error is logged. Maybe change to an exception
    if (dataByteArray.size() < n) {
      logger.error("Error in File Protocol! User required: " + n + " elements but the resource just had: " +
              dataByteArray.size());
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
