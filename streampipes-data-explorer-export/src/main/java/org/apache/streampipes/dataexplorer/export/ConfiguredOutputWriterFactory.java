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
import org.apache.streampipes.storage.api.system.IFileMetadataStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;

public class ConfiguredOutputWriterFactory {

  private final IFileMetadataStorage fileMetadataStorage;
  private final ISpCoreConfigurationStorage coreConfigurationStorage;

  public ConfiguredOutputWriterFactory(IFileMetadataStorage fileMetadataStorage,
                                       ISpCoreConfigurationStorage coreConfigurationStorage) {
    this.fileMetadataStorage = fileMetadataStorage;
    this.coreConfigurationStorage = coreConfigurationStorage;
  }

  public ConfiguredOutputWriter getConfiguredWriter(DataLakeMeasure schema,
                                                    OutputFormat format,
                                                    ProvidedRestQueryParams params,
                                                    boolean ignoreMissingValues) {
    var writer = createWriter(format);
    writer.configure(schema, params, ignoreMissingValues);

    return writer;
  }

  private ConfiguredOutputWriter createWriter(OutputFormat format) {
    return switch (format) {
      case JSON -> new ConfiguredJsonOutputWriter();
      case CSV -> new ConfiguredCsvOutputWriter();
      case XLSX -> new ConfiguredExcelOutputWriter(fileMetadataStorage, coreConfigurationStorage);
    };
  }
}
