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

package org.apache.streampipes.processors.imageprocessing.jvm.processor.migration;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataProcessorMigrator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.commons.ImagePropertyConstants;
import org.apache.streampipes.processors.imageprocessing.jvm.processor.imageenrichment.ImageEnrichmentProcessor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.OutputStrategies;

import java.util.List;

public class ImageEnrichmentProcessorMigrationv1 implements IDataProcessorMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        ImageEnrichmentProcessor.ID,
        SpServiceTagPrefix.DATA_PROCESSOR,
        0,
        1);
  }

  @Override
  public MigrationResult<DataProcessorInvocation> migrate(DataProcessorInvocation element,
                                                          IDataProcessorParameterExtractor extractor)
      throws RuntimeException {
    element.setOutputStrategies(List.of(
        OutputStrategies.fixed(
            EpProperties.timestampProperty("timestamp"),
            EpProperties.stringEp(Labels.empty(), ImagePropertyConstants.IMAGE.getProperty(),
                "https://image.com")
        )
    ));
    return MigrationResult.success(element);
  }
}
