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

import org.apache.streampipes.manager.recommender.AllElementsProvider;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.connection.Connection;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.List;

public class ConnectionStorageHandler {

  private AllElementsProvider elementsProvider;
  private Pipeline pipeline;

  public ConnectionStorageHandler(Pipeline pipeline) {
    this.pipeline = pipeline;
    this.elementsProvider = new AllElementsProvider(pipeline);
  }

  public void storeConnections() {
    List<Connection> connections = new ArrayList<>();
    pipeline.getActions().forEach(sink -> findConnections(sink, connections));

    connections.forEach(connection -> StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getConnectionStorageApi()
        .addConnection(connection));
  }

  private void findConnections(NamedStreamPipesEntity target,
                               List<Connection> connections) {
    if (target instanceof DataSinkInvocation || target instanceof DataProcessorInvocation) {
      InvocableStreamPipesEntity pipelineElement = (InvocableStreamPipesEntity) target;
      pipelineElement.getConnectedTo().forEach(conn -> {
        NamedStreamPipesEntity source = this.elementsProvider.findElement(conn);
        String sourceId;
        if (source instanceof SpDataStream) {
          sourceId = source.getElementId();
        } else {
          sourceId = ((InvocableStreamPipesEntity) source).getBelongsTo();
        }
        connections.add(new Connection(sourceId, ((InvocableStreamPipesEntity) target).getBelongsTo()));
        findConnections(source, connections);
      });
    }
  }
}
