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

package org.apache.streampipes.connect;

import org.apache.streampipes.connect.adapters.image.stream.ImageStreamAdapter;
import org.apache.streampipes.connect.adapters.iss.IssAdapter;
import org.apache.streampipes.connect.adapters.netio.NetioMQTTAdapter;
import org.apache.streampipes.connect.adapters.netio.NetioRestAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.declarer.IExtensionModuleExport;
import org.apache.streampipes.extensions.api.migration.IModelMigrator;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;

import java.util.Collections;
import java.util.List;

public class GeneralAdaptersExtensionModuleExport implements IExtensionModuleExport {

  @Override
  public List<StreamPipesAdapter> adapters() {
    return List.of(
        new NetioRestAdapter(),
        new NetioMQTTAdapter(),
        new ImageStreamAdapter(),
        new IssAdapter()
    );
  }

  @Override
  public List<IStreamPipesPipelineElement<?>> pipelineElements() {
    return Collections.emptyList();
  }

  @Override
  public List<IModelMigrator<?, ?>> migrators() {
    return Collections.emptyList();
  }
}
