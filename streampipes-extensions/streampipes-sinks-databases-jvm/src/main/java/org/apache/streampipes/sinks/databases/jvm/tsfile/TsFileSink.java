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
import org.apache.streampipes.vocabulary.XSD;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import org.apache.tsfile.enums.TSDataType;
import org.apache.tsfile.exception.write.WriteProcessException;
import org.apache.tsfile.read.common.Path;
import org.apache.tsfile.write.TsFileWriter;
import org.apache.tsfile.write.record.TSRecord;
import org.apache.tsfile.write.record.datapoint.DataPoint;
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
  public static final String DIR_ABSOLUTE_PATH_KEY = "dir_absolute_path";
  //Set the max size of the tsfile.
  public static final String MAX_TSFILE_SIZE_KEY = "max_tsfile_size";
  //Set when should the tsfile be flushed to disk.
  public static final String MAX_FLUSH_DISK_SIZE_KEY = "max_flush_disk_size";

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

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder
        .create("org.apache.streampipes.sinks.databases.jvm.tsfile", 0)
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON).
        category(DataSinkType.DATABASE)
        .requiredTextParameter(Labels.withId(TSFILE_NAME_KEY))
        .requiredTextParameter(Labels.withId(DEVICE_ID_KEY))
        .requiredTextParameter(Labels.withId(DIR_ABSOLUTE_PATH_KEY))
        .requiredLongParameter(Labels.withId(MAX_TSFILE_SIZE_KEY), 1024L * 1024 * 10)
        .requiredLongParameter(Labels.withId(MAX_FLUSH_DISK_SIZE_KEY), Long.MAX_VALUE)
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
    this.dirAbsolutePath = parameters.extractor().singleValueParameter(DIR_ABSOLUTE_PATH_KEY, String.class);
    this.timestampFieldId = parameters.extractor().mappingPropertyValue(TIMESTAMP_MAPPING_KEY);
    this.maxTsFileSize = parameters.extractor().singleValueParameter(MAX_TSFILE_SIZE_KEY, Long.class);
    this.maxFlushDiskSize = parameters.extractor().singleValueParameter(MAX_FLUSH_DISK_SIZE_KEY, Long.class);

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

    TSRecord tsRecord = new TSRecord(timestamp, deviceId); // init tsRecord
    int size = 0;
    for (Map.Entry<String, Object> measurementValuePair : measurementValuePairs.entrySet()) {
      if (measurementValuePair.getKey().equals(timestampFieldId)) {
        continue;
      }
      final String measurementId = measurementValuePair.getKey();
      final Object value = measurementValuePair.getValue();

      if (value instanceof Boolean) {
        size += BOOLEAN_SIZE;
        tsRecord.addTuple(DataPoint.getDataPoint(TSDataType.BOOLEAN, measurementId, String.valueOf(value)));
      } else if (value instanceof Integer) {
        size += INIEGER_SIZE;
        tsRecord.addTuple(DataPoint.getDataPoint(TSDataType.INT32, measurementId, String.valueOf(value)));
      } else if (value instanceof Long) {
        size += LONG_SIZE;
        tsRecord.addTuple(DataPoint.getDataPoint(TSDataType.INT64, measurementId, String.valueOf(value)));
      } else if (value instanceof Float) {
        size += FLOAT_SIZE;
        tsRecord.addTuple(DataPoint.getDataPoint(TSDataType.FLOAT, measurementId, String.valueOf(value)));
      } else if (value instanceof Double) {
        size += DOUBLE_SIZE;
        tsRecord.addTuple(DataPoint.getDataPoint(TSDataType.DOUBLE, measurementId, String.valueOf(value)));
      } else if (value instanceof String) {
        String sValue = String.valueOf(value);
        size += sValue.length();
        tsRecord.addTuple(DataPoint.getDataPoint(TSDataType.STRING, measurementId, String.valueOf(value)));
      } else {
        throw new UnsupportedOperationException("Unsupported data type: " + value.getClass());
      }
    }

    try {
      if (maxTime > timestamp) {
        log.info("timestamp fault");
        resetTsFileWriter();
        maxTime = Long.MIN_VALUE;
      }
      try {
        tsFileWriter.write(tsRecord);
        totalWriteSize += size;
        writeSize += size;
        maxTime = timestamp;
      } catch (WriteProcessException | IOException e) {
        resetTsFileWriter();
        throw new SpRuntimeException("Failed to write TSRecord", e);
      }
      if (totalWriteSize >= maxTsFileSize) {
        resetTsFileWriter();
        log.info("Success to reset tsFileWriter");
        return;
      }
      if (writeSize >= maxFlushDiskSize) {
        tsFileWriter.flushAllChunkGroups();
        writeSize = 0;
        log.info("Success to flush tsFileWriter,totalWriteSize is " + totalWriteSize);
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
      log.info("Success to close tsFileWriter,writeSize is " + writeSize + " and totalWriteSize is " + totalWriteSize);
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
