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
import org.apache.streampipes.dataexplorer.v4.query.writer.item.CsvItemWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.StringJoiner;

import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_CSV_DELIMITER;

public class ConfiguredCsvOutputWriter extends ConfiguredOutputWriter {

  private static final String LINE_SEPARATOR = "\n";
  private static final String COMMA = ",";
  private static final String SEMICOLON = ";";

  private CsvItemWriter itemWriter;
  private String delimiter = COMMA;

  @Override
  public void configure(ProvidedQueryParams params,
                        boolean ignoreMissingValues) {
    if (params.has(QP_CSV_DELIMITER)) {
      delimiter = params.getAsString(QP_CSV_DELIMITER).equals("comma") ? COMMA : SEMICOLON;
    }
    this.itemWriter = new CsvItemWriter(delimiter);
  }

  @Override
  public void beforeFirstItem(OutputStream outputStream) {
    // do nothing
  }

  @Override
  public void afterLastItem(OutputStream outputStream) {
    // do nothing
  }

  @Override
  public void writeItem(OutputStream outputStream,
                        List<Object> row,
                        List<String> columnNames,
                        boolean firstObject) throws IOException {
    if (firstObject) {
      outputStream.write(toBytes(makeHeaderLine(columnNames)));
    }

    outputStream.write(toBytes(itemWriter.createItem(row, columnNames) + LINE_SEPARATOR));
  }

  private String makeHeaderLine(List<String> columns) {
    StringJoiner joiner = new StringJoiner(this.delimiter);
    columns.forEach(joiner::add);
    return joiner + LINE_SEPARATOR;
  }
}
