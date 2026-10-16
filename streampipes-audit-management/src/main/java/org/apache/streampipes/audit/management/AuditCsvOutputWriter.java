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

package org.apache.streampipes.audit.management;

import org.apache.streampipes.audit.api.AuditEntry;
import org.apache.streampipes.audit.api.AuditExportConfiguration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

final class AuditCsvOutputWriter implements AuditOutputWriter {
  private final AuditExportConfiguration configuration;
  private BufferedWriter writer;

  AuditCsvOutputWriter(AuditExportConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void beforeFirstItem(OutputStream outputStream) throws IOException {
    writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
    writeRow(configuration.headers());
  }

  @Override
  public void writeItem(AuditEntry entry) throws IOException {
    writeRow(configuration.columns().stream().map(column -> column.value(entry)).toList());
  }

  @Override
  public void afterLastItem(OutputStream outputStream) throws IOException {
    writer.flush();
  }

  private void writeRow(List<String> values) throws IOException {
    for (var index = 0; index < values.size(); index++) {
      if (index > 0) {
        writer.write(configuration.delimiter());
      }
      writer.write(escape(values.get(index)));
    }
    writer.write("\r\n");
  }

  private String escape(String value) {
    String escapedValue = value == null ? "" : value;
    if (!escapedValue.isEmpty() && "=+-@".indexOf(escapedValue.charAt(0)) >= 0) {
      escapedValue = "'" + escapedValue;
    }
    return "\"" + escapedValue.replace("\"", "\"\"") + "\"";
  }
}
