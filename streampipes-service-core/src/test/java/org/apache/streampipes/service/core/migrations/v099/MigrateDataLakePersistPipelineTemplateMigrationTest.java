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

import org.apache.streampipes.model.pipeline.compact.CompactPipelineElement;
import org.apache.streampipes.model.template.CompactPipelineTemplate;
import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MigrateDataLakePersistPipelineTemplateMigrationTest {

  private static final String PERSIST_TEMPLATE_ID = "sp-internal-persist";
  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  private static final String DATASET_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.dataset";

  private ICompactPipelineTemplateStorage pipelineTemplateStorage;
  private MigrateDataLakePersistPipelineTemplateMigration migration;

  @BeforeEach
  void setUp() {
    pipelineTemplateStorage = mock(ICompactPipelineTemplateStorage.class);
    migration = new MigrateDataLakePersistPipelineTemplateMigration(pipelineTemplateStorage);
  }

  @Test
  void migratesLegacyDataLakeSinkInPersistTemplate() throws IOException {
    var template = templateWithSinkId(DATA_LAKE_SINK_APP_ID);
    when(pipelineTemplateStorage.getElementById(PERSIST_TEMPLATE_ID)).thenReturn(template);

    assertTrue(migration.shouldExecute());
    migration.executeMigration();

    assertEquals(DATASET_SINK_APP_ID, template.getPipeline().get(0).id());
    verify(pipelineTemplateStorage).updateElement(template);
  }

  @Test
  void doesNotExecuteWhenPersistTemplateAlreadyUsesDatasetSink() {
    when(pipelineTemplateStorage.getElementById(PERSIST_TEMPLATE_ID))
        .thenReturn(templateWithSinkId(DATASET_SINK_APP_ID));

    assertFalse(migration.shouldExecute());
  }

  private CompactPipelineTemplate templateWithSinkId(String sinkId) {
    var template = new CompactPipelineTemplate();
    template.setElementId(PERSIST_TEMPLATE_ID);
    template.setPipeline(List.of(new CompactPipelineElement(
        "sink", "lake", sinkId, List.of("stream1"), List.of(), null)));
    return template;
  }
}
