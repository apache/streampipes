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

package org.apache.streampipes.dataexplorer.management;

import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.dataexplorer.export.ConfiguredOutputWriter;
import org.apache.streampipes.model.dataset.DatasetMetadata;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.function.Function;

/** Writes all cursor batches without deriving continuation from timestamps or mutating the query. */
public final class DatasetExportService {
  private final DatasetQueryService queries;

  public DatasetExportService(DatasetQueryService queries) {
    this.queries = queries;
  }

  public void exportByName(String name, QuerySpec query, QueryExecutionOptions options,
                           Function<DatasetMetadata, ConfiguredOutputWriter> writerFactory,
                           OutputStream output) throws IOException {
    var dataset = queries.resolveByName(name);
    var writer = writerFactory.apply(dataset);
    try (var cursor = queries.open(dataset, query, options)) {
      writer.beforeFirstItem(output);
      boolean first = true;
      long count = 0;
      while (cursor.hasNext()) {
        var batch = cursor.next();
        var columns = new ArrayList<>(batch.columns());
        var tagColumns = batch.tags().keySet().stream().filter(tag -> !columns.contains(tag)).sorted().toList();
        columns.addAll(tagColumns);
        int time = columns.indexOf("time");
        if (time >= 0) {
          columns.set(time, dataset.getTimestampFieldName());
        }
        for (var row : batch.rows()) {
          if (options.ignoreMissingValues() && row.contains(null)) {
            continue;
          }
          if (options.maximumRows().isPresent() && count >= options.maximumRows().getAsInt()) {
            throw new IllegalArgumentException("Export exceeds maximum result rows");
          }
          var exportedRow = new ArrayList<>(row);
          if (time >= 0 && cursor.timestampFormat() != options.timestampFormat()) {
            exportedRow.set(time, options.timestampFormat().convert(row.get(time), cursor.timestampFormat()));
          }
          tagColumns.forEach(tag -> exportedRow.add(batch.tags().get(tag)));
          writer.writeItem(output, exportedRow, columns, first);
          first = false;
          count++;
        }
      }
      writer.afterLastItem(output);
    }
  }
}
