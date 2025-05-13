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

package org.apache.streampipes.sinks.databases.jvm.tsfile;

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
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.vocabulary.XSD;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import org.apache.tsfile.enums.TSDataType;
import org.apache.tsfile.exception.write.WriteProcessException;
import org.apache.tsfile.read.common.Path;
import org.apache.tsfile.write.TsFileWriter;
import org.apache.tsfile.write.record.Tablet;
import org.apache.tsfile.write.schema.MeasurementSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TsFileSink extends StreamPipesDataSink {

  private static final Logger log = LoggerFactory.getLogger(TsFileSink.class);
  public static final String DEVICE_ID_KEY = "device_id";
  public static final String TSFILE_NAME_KEY = "tsfile_name";
  public static final String TIMESTAMP_MAPPING_KEY = "timestamp_mapping";
  //To tell where the tsfile is stored.Like "/home/user/".
  public static final String TSFILE_GENERATION_DIRECTORY_KRY = "tsfile_generation_directory";
  //Set the max size of the tsfile.
  public static final String MAX_TSFILE_SIZE_KEY = "max_tsfile_size";
  //Set when should the tsfile be flushed to disk.
  public static final String MAX_FLUSH_DISK_SIZE_KEY = "max_flush_disk_size";
  public static final String ALIGNED = "aligned";

  private static final String suffix = ".tsfile";

  //XSD DataType
  public static final String LONG = XSD.LONG.toString();
  public static final String INIEGER =  XSD.INTEGER.toString();
  public static final String FLOAT = XSD.FLOAT.toString();
  public static final String DOUBLE = XSD.DOUBLE.toString();
  public static final String BOOLEAN = XSD.BOOLEAN.toString();
  public static final String STRING = XSD.STRING.toString();

  //XSD DataType Size
  public static final int BOOLEAN_SIZE = 8;
  public static final int INIEGER_SIZE = 32;
  public static final int LONG_SIZE = 64;
  public static final int FLOAT_SIZE = 32;
  public static final int DOUBLE_SIZE = 64;

  private long maxTsFileSize = 1024L * 1024 * 10;
  private long maxFlushDiskSize = Long.MAX_VALUE;
  private TsFileWriter tsFileWriter;
  private String tsFileName;
  private String deviceId;
  private String timestampFieldId;
  private String dirAbsolutePath;
  //The timestamp of TsFile should be increased.This field is used to check.
  private long maxTime;
  private File newTsFile;
  private List<MeasurementSchema> schemas;
  //The size of the tsfile written to disk.
  private long writeSize = 0;
  //The total size of the tsfile written to disk.
  private long totalWriteSize = 0;
  private boolean aligned = false;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder
        .create("org.apache.streampipes.sinks.databases.jvm.tsfile", 0)
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON).
        category(DataSinkType.DATABASE)
        .requiredTextParameter(Labels.withId(TSFILE_NAME_KEY))
        .requiredTextParameter(Labels.withId(DEVICE_ID_KEY))
        .requiredTextParameter(Labels.withId(TSFILE_GENERATION_DIRECTORY_KRY))
        .requiredLongParameter(Labels.withId(MAX_TSFILE_SIZE_KEY), 1024L * 1024 * 10)
        .requiredLongParameter(Labels.withId(MAX_FLUSH_DISK_SIZE_KEY), Long.MAX_VALUE)
        .requiredSingleValueSelection(Labels.withId(ALIGNED), Options.from("False", "True"))
        .requiredStream(
                StreamRequirementsBuilder.create()
                        .requiredPropertyWithUnaryMapping
                                (EpRequirements.timestampReq(), Labels.withId(TIMESTAMP_MAPPING_KEY),
                                PropertyScope.NONE).build()
        )
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    this.tsFileName = parameters.extractor().singleValueParameter(TSFILE_NAME_KEY, String.class);
    this.deviceId = parameters.extractor().singleValueParameter(DEVICE_ID_KEY, String.class);
    this.dirAbsolutePath = parameters.extractor().singleValueParameter(TSFILE_GENERATION_DIRECTORY_KRY, String.class);
    this.timestampFieldId = parameters.extractor().mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
    this.maxTsFileSize = parameters.extractor().singleValueParameter(MAX_TSFILE_SIZE_KEY, Long.class);
    this.maxFlushDiskSize = parameters.extractor().singleValueParameter(MAX_FLUSH_DISK_SIZE_KEY, Long.class);
    this.aligned = parameters.extractor().selectedSingleValue(ALIGNED, String.class).equals("true");

    try {
      newTsFile = createTsFile(dirAbsolutePath, tsFileName);
      this.tsFileWriter = new TsFileWriter(newTsFile);
    } catch (IOException e) {
      throw new SpRuntimeException("Failed to close tsFileWriter when init Pipeline", e);
    }
    schemas = new ArrayList<>();
    EventSchema schema = parameters.getModel().getInputStreams().get(0).getEventSchema();
    this.extractEventProperties(schema.getEventProperties(), "", schemas);
    tsFileWriter.registerTimeseries(new Path(deviceId), schemas);
  }

  @Override
  public void onDetach() {
    try {
      if (tsFileWriter != null){
        tsFileWriter.close();
        log.info("success to close tsFileSink");
      }
    } catch (IOException e) {
      log.warn("Failed to close tsFileWriter", e);
      throw new SpRuntimeException("Failed to close tsFileWriter when close tsFileSink", e);
    }
  }

  @Override
  public void onEvent(Event event) {

    if (event == null) {
      log.info("Received null event");
      return;
    }

    final AbstractField timestampAbstractField = event.getFieldBySelector(timestampFieldId);
    final Long timestamp = timestampAbstractField.getAsPrimitive().getAsLong();
    if (timestamp == null) {
      log.info("Received event with null timestamp");
      return;
    }

    final Map<String, Object> measurementValuePairs = event.getRaw();
    // should be at least a timestamp field and a measurement field
    if (measurementValuePairs.size() <= 1) {
      log.info("Received event with insufficient measurement value pairs");
      return;
    }

    Tablet tablet = new Tablet(deviceId, schemas, 1);
    /*
     We need to know the size of the file to determine the timing of flashing data to disk and
     creating a new file when the file is too large.
     However, newTsFile. length() cannot return the actual file size,
     so we use size to estimate the file size. For example,
     if we write a Boolean type data that occupies 8 bytes, we add 8 to achieve this goal
     */
    int size = 0;

    tablet.timestamps[0] = timestamp;
    for (int i = 0; i < schemas.size(); i++) {
      MeasurementSchema schema = tablet.getSchemas().get(i);
      AbstractField fieldByRuntimeName = event.getFieldByRuntimeName(schema.getMeasurementId());
      if (fieldByRuntimeName == null){
        tablet.bitMaps[i].mark(0);
        continue;
      }
      switch (schema.getType()){
        case BOOLEAN:
          size += BOOLEAN_SIZE;
          ((boolean[]) tablet.values[i])[0] = fieldByRuntimeName.getAsPrimitive().getAsBoolean();
          break;
        case INT32:
          size += INIEGER_SIZE;
          ((int[]) tablet.values[i])[0] = fieldByRuntimeName.getAsPrimitive().getAsInt();
          break;
        case INT64:
          size += LONG_SIZE;
          ((long[]) tablet.values[i])[0] = fieldByRuntimeName.getAsPrimitive().getAsLong();
          break;
        case FLOAT:
          size += FLOAT_SIZE;
          ((float[]) tablet.values[i])[0] = fieldByRuntimeName.getAsPrimitive().getAsFloat();
          break;
        case DOUBLE:
          size += DOUBLE_SIZE;
          ((double[]) tablet.values[i])[0] = fieldByRuntimeName.getAsPrimitive().getAsDouble();
          break;
        case STRING:
          String sValue = fieldByRuntimeName.getAsPrimitive().getAsString();
          size += sValue.length();
          ((String[]) tablet.values[i])[0] = sValue;
          break;
        default:
          throw new UnsupportedOperationException("Unsupported data type: " + schema.getType());
      }
    }

    try {
      if (maxTime > timestamp) {
        log.info("The file size did not reach the expected size, "
                 + "but due to the time taken to write the measurement point being less than the previous writing time,"
                 + " the file needs to be closed in advance");
        resetTsFileWriter();
        maxTime = Long.MIN_VALUE;
      }
      try {
        if (aligned){
          tsFileWriter.writeAligned(tablet);
        } else {
          tsFileWriter.write(tablet);
        }
        totalWriteSize += size;
        writeSize += size;
        maxTime = timestamp;
      } catch (WriteProcessException | IOException e) {
        resetTsFileWriter();
        throw new SpRuntimeException("Failed to write TSRecord", e);
      }
      if (totalWriteSize >= maxTsFileSize) {
        resetTsFileWriter();
        return;
      }
      if (writeSize >= maxFlushDiskSize) {
        tsFileWriter.flushAllChunkGroups();
        writeSize = 0;
      }
    } catch (IOException e) {
      throw new SpRuntimeException("Failed to resetTsFileWriter" , e);
    }
  }

  private void extractEventProperties(List<EventProperty> properties,
                                      String preProperty, List<MeasurementSchema> schemas)
        throws SpRuntimeException {
    for (EventProperty property : properties) {
      final String measurementId = preProperty + property.getRuntimeName();
      if (!(property instanceof EventPropertyPrimitive)) {
        throw new UnsupportedOperationException("Unsupported data type: " + property.getClass());
      }
      initMeasurement(measurementId, ((EventPropertyPrimitive) property).getRuntimeType(), schemas);
    }
  }

  private void initMeasurement(final String measurementId , final String uri, List<MeasurementSchema> schemas) {
    if (uri.equals(BOOLEAN)) {
      schemas.add(new MeasurementSchema(measurementId, TSDataType.BOOLEAN));
    } else if (uri.equals(INIEGER)) {
      schemas.add(new MeasurementSchema(measurementId, TSDataType.INT32));
    } else if (uri.equals(LONG)) {
      schemas.add(new MeasurementSchema(measurementId, TSDataType.INT64));
    } else if (uri.equals(FLOAT)) {
      schemas.add(new MeasurementSchema(measurementId, TSDataType.FLOAT));
    } else if (uri.equals(DOUBLE)) {
      schemas.add(new MeasurementSchema(measurementId, TSDataType.DOUBLE));
    } else if (uri.equals(STRING)){
      schemas.add(new MeasurementSchema(measurementId, TSDataType.STRING));
    } else {
      throw new UnsupportedOperationException("Unsupported data type: " + uri);
    }
  }

  private File createTsFile(String sourceDir, String tsFileName) throws IOException {
    File tsfile = new File(sourceDir, tsFileName + suffix);
    int counter = 1;
    while (tsfile.exists()) {
      String filename = tsFileName;
      //use index to create a new tsfile with different name
      long index = setIndex();
      filename = filename + "_" + index;
      tsfile = new File(sourceDir, filename + suffix);
      while (tsfile.exists()) {
        index++;
        filename = tsFileName + "_" + index;
        tsfile = new File(sourceDir, filename + suffix);
      }
      if (!tsfile.createNewFile()){
        log.info("The number of times tsfile is created ：" + counter);
        if (counter > 5){
          throw new SpRuntimeException("Failed to create new tsfile after 5 attempts");
        }
        counter++;
        continue;
      }
      log.info("Create new tsfile: " + tsfile.getAbsolutePath());
      return tsfile;
    }
    //The log of first createNewFile
    if (tsfile.createNewFile()){
      log.info("Create new tsfile: " + tsfile.getAbsolutePath());
    } else {
      throw new SpRuntimeException("Failed to create new tsfile");
    }
    return tsfile;
  }

  private void resetTsFileWriter() throws IOException {
    if (tsFileWriter != null){
      tsFileWriter.close();
      log.info("Success to close tsFileWriter, file name is {}, totalWriteSize is {}",
              newTsFile.getName(), totalWriteSize);
    }
    totalWriteSize = 0;
    writeSize = 0;
    newTsFile = createTsFile(dirAbsolutePath, tsFileName);
    tsFileWriter = new TsFileWriter(newTsFile);
    tsFileWriter.registerTimeseries(new Path(deviceId), schemas);
  }

  private long setIndex() {
    return new Random().nextLong();
  }
}
