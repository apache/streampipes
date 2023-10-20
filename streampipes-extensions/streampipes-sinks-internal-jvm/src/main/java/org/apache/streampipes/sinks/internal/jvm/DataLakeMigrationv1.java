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

package org.apache.streampipes.sinks.internal.jvm;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.MigrationResult;
import org.apache.streampipes.extensions.api.migration.ModelMigrator;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.utils.Datatypes;

public class DataLakeMigrationv1 implements ModelMigrator<DataSinkInvocation, IDataSinkParameterExtractor> {

  @Override
  public String targetsAppId() {
    return "org.apache.streampipes.sinks.internal.jvm.datalake";
  }

  @Override
  public Integer fromVersion() {
    return 0;
  }

  @Override
  public Integer toVersion() {
    return 1;
  }

  @Override
  public MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element,
                                                     IDataSinkParameterExtractor extractor) throws SpConfigurationException {
    element.getStaticProperties().add(
        StaticProperties.freeTextProperty(Labels.empty(), Datatypes.String)
    );

    return MigrationResult.success(element);
  }
}
