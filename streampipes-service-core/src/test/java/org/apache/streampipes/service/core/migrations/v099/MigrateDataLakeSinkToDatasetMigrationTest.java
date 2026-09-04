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
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MigrateDataLakeSinkToDatasetMigrationTest {

  private IPipelineStorage pipelineStorage;
  private IDataSinkStorage dataSinkStorage;
  private MigrateDataLakeSinkToDatasetMigration migration;

  @BeforeEach
  void setUp() {
    pipelineStorage = mock(IPipelineStorage.class);
    dataSinkStorage = mock(IDataSinkStorage.class);
    migration = new MigrateDataLakeSinkToDatasetMigration(pipelineStorage, dataSinkStorage);
  }

  @Test
  void migratesPipelineInvocationsAndRemovesDataLakeDescription() throws IOException {
    var dataLakeSink = new DataSinkInvocation();
    dataLakeSink.setAppId(MigrateDataLakeSinkToDatasetMigration.DATA_LAKE_SINK_APP_ID);
    var otherSink = new DataSinkInvocation();
    otherSink.setAppId("org.apache.streampipes.sinks.notifications.jvm.email");
    var affectedPipeline = new Pipeline();
    affectedPipeline.setActions(List.of(dataLakeSink, otherSink));
    var unaffectedPipeline = new Pipeline();
    unaffectedPipeline.setActions(List.of(otherSink));
    var dataLakeDescription = new DataSinkDescription();
    dataLakeDescription.setAppId(MigrateDataLakeSinkToDatasetMigration.DATA_LAKE_SINK_APP_ID);

    when(pipelineStorage.findAll()).thenReturn(List.of(affectedPipeline, unaffectedPipeline));
    when(dataSinkStorage.getDataSinksByAppId(MigrateDataLakeSinkToDatasetMigration.DATA_LAKE_SINK_APP_ID))
        .thenReturn(List.of(dataLakeDescription));

    migration.executeMigration();

    assertEquals(MigrateDataLakeSinkToDatasetMigration.DATASET_SINK_APP_ID, dataLakeSink.getAppId());
    assertEquals("org.apache.streampipes.sinks.notifications.jvm.email", otherSink.getAppId());
    verify(pipelineStorage).updateElement(affectedPipeline);
    verify(pipelineStorage, never()).updateElement(unaffectedPipeline);
    verify(dataSinkStorage).deleteElement(dataLakeDescription);
  }

  @Test
  void executesWhenOnlyTheLegacyDescriptionExists() {
    var dataLakeDescription = new DataSinkDescription();
    when(pipelineStorage.findAll()).thenReturn(List.of());
    when(dataSinkStorage.getDataSinksByAppId(MigrateDataLakeSinkToDatasetMigration.DATA_LAKE_SINK_APP_ID))
        .thenReturn(List.of(dataLakeDescription));

    assertTrue(migration.shouldExecute());
  }

  @Test
  void doesNotExecuteWithoutLegacyDataLakeResources() {
    when(pipelineStorage.findAll()).thenReturn(List.of());
    when(dataSinkStorage.getDataSinksByAppId(MigrateDataLakeSinkToDatasetMigration.DATA_LAKE_SINK_APP_ID))
        .thenReturn(List.of());

    assertFalse(migration.shouldExecute());
  }
}
