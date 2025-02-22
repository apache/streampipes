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

import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.Arrays;
import java.util.function.Supplier;

public enum OutputFormat {
  JSON(ConfiguredJsonOutputWriter::new),
  CSV(ConfiguredCsvOutputWriter::new),
  XLSX(() -> new ConfiguredExcelOutputWriter(StorageDispatcher.INSTANCE.getNoSqlStore().getFileMetadataStorage()));

  private final Supplier<ConfiguredOutputWriter> writerSupplier;

  OutputFormat(Supplier<ConfiguredOutputWriter> writerSupplier) {
    this.writerSupplier = writerSupplier;
  }

  public ConfiguredOutputWriter getWriter() {
    return writerSupplier.get();
  }

  public static OutputFormat fromString(String desiredFormat) {
    return Arrays.stream(
            OutputFormat.values())
        .filter(format -> format.name().equalsIgnoreCase(desiredFormat))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(String.format("Could not find format %s", desiredFormat)));
  }
}
