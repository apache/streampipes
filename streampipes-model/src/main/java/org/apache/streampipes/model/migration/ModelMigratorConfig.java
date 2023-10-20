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

package org.apache.streampipes.model.migration;

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;

/**
 * The configuration element for a 'ModelMigrator'.
 *
 * @param targetAppId 'appId' of the model to be migrated
 * @param modelType Type of the model to be migrated, e.g., adapter
 * @param fromVersion Base version from which the migration starts
 * @param toVersion   Target version that the migration aims to achieve
 */
public record ModelMigratorConfig(String targetAppId, SpServiceTagPrefix modelType,
                                  Integer fromVersion, Integer toVersion) {
}
