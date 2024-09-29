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
package org.apache.streampipes.rest.extensions.migration;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataProcessorMigrator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MigrateExtensionsResourceTest {

  @Test
  public void executeMigration() {
    var migrationsResource = new DataProcessorMigrationResource();

    var migrator = new IDataProcessorMigrator() {
      @Override
      public ModelMigratorConfig config() {
        return new ModelMigratorConfig("app-id", SpServiceTagPrefix.DATA_PROCESSOR, 0, 1);
      }

      @Override
      public MigrationResult<DataProcessorInvocation> migrate(DataProcessorInvocation element,
              IDataProcessorParameterExtractor extractor) throws RuntimeException {
        var properties = element.getStaticProperties();
        properties.add(StaticProperties.freeTextProperty(Labels.empty(), Datatypes.String));
        element.setStaticProperties(properties);
        return MigrationResult.success(element);
      }
    };

    var dataProcessor = new DataProcessorInvocation();
    dataProcessor.setStaticProperties(new ArrayList<>());

    var result = migrationsResource.executeMigration(migrator, dataProcessor);

    Assertions.assertTrue(result.success());
    Assertions.assertEquals("SUCCESS", result.message());
    Assertions.assertEquals(1, result.element().getVersion());
    Assertions.assertEquals(1, result.element().getStaticProperties().size());

  }

  @Test
  public void executeMigrationWithFailure() {
    var migrationsResource = new DataProcessorMigrationResource();

    var migrator = new IDataProcessorMigrator() {
      @Override
      public ModelMigratorConfig config() {
        return new ModelMigratorConfig("app-id", SpServiceTagPrefix.DATA_PROCESSOR, 0, 1);
      }

      @Override
      public MigrationResult<DataProcessorInvocation> migrate(DataProcessorInvocation element,
              IDataProcessorParameterExtractor extractor) throws RuntimeException {
        return MigrationResult.failure(element, "This should fail");
      }
    };

    var dataProcessor = new DataProcessorInvocation();

    var result = migrationsResource.executeMigration(migrator, dataProcessor);

    Assertions.assertFalse(result.success());
    Assertions.assertEquals("This should fail", result.message());
    Assertions.assertEquals(0, result.element().getVersion());
  }

  @Test
  public void executeMigrationWithUnknownFailure() {
    var migrationsResource = new DataProcessorMigrationResource();

    var migrator = new IDataProcessorMigrator() {
      @Override
      public ModelMigratorConfig config() {
        return new ModelMigratorConfig("app-id", SpServiceTagPrefix.DATA_PROCESSOR, 0, 1);
      }

      @Override
      public MigrationResult<DataProcessorInvocation> migrate(DataProcessorInvocation element,
              IDataProcessorParameterExtractor extractor) throws RuntimeException {
        throw new NullPointerException();
      }
    };

    var dataProcessor = new DataProcessorInvocation();

    var result = migrationsResource.executeMigration(migrator, dataProcessor);

    Assertions.assertFalse(result.success());
    Assertions.assertTrue(result.message().startsWith("Migration failed due to an unexpected exception:"));
    Assertions.assertEquals(0, result.element().getVersion());
  }
}
