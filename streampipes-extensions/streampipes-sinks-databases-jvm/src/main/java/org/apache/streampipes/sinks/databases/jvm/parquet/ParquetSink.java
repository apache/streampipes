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

package org.apache.streampipes.sinks.databases.jvm.parquet;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.vocabulary.XSD;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.apache.parquet.io.OutputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ParquetSink extends StreamPipesDataSink {

  private static final Logger log = LoggerFactory.getLogger(ParquetSink.class);
  private static final String SCHEMA_NAME_KEY = "schema_name";
  private static final String SCHEMA_NAMESPACE_KEY = "schema_namespace";
  private static final String PARQUET_FILE_NAME_KEY = "parquet_file_name";
  private static final String PARQUET_GENERATION_DIRECTORY_KRY = "parquet_generation_directory";
  private static final String ROW_GROUP_SIZE_KEY = "row_group_size";
  private static final String PAGE_SIZE_KEY = "page_size";
  private static final String COMPRESSION_CODEC_NAME_KEY = "compression_codec_name";


  private static final String suffix = ".parquet";

  //XSD DataType
  public static final String LONG = XSD.LONG.toString();
  public static final String INT =  XSD.INT.toString();
  public static final String FLOAT = XSD.FLOAT.toString();
  public static final String DOUBLE = XSD.DOUBLE.toString();
  public static final String BOOLEAN = XSD.BOOLEAN.toString();
  public static final String STRING = XSD.STRING.toString();

  String schemaName;
  String schemaNamespace;
  String parquetGenerationDirectory;
  int rowGroupSize;
  int pageSize;
  CompressionCodecName compressionCodecName;
  Schema parquetSchema;
  SchemaBuilder.FieldAssembler <Schema> schemaFieldAssembler;
  String parquetFileName;
  Path newParquetFilePath;
  ParquetWriter<GenericRecord> writer;
  //The current size of the Parquet is used to confirm the timing of successfully creating a new Parquet
  long parquetFileSize;


  //Compression algorithm
  private static final Map<String, CompressionCodecName> COMPRESSION_CODEC_NAME_MAP = new HashMap<>() {
    {
      put("UNCOMPRESSED", CompressionCodecName.UNCOMPRESSED);
      put("GZIP", CompressionCodecName.GZIP);
      put("LZ4", CompressionCodecName.LZ4);
      put("SNAPPY", CompressionCodecName.SNAPPY);
      put("BROTLI", CompressionCodecName.BROTLI);
      put("ZSTD", CompressionCodecName.ZSTD);
      put("LZO", CompressionCodecName.LZO);
    }
  };

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder
        .create("org.apache.streampipes.sinks.databases.jvm.parquet", 0)
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON).
        category(DataSinkType.DATABASE)
        .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredProperty(EpRequirements.anyProperty())
                .build())
        .requiredTextParameter(Labels.withId(SCHEMA_NAME_KEY))
        .requiredTextParameter(Labels.withId(SCHEMA_NAMESPACE_KEY))
        .requiredTextParameter(Labels.withId(PARQUET_FILE_NAME_KEY))
        .requiredTextParameter(Labels.withId(PARQUET_GENERATION_DIRECTORY_KRY))
        .requiredIntegerParameter(Labels.withId(ROW_GROUP_SIZE_KEY), 134217728)
        .requiredIntegerParameter(Labels.withId(PAGE_SIZE_KEY), 1048576)
        .requiredSingleValueSelection(Labels.withId(COMPRESSION_CODEC_NAME_KEY),
                Options.from(COMPRESSION_CODEC_NAME_MAP.keySet().toArray(new String[0])))
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    this.schemaName = parameters.extractor().singleValueParameter(SCHEMA_NAME_KEY, String.class);
    this.schemaNamespace = parameters.extractor().singleValueParameter(SCHEMA_NAMESPACE_KEY, String.class);
    this.parquetFileName = parameters.extractor().singleValueParameter(PARQUET_FILE_NAME_KEY, String.class);
    this.parquetGenerationDirectory = parameters.extractor().
            singleValueParameter(PARQUET_GENERATION_DIRECTORY_KRY, String.class);
    this.rowGroupSize = parameters.extractor().singleValueParameter(ROW_GROUP_SIZE_KEY, Integer.class);
    this.pageSize = parameters.extractor().singleValueParameter(PAGE_SIZE_KEY, Integer.class);
    this.compressionCodecName = COMPRESSION_CODEC_NAME_MAP.
            get(parameters.extractor().selectedSingleValue(COMPRESSION_CODEC_NAME_KEY, String.class));

    try {
      newParquetFilePath = createNewParquetFilePath(parquetGenerationDirectory, parquetFileName);
      EventSchema schema = parameters.getModel().getInputStreams().get(0).getEventSchema();
      this.schemaFieldAssembler = initialFieldAssembler();
      this.extractEventProperties(schema.getEventProperties(), schemaFieldAssembler);
      this.parquetSchema = this.schemaFieldAssembler.endRecord();
      this.writer = createParquetWriter(newParquetFilePath);
    } catch (IOException e) {
      throw new SpRuntimeException(e.getMessage());
    }
  }

  @Override
  public void onDetach() {
    try {
      if (writer != null){
        writer.close();
        log.info("success to close ParquetSink");
      }
    } catch (IOException e) {
      log.warn("Failed to close parquetWriter", e);
      throw new SpRuntimeException("Failed to close writer when close ParquetSink", e);
    }
  }

  @Override
  public void onEvent(Event event) {
    if (event == null) {
      log.info("Received null event");
      return;
    }

    final Map<String, Object> measurementValuePairs = event.getRaw();

    if (measurementValuePairs.size() <= 1) {
      log.info("Received event with insufficient measurement value pairs");
      return;
    }
    GenericRecord record = new GenericData.Record(parquetSchema);
    for (int i = 0; i < parquetSchema.getFields().size(); i++) {
      // Get the field name and type from the Parquet schema
      String fieldName = parquetSchema.getFields().get(i).name();
      Schema.Type fieldType = parquetSchema.getFields().get(i).schema().getType();
      AbstractField fieldByRuntimeName = event.getFieldByRuntimeName(fieldName);
      switch (fieldType){
        case BOOLEAN :
          record.put(fieldName, fieldByRuntimeName.getAsPrimitive().getAsBoolean());
          break;
        case INT :
          record.put(fieldName, fieldByRuntimeName.getAsPrimitive().getAsInt());
          break;
        case LONG :
          record.put(fieldName, fieldByRuntimeName.getAsPrimitive().getAsLong());
          break;
        case FLOAT :
          record.put(fieldName, fieldByRuntimeName.getAsPrimitive().getAsFloat());
          break;
        case DOUBLE :
          record.put(fieldName, fieldByRuntimeName.getAsPrimitive().getAsDouble());
          break;
        case STRING :
          record.put(fieldName, fieldByRuntimeName.getAsPrimitive().getAsString());
          break;
        default:
          log.info("Unsupported data type: {}", fieldType);
          break;
      }
    }
    try {
      writer.write(record);
      parquetFileSize = writer.getDataSize();
      if (parquetFileSize > rowGroupSize){
        resetParquetWriter();
      }
    } catch (IOException e) {
      throw new SpRuntimeException("Failed to write record" + e.getMessage());
    }
  }

  private SchemaBuilder.FieldAssembler <Schema> initialFieldAssembler(){
    this.schemaFieldAssembler = SchemaBuilder.record(schemaName)
        .namespace(schemaNamespace)
        .fields();
    return schemaFieldAssembler;
  }

  private void extractEventProperties(List<EventProperty> properties,
                                    SchemaBuilder.FieldAssembler<Schema> schemaFieldAssembler)
        throws SpRuntimeException {
    for (EventProperty property : properties) {
      final String fieldName = property.getRuntimeName();
      if (!(property instanceof EventPropertyPrimitive)) {
        throw new UnsupportedOperationException("Unsupported data type: " + property.getClass());
      }
      initSchema(fieldName, ((EventPropertyPrimitive) property).getRuntimeType(), schemaFieldAssembler);
    }
  }

  private void initSchema(final String fieldName , final String uri,
                          SchemaBuilder.FieldAssembler<Schema> schemaFieldAssembler) {
    if (uri.equals(INT)) {
      schemaFieldAssembler.requiredInt(fieldName);
    } else if (uri.equals(LONG)) {
      schemaFieldAssembler.requiredLong(fieldName);
    } else if (uri.equals(FLOAT)) {
      schemaFieldAssembler.requiredFloat(fieldName);
    } else if (uri.equals(DOUBLE)) {
      schemaFieldAssembler.requiredDouble(fieldName);
    } else if (uri.equals(BOOLEAN)) {
      schemaFieldAssembler.requiredBoolean(fieldName);
    } else if (uri.equals(STRING)) {
      schemaFieldAssembler.requiredString(fieldName);
    } else {
      throw new UnsupportedOperationException("Unsupported data type: " + uri);
    }
  }

  private Path createNewParquetFilePath(String sourceDir, String parquetFileName) throws IOException {
    Path newParquetFilePath = new Path(sourceDir, parquetFileName + suffix);
    String path = newParquetFilePath.toString();
    int counter = 1;
    while (Files.exists(java.nio.file.Path.of(path))) {
      long index = setIndex();
      String filename = parquetFileName + "_" + index;
      newParquetFilePath = new Path(sourceDir, filename + suffix);
      path = newParquetFilePath.toString();
      counter++;
      if (counter > 5) {
        throw new SpRuntimeException("Failed to create new parquetFilePath after 5 attempts");
      }
    }
    log.info("Create new parquetFilePath: {}", newParquetFilePath);
    return newParquetFilePath;
  }

  private void resetParquetWriter() throws IOException {
    if (writer != null) {
      writer.close();
      log.info("Success to close parquetWriter, file path is {}, parquetFileSize is {}",
          newParquetFilePath.getName(), parquetFileSize);
    }
    parquetFileSize = 0;
    newParquetFilePath = createNewParquetFilePath(parquetGenerationDirectory, parquetFileName);
    try {
      writer = createParquetWriter(newParquetFilePath);
    } catch (SpRuntimeException e) {
      throw new SpRuntimeException("Failed to create ParquetWriter: " + e.getMessage());
    }
  }

  private ParquetWriter<GenericRecord> createParquetWriter(Path path) throws IOException {
    if (writer != null) {
      writer.close();
    }
    Configuration conf = new Configuration();
    OutputFile outputFile = HadoopOutputFile.fromPath(path, conf);
    return AvroParquetWriter.<GenericRecord>builder(outputFile)
        .withSchema(parquetSchema)
        .withCompressionCodec(compressionCodecName)
        .withRowGroupSize(rowGroupSize)
        .withPageSize(pageSize)
        .build();
  }

  private long setIndex() {
    return new Random().nextLong();
  }
}
