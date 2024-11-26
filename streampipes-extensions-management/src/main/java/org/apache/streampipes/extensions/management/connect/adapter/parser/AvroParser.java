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
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.IParserEventHandler;
import org.apache.streampipes.model.connect.grounding.ParserDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.ParserDescriptionBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Options;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.util.Utf8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AvroParser implements IParser {

  private static final Logger LOG = LoggerFactory.getLogger(AvroParser.class);

  public static final String ID = "org.apache.streampipes.extensions.management.connect.adapter.parser.avro";
  public static final String LABEL = "Avro";
  public static final String DESCRIPTION = "Can be used to read avro records";

  public static final String SCHEMA = "schema";
  public static final String SCHEMA_REGISTRY = "schemaRegistry";
  public static final String FLATTEN_RECORDS = "flattenRecord";

  private final ParserUtils parserUtils;
  private DatumReader<GenericRecord> datumReader;
  private boolean schemaRegistry;
  private boolean flattenRecord;


  public AvroParser() {
    parserUtils = new ParserUtils();
  }

  public AvroParser(String schemaString, boolean schemaRegistry, boolean flattenRecord) {
    this();
    Schema schema = new Schema.Parser().parse(schemaString);
    this.datumReader = new GenericDatumReader<>(schema);
    this.schemaRegistry = schemaRegistry;
    this.flattenRecord = flattenRecord;
  }

  @Override
  public ParserDescription declareDescription() {
    return ParserDescriptionBuilder.create(ID, LABEL, DESCRIPTION)
      .requiredSingleValueSelection(
          Labels.from(SCHEMA_REGISTRY, "Schema Registry",
                  "Does the messages include the schema registry header?"),
          Options.from("yes", "no")
      )
      .requiredSingleValueSelection(
          Labels.from(FLATTEN_RECORDS, "Flatten Records",
                  "Should nested records be flattened?"),
          Options.from("no", "yes")
      )
      .requiredCodeblock(Labels.from(SCHEMA, "Schema",
  "The schema of the avro record"),
  "{\n"
          + " \"namespace\": \"example.avro\",\n"
          + " \"type\": \"record\",\n"
          + " \"name\": \"Test\",\n"
          + " \"fields\": [\n"
          + "     {\"name\": \"id\", \"type\": \"string\"},\n"
          + "     {\"name\": \"value\", \"type\": \"double\"}\n"
          + " ]\n"
          + "}")
      .build();
  }


  @Override
  public GuessSchema getGuessSchema(InputStream inputStream) throws ParseException {
    GenericRecord avroRecord = getRecord(inputStream);
    var event = toMap(avroRecord);
    return parserUtils.getGuessSchema(event);
  }

  @Override
  public void parse(InputStream inputStream, IParserEventHandler handler) throws ParseException {
    GenericRecord avroRecord = getRecord(inputStream);
    var event = toMap(avroRecord);
    handler.handle(event);
  }

  @Override
  public IParser fromDescription(List<StaticProperty> configuration) {
    var extractor = StaticPropertyExtractor.from(configuration);
    String schema = extractor.codeblockValue(SCHEMA);
    boolean schemaRegistry = extractor
        .selectedSingleValue(SCHEMA_REGISTRY, String.class)
        .equals("yes");
    boolean flattenRecords = extractor
        .selectedSingleValue(FLATTEN_RECORDS, String.class)
        .equals("yes");

    return new AvroParser(schema, schemaRegistry, flattenRecords);
  }

  private GenericRecord getRecord(InputStream inputStream) throws ParseException {
    try {
      if (schemaRegistry) {
        inputStream.skipNBytes(5);
      }
      BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(inputStream, null);
      GenericRecord avroRecord = datumReader.read(null, decoder);
      LOG.info("Read record: {}", avroRecord);
      return  avroRecord;
    } catch (IOException e) {
      throw new ParseException(
          "Error processing Kafka message: " + e.getMessage()
      );
    }
  }


  private Map<String, Object> toMap(GenericRecord avroRecord) {
    Map<String, Object> resultMap = new LinkedHashMap<>();
    avroRecord.getSchema().getFields().forEach(field -> {
      String fieldName = field.name();
      Object fieldValue = avroRecord.get(fieldName);
      if (flattenRecord && fieldValue instanceof GenericRecord){
        Map<String, Object> flatMap = unwrapNestedRecord((GenericRecord) fieldValue, fieldName);
        resultMap.putAll(flatMap);
      } else {
        resultMap.put(fieldName, toMapHelper(fieldValue));
      }
    });

    return resultMap;
  }

  private Object toMapHelper(Object fieldValue) {
    if (fieldValue instanceof GenericRecord){
      return toMap((GenericRecord) fieldValue);
    }
    if (fieldValue instanceof GenericData.Array<?>){
      List<Object> valueList = new ArrayList<>();
      ((GenericData.Array) fieldValue).forEach(value -> valueList.add(toMapHelper(value)));
      return valueList;
    }
    if (fieldValue instanceof Map<?, ?>){
      Map<Object, Object> valueMap = new LinkedHashMap<>();
      ((Map<Object, Object>) fieldValue).entrySet().forEach(
              value -> valueMap.put(convertUTF8(value.getKey()), toMapHelper(value.getValue())));
      return valueMap;
    }
    return convertUTF8(fieldValue);
  }


  private Map<String, Object> unwrapNestedRecord(GenericRecord nestedRecord, String prefix) {
    Map<String, Object> flatMap = new HashMap<>();

    nestedRecord.getSchema().getFields().forEach(field -> {
      String fieldName = field.name();
      Object fieldValue = nestedRecord.get(fieldName);
      String newKey = prefix.isEmpty() ? fieldName : prefix + "_" + fieldName;
      if (fieldValue instanceof GenericRecord) {
        flatMap.putAll(unwrapNestedRecord((GenericRecord) fieldValue, newKey));
      } else {
        flatMap.put(newKey, toMapHelper(fieldValue));
      }
    });

    return flatMap;
  }

  private Object convertUTF8(Object fieldValue) {
    if (fieldValue instanceof Utf8){
      return fieldValue.toString();
    }
    return fieldValue;
  }

}
