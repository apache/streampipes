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

package org.apache.streampipes.sinks.internal.jvm.dataset.migrations;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DatasetSinkMigrationV3Test {

  @Test
  void shouldExposeMigrationConfigAndPreserveInvocation() {
    var migration = new DatasetSinkMigrationV3();
    var config = migration.config();
    var invocation = Mockito.mock(DataSinkInvocation.class);
    var extractor = Mockito.mock(IDataSinkParameterExtractor.class);

    var actual = migration.migrate(invocation, extractor);

    Assertions.assertTrue(actual.success());
    Assertions.assertSame(invocation, actual.element());
    Assertions.assertEquals("org.apache.streampipes.sinks.internal.jvm.datalake", config.targetAppId());
    Assertions.assertEquals(SpServiceTagPrefix.DATA_SINK, config.modelType());
    Assertions.assertEquals(2, config.fromVersion());
    Assertions.assertEquals(3, config.toVersion());
  }
}
