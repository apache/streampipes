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
package org.apache.streampipes.sinks.internal.jvm.datalake.migrations;

import static org.mockito.Mockito.mock;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sinks.internal.jvm.datalake.DataLakeSink;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataLakeSinkMigrationV1Test {

  @Test
  public void migrate() {
    var dataLakeSinkMigrationV1 = new DataLakeSinkMigrationV1();

    var extractor = mock(DataSinkParameterExtractor.class);
    var invocation = new DataSinkInvocation();
    invocation.setStaticProperties(new ArrayList<>());

    var actual = dataLakeSinkMigrationV1.migrate(invocation, extractor);

    Assertions.assertTrue(actual.success());
    Assertions.assertEquals(actual.element().getStaticProperties().size(), 1);
    var schemaUpdateStaticProperty = getOneOfStaticProperty(actual);
    Assertions.assertEquals(schemaUpdateStaticProperty.getInternalName(), DataLakeSink.SCHEMA_UPDATE_KEY);
    Assertions.assertEquals(schemaUpdateStaticProperty.getOptions().get(0).isSelected(), true);
    Assertions.assertEquals(schemaUpdateStaticProperty.getOptions().get(1).isSelected(), false);
  }

  private static OneOfStaticProperty getOneOfStaticProperty(MigrationResult<DataSinkInvocation> actual) {
    return (OneOfStaticProperty) actual.element().getStaticProperties().get(0);
  }
}