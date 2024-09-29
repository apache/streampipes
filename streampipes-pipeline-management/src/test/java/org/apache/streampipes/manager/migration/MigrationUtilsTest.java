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
package org.apache.streampipes.manager.migration;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MigrationUtilsTest {

  List<ModelMigratorConfig> migrationConfigs = List.of(
          new ModelMigratorConfig("app-id", SpServiceTagPrefix.DATA_PROCESSOR, 0, 1),
          new ModelMigratorConfig("app-id", SpServiceTagPrefix.DATA_PROCESSOR, 1, 2),
          new ModelMigratorConfig("other-app-id", SpServiceTagPrefix.DATA_PROCESSOR, 0, 1),
          new ModelMigratorConfig("other-app-id", SpServiceTagPrefix.DATA_SINK, 1, 2));

  @Test
  public void findMigrations() {

    var pipelineElement1 = new DataProcessorInvocation();
    pipelineElement1.setAppId("app-id");
    pipelineElement1.setVersion(0);

    var pipelineElement2 = new DataProcessorInvocation();
    pipelineElement2.setAppId("app-id");
    pipelineElement2.setVersion(1);

    var pipelineElement3 = new DataProcessorInvocation();
    pipelineElement3.setAppId("other-app-id");
    pipelineElement3.setVersion(0);

    var pipelineElement4 = new DataSinkInvocation();
    pipelineElement4.setAppId("other-app-id");
    pipelineElement4.setVersion(0);

    Assertions.assertEquals(migrationConfigs.get(0),
            MigrationUtils.getApplicableMigration(pipelineElement1, migrationConfigs).get());
    Assertions.assertEquals(migrationConfigs.get(1),
            MigrationUtils.getApplicableMigration(pipelineElement2, migrationConfigs).get());
    Assertions.assertEquals(migrationConfigs.get(2),
            MigrationUtils.getApplicableMigration(pipelineElement3, migrationConfigs).get());
    Assertions.assertTrue(MigrationUtils.getApplicableMigration(pipelineElement4, migrationConfigs).isEmpty());
  }
}
