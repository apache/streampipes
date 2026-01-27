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

package org.apache.streampipes.connect.iiot.migration;

import org.apache.streampipes.connect.iiot.protocol.stream.HttpStreamProtocol;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;

public class HttpStreamProtocolMigrationV1 implements IAdapterMigrator {

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        HttpStreamProtocol.ID,
        SpServiceTagPrefix.ADAPTER,
        0,
        1
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {
    var headerCollection = makeHeaderCollection();
    int insertIndex = Math.min(2, element.getConfig().size());
    element.getConfig().add(insertIndex, headerCollection);
    return MigrationResult.success(element);
  }

  private CollectionStaticProperty makeHeaderCollection() {
    var headerKey = StaticProperties.stringFreeTextProperty(
        Labels.withId(HttpStreamProtocol.HEADER_KEY)
    );
    headerKey.setOptional(true);
    headerKey.setValue("");

    var headerValue = StaticProperties.stringFreeTextProperty(
        Labels.withId(HttpStreamProtocol.HEADER_VALUE)
    );
    headerValue.setOptional(true);
    headerValue.setValue("");

    return StaticProperties.collection(
        Labels.withId(HttpStreamProtocol.HEADER_COLLECTION),
        false,
        headerKey,
        headerValue
    );
  }
}
