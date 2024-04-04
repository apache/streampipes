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

package org.apache.streampipes.extensions.connectors.opcua;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.declarer.IExtensionModuleExport;
import org.apache.streampipes.extensions.api.migration.IModelMigrator;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaAdapter;
import org.apache.streampipes.extensions.connectors.opcua.migration.OpcUaAdapterMigrationV1;
import org.apache.streampipes.extensions.connectors.opcua.migration.OpcUaAdapterMigrationV2;
import org.apache.streampipes.extensions.connectors.opcua.sink.OpcUaSink;

import java.util.List;

public class OpcUaConnectorsModuleExport implements IExtensionModuleExport {
  @Override
  public List<StreamPipesAdapter> adapters() {
    return List.of(
        new OpcUaAdapter()
    );
  }

  @Override
  public List<IStreamPipesPipelineElement<?>> pipelineElements() {
    return List.of(
        new OpcUaSink()
    );
  }

  @Override
  public List<IModelMigrator<?, ?>> migrators() {
    return List.of(
        new OpcUaAdapterMigrationV1(),
        new OpcUaAdapterMigrationV2()
    );
  }
}
