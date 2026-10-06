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
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;

import java.io.IOException;

public class MigrateDataLakePersistPipelineTemplateMigration implements Migration {

  private static final String PERSIST_TEMPLATE_ID = "sp-internal-persist";
  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  private static final String DATASET_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.dataset";

  private final ICompactPipelineTemplateStorage pipelineTemplateStorage;

  public MigrateDataLakePersistPipelineTemplateMigration(
      ICompactPipelineTemplateStorage pipelineTemplateStorage) {
    this.pipelineTemplateStorage = pipelineTemplateStorage;
  }

  @Override
  public boolean shouldExecute() {
    var template = pipelineTemplateStorage.getElementById(PERSIST_TEMPLATE_ID);
    return template != null && template.getPipeline().stream()
        .anyMatch(element -> DATA_LAKE_SINK_APP_ID.equals(element.id()));
  }

  @Override
  public void executeMigration() throws IOException {
    var template = pipelineTemplateStorage.getElementById(PERSIST_TEMPLATE_ID);
    if (template == null) {
      return;
    }

    template.setPipeline(template.getPipeline().stream()
        .map(this::migrateSinkId)
        .toList());
    pipelineTemplateStorage.updateElement(template);
  }

  @Override
  public String getDescription() {
    return "Migrate the Persist Data pipeline template to use the Dataset sink";
  }

  private CompactPipelineElement migrateSinkId(CompactPipelineElement element) {
    if (!DATA_LAKE_SINK_APP_ID.equals(element.id())) {
      return element;
    }

    return new CompactPipelineElement(
        element.type(),
        element.ref(),
        DATASET_SINK_APP_ID,
        element.connectedTo(),
        element.configuration(),
        element.output());
  }
}
