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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class RenameDataLakeSinkLabelsMigration implements Migration {

  static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  static final String NEW_SINK_NAME = "Dataset";
  static final String NEW_SINK_DESCRIPTION = "Stores events in the internal dataset.";

  private final IPipelineStorage pipelineStorage;
  private final IDataSinkStorage dataSinkStorage;

  public RenameDataLakeSinkLabelsMigration(IPipelineStorage pipelineStorage,
                                           IDataSinkStorage dataSinkStorage) {
    this.pipelineStorage = pipelineStorage;
    this.dataSinkStorage = dataSinkStorage;
  }

  public RenameDataLakeSinkLabelsMigration(IPipelineStorage pipelineStorage) {
    this(pipelineStorage, StorageDispatcher.INSTANCE.getNoSqlStore().getDataSinkStorage());
  }

  @Override
  public boolean shouldExecute() {
    return dataSinkStorage.getDataSinksByAppId(DATA_LAKE_SINK_APP_ID)
        .stream()
        .anyMatch(this::requiresUpdate)
        || pipelineStorage.findAll()
        .stream()
        .anyMatch(this::requiresPipelineUpdate);
  }

  @Override
  public void executeMigration() throws IOException {
    migrateSinkDescriptions();
    migratePipelines();
  }

  @Override
  public String getDescription() {
    return "Rename data lake sink labels to dataset";
  }

  private void migrateSinkDescriptions() {
    dataSinkStorage.getDataSinksByAppId(DATA_LAKE_SINK_APP_ID)
        .stream()
        .filter(this::requiresUpdate)
        .forEach(dataSink -> {
          applyUpdatedLabels(dataSink);
          dataSinkStorage.updateElement(dataSink);
        });
  }

  private void migratePipelines() {
    pipelineStorage.findAll()
        .stream()
        .filter(this::requiresPipelineUpdate)
        .forEach(pipeline -> {
          pipeline.getActions()
              .stream()
              .filter(action -> DATA_LAKE_SINK_APP_ID.equals(action.getAppId()))
              .forEach(this::applyUpdatedLabels);
          pipelineStorage.updateElement(pipeline);
        });
  }

  private boolean requiresPipelineUpdate(Pipeline pipeline) {
    return pipeline.getActions() != null
        && pipeline.getActions()
        .stream()
        .filter(action -> DATA_LAKE_SINK_APP_ID.equals(action.getAppId()))
        .anyMatch(this::requiresUpdate);
  }

  private boolean requiresUpdate(DataSinkDescription dataSink) {
    return !Objects.equals(NEW_SINK_NAME, dataSink.getName())
        || !Objects.equals(NEW_SINK_DESCRIPTION, dataSink.getDescription());
  }

  private boolean requiresUpdate(DataSinkInvocation dataSink) {
    return !Objects.equals(NEW_SINK_NAME, dataSink.getName())
        || !Objects.equals(NEW_SINK_DESCRIPTION, dataSink.getDescription());
  }

  private void applyUpdatedLabels(DataSinkDescription dataSink) {
    dataSink.setName(NEW_SINK_NAME);
    dataSink.setDescription(NEW_SINK_DESCRIPTION);
  }

  private void applyUpdatedLabels(DataSinkInvocation dataSink) {
    dataSink.setName(NEW_SINK_NAME);
    dataSink.setDescription(NEW_SINK_DESCRIPTION);
  }
}
