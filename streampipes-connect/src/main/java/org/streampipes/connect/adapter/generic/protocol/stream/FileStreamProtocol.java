/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic.protocol.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.generic.format.Format;
import org.streampipes.connect.adapter.generic.format.Parser;
import org.streampipes.connect.adapter.generic.guess.SchemaGuesser;
import org.streampipes.connect.adapter.generic.protocol.Protocol;
import org.streampipes.connect.adapter.generic.sdk.ParameterExtractor;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.staticproperty.FileStaticProperty;
import org.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.streampipes.sdk.helpers.AdapterSourceType;
import org.streampipes.sdk.helpers.Labels;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileStreamProtocol extends PullProtocol {

  private static Logger logger = LoggerFactory.getLogger(FileStreamProtocol.class);

  public static final String ID = "https://streampipes.org/vocabulary/v1/protocol/stream/file";

  private String filePath;

  public FileStreamProtocol() {
  }

  public FileStreamProtocol(Parser parser, Format format, long interval, String filePath) {
    super(parser, format, interval);
    this.filePath = filePath;
  }

  @Override
  InputStream getDataFromEndpoint() {
    FileReader fr = null;
    InputStream inn = null;
    try {

      fr = new FileReader(filePath);
      BufferedReader br = new BufferedReader(fr);

      inn = new FileInputStream(filePath);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return inn;
  }

  @Override
  public Protocol getInstance(ProtocolDescription protocolDescription, Parser parser, Format format) {
    ParameterExtractor extractor = new ParameterExtractor(protocolDescription.getConfig());
    long intervalProperty = Long.parseLong(extractor.singleValue("interval"));

    FileStaticProperty fileStaticProperty = (FileStaticProperty) extractor.getStaticPropertyByName("filePath");

    String fileUri = fileStaticProperty.getLocationPath();
    return new FileStreamProtocol(parser, format, intervalProperty, fileUri);
  }

  @Override
  public ProtocolDescription declareModel() {
    return ProtocolDescriptionBuilder.create(ID, "File", "Continuously streams the content of a " +
            "file.")
            .sourceType(AdapterSourceType.STREAM)
            .iconUrl("file.png")
            .requiredFile(Labels.from("filePath", "File", "This property defines the path to the file."))
            .requiredIntegerParameter(Labels.from("interval", "Interval", "This property " +
                    "defines the pull interval in seconds."))
            .build();
  }

  @Override
  public GuessSchema getGuessSchema() {
    InputStream dataInputStream = getDataFromEndpoint();

    List<byte[]> dataByte = parser.parseNEvents(dataInputStream, 2);

    EventSchema eventSchema = parser.getEventSchema(dataByte);

    GuessSchema result = SchemaGuesser.guessSchma(eventSchema, getNElements(2));

    return result;
  }

  @Override
  public List<Map<String, Object>> getNElements(int n) {
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
