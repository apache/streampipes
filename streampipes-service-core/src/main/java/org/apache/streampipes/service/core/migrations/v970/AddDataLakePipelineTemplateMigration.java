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

package org.apache.streampipes.service.core.migrations.v970;

import org.apache.streampipes.manager.template.instances.PersistDataLakePipelineTemplate;
import org.apache.streampipes.model.template.CompactPipelineTemplate;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.io.IOException;

public class AddDataLakePipelineTemplateMigration implements Migration {

  private final CRUDStorage<CompactPipelineTemplate> storage;

  public AddDataLakePipelineTemplateMigration() {
    this.storage = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineTemplateStorage();
  }

  @Override
  public boolean shouldExecute() {
    return storage.getElementById(PersistDataLakePipelineTemplate.DATA_LAKE_TEMPLATE_ID) == null;
  }

  @Override
  public void executeMigration() throws IOException {
    storage.persist(new PersistDataLakePipelineTemplate().getTemplate());
  }

  @Override
  public String getDescription() {
    return "Adding data lake pipeline template to template storage";
  }
}
