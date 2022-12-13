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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public abstract class ConfiguredOutputWriter {

  public static ConfiguredOutputWriter getConfiguredWriter(OutputFormat format,
                                                           ProvidedQueryParams params,
                                                           boolean ignoreMissingValues) {
    var writer = format.getWriter();
    writer.configure(params, ignoreMissingValues);

    return writer;
  }

  public abstract void configure(ProvidedQueryParams params,
                                 boolean ignoreMissingValues);

  public abstract void beforeFirstItem(OutputStream outputStream) throws IOException;

  public abstract void afterLastItem(OutputStream outputStream) throws IOException;

  public abstract void writeItem(OutputStream outputStream,
                                 List<Object> row,
                                 List<String> columnNames,
                                 boolean firstObject) throws IOException;

  protected byte[] toBytes(String value) {
    return value.getBytes();
  }
}
