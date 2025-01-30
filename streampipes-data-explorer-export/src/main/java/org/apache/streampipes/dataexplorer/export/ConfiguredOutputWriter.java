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

package org.apache.streampipes.dataexplorer.export;

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.schema.EventProperty;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public abstract class ConfiguredOutputWriter {

  public static ConfiguredOutputWriter getConfiguredWriter(DataLakeMeasure schema,
                                                           OutputFormat format,
                                                           ProvidedRestQueryParams params,
                                                           boolean ignoreMissingValues) {
    var writer = format.getWriter();
    writer.configure(schema, params, ignoreMissingValues);

    return writer;
  }

  protected String getHeaderName(DataLakeMeasure schema,
                                 String runtimeName,
                                 String headerColumnNameStrategy) {
    if (Objects.nonNull(schema) && headerColumnNameStrategy.equals("label")) {
      return schema
          .getEventSchema()
          .getEventProperties()
          .stream()
          .filter(ep -> ep.getRuntimeName().equals(runtimeName))
          .findFirst()
          .map(ep -> extractLabel(ep, runtimeName))
          .orElse(runtimeName);
    } else {
      return runtimeName;
    }
  }

  private String extractLabel(EventProperty ep,
                              String runtimeName) {
    if (Objects.nonNull(ep.getLabel())) {
      return ep.getLabel();
    } else {
      return runtimeName;
    }
  }

  public abstract void configure(DataLakeMeasure schema,
                                 ProvidedRestQueryParams params,
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
