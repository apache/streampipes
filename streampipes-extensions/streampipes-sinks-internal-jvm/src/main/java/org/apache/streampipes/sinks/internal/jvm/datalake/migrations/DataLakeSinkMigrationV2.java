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

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableAnyStaticProperty;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sinks.internal.jvm.datalake.DataLakeDimensionProvider;
import org.apache.streampipes.sinks.internal.jvm.datalake.DataLakeSink;

public class DataLakeSinkMigrationV2 implements IDataSinkMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        "org.apache.streampipes.sinks.internal.jvm.datalake",
        SpServiceTagPrefix.DATA_SINK,
        1,
        2
    );
  }

  @Override
  public MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element,
                                                     IDataSinkParameterExtractor extractor) throws RuntimeException {
    var label = Labels.from(DataLakeSink.DIMENSIONS_KEY, "Dimensions", "Selected fields will be stored as dimensions.");
    var staticProperty = new RuntimeResolvableAnyStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription()
    );
    var inputFields = element.getInputStreams().get(0).getEventSchema().getEventProperties();
    new DataLakeDimensionProvider().applyOptions(inputFields, staticProperty);

    element.getStaticProperties().add(staticProperty);
    return MigrationResult.success(element);
  }
}
