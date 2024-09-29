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
package org.apache.streampipes.manager.migration;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.List;
import java.util.Optional;

public class MigrationUtils {

  /**
   * Filter the application migration for a pipeline definition. By definition, there is only one migration config that
   * fulfills the requirements. Otherwise, it should have been detected as duplicate by the extensions service.
   *
   * @param pipelineElement
   *          pipeline element that should be migrated
   * @param migrationConfigs
   *          available migration configs to pick the applicable from
   * @return config that is applicable for the given pipeline element
   */
  public static Optional<ModelMigratorConfig> getApplicableMigration(InvocableStreamPipesEntity pipelineElement,
          List<ModelMigratorConfig> migrationConfigs) {
    return migrationConfigs.stream()
            .filter(config -> config.modelType().equals(pipelineElement.getServiceTagPrefix())
                    && config.targetAppId().equals(pipelineElement.getAppId())
                    && config.fromVersion() == pipelineElement.getVersion())
            .findFirst();
  }

  /**
   * Get the URL that provides the description for an entity.
   *
   * @param entityType
   *          Type of the entity to be updated.
   * @param appId
   *          AppId of the entity to be updated
   * @param serviceUrl
   *          URL of the extensions service to which the entity belongs
   * @return URL of the endpoint that provides the description for the given entity
   */
  public static String getRequestUrl(SpServiceTagPrefix entityType, String appId, String serviceUrl) {

    SpServiceUrlProvider urlProvider;
    switch (entityType) {
      case ADAPTER -> urlProvider = SpServiceUrlProvider.ADAPTER;
      case DATA_PROCESSOR -> urlProvider = SpServiceUrlProvider.DATA_PROCESSOR;
      case DATA_SINK -> urlProvider = SpServiceUrlProvider.DATA_SINK;
      default -> throw new RuntimeException("Unexpected instance type.");
    }
    return urlProvider.getInvocationUrl(serviceUrl, appId);
  }
}
