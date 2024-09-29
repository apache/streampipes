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
package org.apache.streampipes.model.extensions.migration;

import org.apache.streampipes.model.migration.ModelMigratorConfig;

/**
 * Models a request that is sent from the core to an extensions service to mandate a migration.
 * 
 * @param migrationElement
 *          element that needs to be migrated
 * @param modelMigratorConfig
 *          migration config that describes the migration to be applied.
 */
public record MigrationRequest<T>(T migrationElement, ModelMigratorConfig modelMigratorConfig) {
}
