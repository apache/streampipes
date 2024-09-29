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
package org.apache.streampipes.extensions.management.model;

import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpServiceDefinitionBuilderTest {

  @Test
  public void registerAdapter() {
    var expected = new TestAdapter();
    var result = SpServiceDefinitionBuilder.create("", "", "", 1).registerAdapter(expected).build();

    Assertions.assertEquals(1, result.getAdapters().size());
    Assertions.assertEquals(expected, result.getAdapters().get(0));
  }

  @Test
  public void registerMigration() {

    var migration1 = new TestMigration("app-id", 1, 2);
    var migration2 = new TestMigration("app-id", 0, 1);
    var migration3 = new TestMigration("other-id", 0, 1);
    var migration4 = new TestMigration("app-id", 0, 1);

    var result = SpServiceDefinitionBuilder.create("", "", "", 1)
            .registerMigrators(migration1, migration2, migration3, migration4).build();

    // assert de-duplication (last migration should not be registered)
    Assertions.assertEquals(3, result.getMigrators().size());

    // assert ordering
    Assertions.assertEquals(migration2, result.getMigrators().get(0));
    Assertions.assertEquals(migration1, result.getMigrators().get(1));
    Assertions.assertEquals(migration3, result.getMigrators().get(2));
  }

  private static class TestAdapter implements StreamPipesAdapter {

    @Override
    public IAdapterConfiguration declareConfig() {
      return null;
    }

    @Override
    public void onAdapterStarted(IAdapterParameterExtractor extractor, IEventCollector collector,
            IAdapterRuntimeContext adapterRuntimeContext) {

    }

    @Override
    public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext) {

    }

    @Override
    public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
            IAdapterGuessSchemaContext adapterGuessSchemaContext) {
      return null;
    }
  }

  private static class TestMigration implements IAdapterMigrator {

    String appId;
    int fromVersion;
    int toVersion;

    public TestMigration(String appId, int fromVersion, int toVersion) {
      this.appId = appId;
      this.fromVersion = fromVersion;
      this.toVersion = toVersion;
    }

    @Override
    public ModelMigratorConfig config() {
      return new ModelMigratorConfig(appId, SpServiceTagPrefix.ADAPTER, fromVersion, toVersion);
    }

    @Override
    public MigrationResult<AdapterDescription> migrate(AdapterDescription element, IStaticPropertyExtractor extractor)
            throws RuntimeException {
      return null;
    }
  }
}
