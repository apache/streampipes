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

package org.apache.streampipes.processors.transformation.jvm.migrations;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataProcessorMigrator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata.StaticMetaDataEnrichmentProcessor;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.List;

import static org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata.StaticMetaDataEnrichmentProcessor.STATIC_METADATA_INPUT_DESCRIPTION;
import static org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata.StaticMetaDataEnrichmentProcessor.STATIC_METADATA_INPUT_LABEL;

public class StaticMetadataEnrichmentProcessorMigrationV2 implements IDataProcessorMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        StaticMetaDataEnrichmentProcessor.ID,
        SpServiceTagPrefix.DATA_PROCESSOR,
        1,
        2
    );
  }

  @Override
  public MigrationResult<DataProcessorInvocation> migrate(DataProcessorInvocation element,
                                                          IDataProcessorParameterExtractor extractor)
      throws RuntimeException {
    element.getStaticProperties()
        .stream()
        .filter(sp -> sp.getInternalName().equals(StaticMetaDataEnrichmentProcessor.STATIC_METADATA_INPUT))
        .map(sp -> (CollectionStaticProperty) sp)
        .forEach(sp -> {
          addLabelAndDescriptionConfig((StaticPropertyGroup) sp.getStaticPropertyTemplate());
          sp.getMembers().forEach(member -> {
            addLabelAndDescriptionConfig((StaticPropertyGroup) member);
          });
        });
    return MigrationResult.success(element);
  }

  private void addLabelAndDescriptionConfig(StaticPropertyGroup group) {
    group.setHorizontalRendering(false);
    group.getStaticProperties().addAll(
        List.of(
            makeFreeTextProperty(STATIC_METADATA_INPUT_LABEL),
            makeFreeTextProperty(STATIC_METADATA_INPUT_DESCRIPTION)
        )
    );
  }

  private StaticProperty makeFreeTextProperty(String id) {
    var sp = StaticProperties.stringFreeTextProperty(
        Labels.withId(
            id));
    sp.setOptional(true);
    sp.setValue("");
    return sp;
  }
}
