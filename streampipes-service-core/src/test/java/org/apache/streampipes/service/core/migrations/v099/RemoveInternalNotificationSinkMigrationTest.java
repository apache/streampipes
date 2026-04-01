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
import org.apache.streampipes.model.pipeline.PipelineHealthStatus;
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

class RemoveInternalNotificationSinkMigrationTest {

  private IPipelineStorage pipelineStorage;
  private IDataSinkStorage dataSinkStorage;
  private RemoveInternalNotificationSinkMigration migration;
  private boolean databaseExists;
  private boolean databaseRemoved;

  @BeforeEach
  void setUp() {
    this.pipelineStorage = mock(IPipelineStorage.class);
    this.dataSinkStorage = mock(IDataSinkStorage.class);
    this.databaseExists = false;
    this.databaseRemoved = false;
    this.migration = new RemoveInternalNotificationSinkMigration(pipelineStorage, dataSinkStorage) {
      @Override
      protected boolean databaseExists() {
        return RemoveInternalNotificationSinkMigrationTest.this.databaseExists;
      }

      @Override
      protected void removeDatabase() {
        RemoveInternalNotificationSinkMigrationTest.this.databaseRemoved = true;
      }
    };
  }

  @Test
  void shouldExecuteReturnsTrueWhenNotificationDatabaseExists() {
    this.databaseExists = true;
    when(pipelineStorage.findAll()).thenReturn(List.of());
    when(dataSinkStorage.getDataSinksByAppId(RemoveInternalNotificationSinkMigration.INTERNAL_NOTIFICATION_SINK_APP_ID))
        .thenReturn(List.of());

    assertTrue(migration.shouldExecute());
  }

  @Test
  void executeMigrationRemovesDeprecatedSinkAndMarksPipeline() throws IOException {
    var deprecatedSink = new DataSinkInvocation();
    deprecatedSink.setAppId(RemoveInternalNotificationSinkMigration.INTERNAL_NOTIFICATION_SINK_APP_ID);

    var retainedSink = new DataSinkInvocation();
    retainedSink.setAppId("org.apache.streampipes.sinks.notifications.jvm.email");

    var affectedPipeline = new Pipeline();
    affectedPipeline.setPipelineId("pipeline-1");
    affectedPipeline.setActions(List.of(deprecatedSink, retainedSink));
    affectedPipeline.setRunning(true);
    affectedPipeline.setValid(true);

    var unaffectedPipeline = new Pipeline();
    unaffectedPipeline.setPipelineId("pipeline-2");
    unaffectedPipeline.setActions(List.of(retainedSink));
    unaffectedPipeline.setRunning(true);
    unaffectedPipeline.setValid(true);

    var sinkDescription = new DataSinkDescription();
    sinkDescription.setAppId(RemoveInternalNotificationSinkMigration.INTERNAL_NOTIFICATION_SINK_APP_ID);

    when(pipelineStorage.findAll()).thenReturn(List.of(affectedPipeline, unaffectedPipeline));
    when(dataSinkStorage.getDataSinksByAppId(RemoveInternalNotificationSinkMigration.INTERNAL_NOTIFICATION_SINK_APP_ID))
        .thenReturn(List.of(sinkDescription));

    migration.executeMigration();

    assertEquals(1, affectedPipeline.getActions().size());
    assertEquals("org.apache.streampipes.sinks.notifications.jvm.email",
        affectedPipeline.getActions().get(0).getAppId());
    assertFalse(affectedPipeline.isRunning());
    assertFalse(affectedPipeline.isValid());
    assertEquals(PipelineHealthStatus.REQUIRES_ATTENTION, affectedPipeline.getHealthStatus());
    assertEquals(List.of(RemoveInternalNotificationSinkMigration.PIPELINE_WARNING),
        affectedPipeline.getPipelineNotifications());

    verify(pipelineStorage).updateElement(affectedPipeline);
    verify(pipelineStorage, never()).updateElement(unaffectedPipeline);
    verify(dataSinkStorage).deleteElement(sinkDescription);
    assertTrue(databaseRemoved);
  }
}
