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
package org.apache.streampipes.manager.matching;

import org.apache.streampipes.manager.util.TreeUtils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.connection.Connection;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;

public class ConnectionStorageHandler {

  private Pipeline pipeline;
  private InvocableStreamPipesEntity rootPipelineElement;

  public ConnectionStorageHandler(Pipeline pipeline,
                                  InvocableStreamPipesEntity rootPipelineElement) {
    this.pipeline = pipeline;
    this.rootPipelineElement = rootPipelineElement;
  }

  public void storeConnection() {
    String fromId = rootPipelineElement.getConnectedTo().get(rootPipelineElement.getConnectedTo().size() - 1);
    NamedStreamPipesEntity sepaElement = TreeUtils.findSEPAElement(fromId, pipeline.getSepas(), pipeline.getStreams());
    String sourceId;
    if (sepaElement instanceof SpDataStream) {
      sourceId = sepaElement.getElementId();
    } else {
      sourceId = ((InvocableStreamPipesEntity) sepaElement).getBelongsTo();
    }
    Connection connection = new Connection(sourceId, rootPipelineElement.getBelongsTo());
    StorageDispatcher.INSTANCE.getNoSqlStore().getConnectionStorageApi().addConnection(connection);
  }
}
