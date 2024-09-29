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
package org.apache.streampipes.sinks.notifications.jvm.migrations;

import static org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink.DEFAULT_WAITING_TIME_MINUTES;
import static org.apache.streampipes.wrapper.standalone.StreamPipesNotificationSink.KEY_SILENT_PERIOD;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.ArrayList;

import org.junit.Test;

public class INotificationDataSinkMigratorTest {

  @Test
  public void testNotificationDataSinkMigration() {

    INotificationDataSinkMigrator migrator = spy(INotificationDataSinkMigrator.class);
    IDataSinkParameterExtractor extractor = mock(IDataSinkParameterExtractor.class);

    DataSinkInvocation dataSinkInvocation = new DataSinkInvocation();
    dataSinkInvocation.setStaticProperties(new ArrayList<>());

    MigrationResult<DataSinkInvocation> result = migrator.migrate(dataSinkInvocation, extractor);

    assertEquals(1, result.element().getStaticProperties().size());

    var property = (FreeTextStaticProperty) result.element().getStaticProperties().get(0);
    assertEquals(KEY_SILENT_PERIOD, property.getInternalName());
    assertEquals(DEFAULT_WAITING_TIME_MINUTES, Integer.valueOf(property.getValue()).intValue());
  }
}