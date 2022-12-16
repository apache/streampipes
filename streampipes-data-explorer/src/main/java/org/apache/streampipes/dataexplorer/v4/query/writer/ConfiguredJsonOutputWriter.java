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

package org.apache.streampipes.dataexplorer.v4.query.writer;

import org.apache.streampipes.dataexplorer.v4.ProvidedQueryParams;
import org.apache.streampipes.dataexplorer.v4.query.writer.item.ItemGenerator;
import org.apache.streampipes.dataexplorer.v4.query.writer.item.JsonItemWriter;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ConfiguredJsonOutputWriter extends ConfiguredOutputWriter {

  private static final String BEGIN_ARRAY = "[";
  private static final String END_ARRAY = "]";

  private final ItemGenerator jsonObjectWriter;

  public ConfiguredJsonOutputWriter() {
    Gson gson = new Gson();
    this.jsonObjectWriter = new JsonItemWriter(gson);
  }

  @Override
  public void configure(ProvidedQueryParams params,
                        boolean ignoreMissingValues) {
    // do nothing
  }

  @Override
  public void beforeFirstItem(OutputStream outputStream) throws IOException {
    outputStream.write(toBytes(BEGIN_ARRAY));
  }

  @Override
  public void afterLastItem(OutputStream outputStream) throws IOException {
    outputStream.write(toBytes(END_ARRAY));
  }

  @Override
  public void writeItem(OutputStream outputStream,
                        List<Object> row,
                        List<String> columnNames,
                        boolean firstObject) throws IOException {
    if (!firstObject) {
      outputStream.write(toBytes(","));
    }

    var item = jsonObjectWriter.createItem(row, columnNames);
    outputStream.write(toBytes(item));
  }
}
