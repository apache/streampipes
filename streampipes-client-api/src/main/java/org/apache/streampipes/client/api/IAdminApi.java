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

package org.apache.streampipes.client.api;

import org.apache.streampipes.model.configuration.MessagingSettings;
import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.function.FunctionDefinition;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

import java.util.List;

public interface IAdminApi {

  void registerService(SpServiceRegistration serviceRegistration);

  void deregisterService(String serviceId);

  void registerServiceConfiguration(SpServiceConfiguration serviceConfiguration);

  SpServiceConfiguration getServiceConfiguration(String serviceGroup);

  void registerFunctions(List<FunctionDefinition> functions);

  void deregisterFunction(String functionId);

  void registerAdapterMigrations(List<ModelMigratorConfig> migrationConfigs, String serviceId);

  void registerPipelineElementMigrations(List<ModelMigratorConfig> migratorConfigs, String serviceId);

  MessagingSettings getMessagingSettings();
}
