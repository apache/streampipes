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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.sinks.internal.jvm.datalake.DataLakeSink;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class DataLakeSinkMigrationV2Test {

  @Test
  public void migrate() {
    var dataLakeSinkMigrationV2 = new DataLakeSinkMigrationV2();

    var stream = new SpDataStream();
    stream.setEventSchema(makeSchema());
    var extractor = mock(DataSinkParameterExtractor.class);
    var invocation = new DataSinkInvocation();
    invocation.setStaticProperties(new ArrayList<>());
    invocation.setInputStreams(List.of(stream));

    var actual = dataLakeSinkMigrationV2.migrate(invocation, extractor);

    Assertions.assertTrue(actual.success());
    Assertions.assertEquals(actual.element()
        .getStaticProperties()
        .size(), 2);
    var dimensionConfig = getAnyStaticProperty(actual);
    Assertions.assertEquals(dimensionConfig.getInternalName(), DataLakeSink.DIMENSIONS_KEY);
    Assertions.assertEquals(dimensionConfig.getOptions().size(), 3);
    Assertions.assertTrue(dimensionConfig.getOptions().get(0).isSelected());
    Assertions.assertFalse(dimensionConfig.getOptions().get(1).isSelected());
    Assertions.assertFalse(dimensionConfig.getOptions().get(2).isSelected());
  }

  private static RuntimeResolvableAnyStaticProperty getAnyStaticProperty(MigrationResult<DataSinkInvocation> actual) {
    return (RuntimeResolvableAnyStaticProperty) actual.element()
        .getStaticProperties()
        .get(0);
  }

  private static EventSchema makeSchema() {
    return new EventSchema(
        List.of(
            PrimitivePropertyBuilder
                .create(Datatypes.String, "a")
                .scope(PropertyScope.DIMENSION_PROPERTY)
                .build(),
            PrimitivePropertyBuilder
                .create(Datatypes.Float, "b")
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .build(),
            PrimitivePropertyBuilder
                .create(Datatypes.Integer, "c")
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .build(),
            PrimitivePropertyBuilder
                .create(Datatypes.Boolean, "d")
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .build()
        )
    );
  }
}
