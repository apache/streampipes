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

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.io.LocalInputFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParquetSinkTest {

  private static final String FILE_NAME = "events";

  @TempDir
  Path tempDir;

  @Test
  void onEvent_directoryIsMissing_writesReadableParquetFile() throws IOException {
    Path directory = tempDir.resolve("missing").resolve("directory");

    writeEvent(directory);

    try (ParquetReader<GenericRecord> reader = openReader(directory.resolve(FILE_NAME + ".parquet"))) {
      GenericRecord record = reader.read();
      assertEquals(1L, record.get("timestamp"));
      assertEquals("value", record.get("name").toString());
      assertNull(reader.read());
    }
  }

  @Test
  void onPipelineStarted_fileExists_keepsExistingFile() throws IOException {
    Path existingFile = tempDir.resolve(FILE_NAME + ".parquet");
    byte[] existingContent = {1, 2, 3};
    Files.write(existingFile, existingContent);

    writeEvent(tempDir);

    assertArrayEquals(existingContent, Files.readAllBytes(existingFile));
    try (var files = Files.list(tempDir)) {
      assertEquals(2, files.count());
    }
  }

  private void writeEvent(Path directory) {
    ParquetSink sink = new ParquetSink();
    sink.onPipelineStarted(makeParameters(directory), null);
    sink.onEvent(makeEvent());
    sink.onPipelineStopped();
  }

  private ParquetReader<GenericRecord> openReader(Path file) throws IOException {
    return AvroParquetReader.<GenericRecord>builder(new LocalInputFile(file)).build();
  }

  private IDataSinkParameters makeParameters(Path directory) {
    IDataSinkParameterExtractor extractor = mock(IDataSinkParameterExtractor.class);
    when(extractor.singleValueParameter("schema_name", String.class)).thenReturn("record");
    when(extractor.singleValueParameter("schema_namespace", String.class)).thenReturn("org.apache.streampipes");
    when(extractor.singleValueParameter("parquet_file_name", String.class)).thenReturn(FILE_NAME);
    when(extractor.singleValueParameter("parquet_generation_directory", String.class))
        .thenReturn(directory.toString());
    when(extractor.singleValueParameter("row_group_size", Integer.class)).thenReturn(134217728);
    when(extractor.singleValueParameter("page_size", Integer.class)).thenReturn(1048576);
    when(extractor.selectedSingleValue("compression_codec_name", String.class)).thenReturn("GZIP");

    SpDataStream stream = new SpDataStream();
    stream.setEventSchema(new EventSchema(List.<EventProperty>of(
        makeProperty("timestamp", ParquetSink.LONG),
        makeProperty("name", ParquetSink.STRING))));
    DataSinkInvocation invocation = new DataSinkInvocation();
    invocation.setInputStreams(List.of(stream));

    IDataSinkParameters parameters = mock(IDataSinkParameters.class);
    when(parameters.extractor()).thenReturn(extractor);
    when(parameters.getModel()).thenReturn(invocation);
    return parameters;
  }

  private EventPropertyPrimitive makeProperty(String runtimeName, String runtimeType) {
    EventPropertyPrimitive property = new EventPropertyPrimitive();
    property.setRuntimeName(runtimeName);
    property.setRuntimeType(runtimeType);
    return property;
  }

  private Event makeEvent() {
    Event event = mock(Event.class, RETURNS_DEEP_STUBS);
    when(event.getRaw()).thenReturn(Map.of("timestamp", 1L, "name", "value"));
    when(event.getFieldByRuntimeName("timestamp").getAsPrimitive().getAsLong()).thenReturn(1L);
    when(event.getFieldByRuntimeName("name").getAsPrimitive().getAsString()).thenReturn("value");
    return event;
  }
}
