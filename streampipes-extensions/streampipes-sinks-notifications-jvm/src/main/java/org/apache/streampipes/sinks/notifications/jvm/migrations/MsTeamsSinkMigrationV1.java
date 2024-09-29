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

import static org.apache.streampipes.sinks.notifications.jvm.msteams.MSTeamsSink.KEY_PROXY_ALTERNATIVES;
import static org.apache.streampipes.sinks.notifications.jvm.msteams.MSTeamsSink.KEY_PROXY_DISABLED;
import static org.apache.streampipes.sinks.notifications.jvm.msteams.MSTeamsSink.KEY_PROXY_ENABLED;
import static org.apache.streampipes.sinks.notifications.jvm.msteams.MSTeamsSink.KEY_PROXY_GROUP;
import static org.apache.streampipes.sinks.notifications.jvm.msteams.MSTeamsSink.KEY_PROXY_URL;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sinks.notifications.jvm.msteams.MSTeamsSink;

public class MsTeamsSinkMigrationV1 implements IDataSinkMigrator {

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(MSTeamsSink.ID, SpServiceTagPrefix.DATA_SINK, 0, 1);
  }

  @Override
  public MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element, IDataSinkParameterExtractor extractor)
          throws RuntimeException {
    var proxyConfiguration = StaticProperties.alternatives(Labels.withId(KEY_PROXY_ALTERNATIVES),
            Alternatives.from(Labels.withId(KEY_PROXY_DISABLED)),
            Alternatives.from(Labels.withId(KEY_PROXY_ENABLED), StaticProperties.group(Labels.withId(KEY_PROXY_GROUP),
                    StaticProperties.stringFreeTextProperty(Labels.withId(KEY_PROXY_URL)))));
    proxyConfiguration.getAlternatives().get(0).setSelected(true);
    element.getStaticProperties().add(1, proxyConfiguration);
    return MigrationResult.success(element);
  }
}
