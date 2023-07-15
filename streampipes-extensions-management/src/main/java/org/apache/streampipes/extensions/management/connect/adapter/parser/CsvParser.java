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

package org.apache.streampipes.extensions.management.connect.adapter.parser;


import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.shared.DatatypeUtils;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.IParserEventHandler;
import org.apache.streampipes.model.Tuple2;
import org.apache.streampipes.model.connect.grounding.ParserDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.ParserDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class CsvParser implements IParser {

  private static final Logger LOG = LoggerFactory.getLogger(CsvParser.class);

  public static final String ID = "org.apache.streampipes.extensions.management.connect.adapter.parser.csv";
  public static final String LABEL = "CSV";

  public static final String DELIMITER = "delimiter";
  public static final String HEADER = "header";

  public static final String DESCRIPTION = "Can be used to read CSV";

  private final ParserUtils parserUtils;

  private boolean header;
  private char delimiter;

  public CsvParser() {
    parserUtils = new ParserUtils();
  }

  public CsvParser(boolean header, char delimiter) {
    this();
    this.header = header;
    this.delimiter = delimiter;
  }

  @Override
  public IParser fromDescription(List<StaticProperty> config) {
    StaticPropertyExtractor extractor = StaticPropertyExtractor.from(config);

    char delimiter = extractor.singleValueParameter(DELIMITER, String.class).charAt(0);

    boolean header = extractor.selectedMultiValues(HEADER, String.class).stream()
        .anyMatch("Header"::equals);

    return new CsvParser(header, delimiter);
  }


  @Override
  public ParserDescription declareDescription() {
    return ParserDescriptionBuilder.create(ID, LABEL, DESCRIPTION)
        .requiredTextParameter(Labels.from(DELIMITER, "Delimiter",
            "The delimiter of the CSV file(s). Mostly either , or ;"))
        .requiredMultiValueSelection(Labels.from(HEADER, "Header",
                "Does the CSV file include a header or not"),
            List.of(new Option("Header", "Header")))
        .build();
  }

  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) throws ParseException {
    var csvReader = getCsvReader(inputStream);

    var headerAndSample = getHeaderAndFirstSample(csvReader);

    var event = toMap(headerAndSample.k, headerAndSample.v, true);

    return parserUtils.getGuessSchema(event);
  }


  @Override
  public void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException {
    var csvReader = getCsvReader(inputStream);

    var headerAndSample = getHeaderAndFirstSample(csvReader);

    var header = headerAndSample.k;
    var sample = headerAndSample.v;

    try {
      do {
        var event = toMap(header, sample, false);
        handler.handle(event);
      } while ((sample = csvReader.readNext()) != null);

    } catch (CsvValidationException | IOException e) {
      LOG.error("Could not parse row: " + Arrays.toString(sample));
    }
  }


  private Map<String, Object> toMap(String[] header, String[] values, boolean preferFloat) throws ParseException {
    if (header == null) {
      throw new ParseException("Header of csv could not be parsed");
    }

    if (values == null) {
      throw new ParseException("Row in csv is empty");
    }

    if (header.length != values.length) {
      throw new ParseException(
          "Row in csv does not have the same length as header. header: %s row: %s"
              .formatted(Arrays.toString(header), Arrays.toString(values)));
    }

    var event = new HashMap<String, Object>();
    for (int i = 0; i < header.length; i++) {
      var runtimeType = DatatypeUtils.getXsdDatatype(values[i], preferFloat);
      var convertedValue = DatatypeUtils.convertValue(values[i], runtimeType);
      event.put(header[i], convertedValue);
    }

    return event;
  }

  private CSVReader getCsvReader(InputStream inputStream) {

    var reader = new BufferedReader(new InputStreamReader(inputStream));
    CSVParser parser = new CSVParserBuilder()
        .withSeparator(delimiter)
        .withIgnoreQuotations(true)
        .build();

    return new CSVReaderBuilder(reader)
        .withSkipLines(0)
        .withCSVParser(parser)
        .build();
  }

  private Tuple2<String[], String[]> getHeaderAndFirstSample(CSVReader csvReader) throws ParseException {
    String[] headers = {};
    String[] sample;

    try {
      // use first row as headers
      if (header) {
        headers = csvReader.readNext();
      }
      // read first data sample that is used to guess data types
      sample = csvReader.readNext();

      // create headers if not available in data
      if (!header) {
        headers = IntStream.range(0, sample.length)
            .mapToObj(i -> "key_" + i)
            .toArray(String[]::new);
      }

    } catch (IOException | CsvValidationException e) {
      throw new RuntimeException(e);
    }

    return new Tuple2<>(headers, sample);
  }


}
