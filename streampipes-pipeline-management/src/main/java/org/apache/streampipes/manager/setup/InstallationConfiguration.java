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

package org.apache.streampipes.manager.setup;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.extensions.AvailableExtensionsProvider;
import org.apache.streampipes.model.client.setup.InitialSettings;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import java.util.ArrayList;
import java.util.List;

public class InstallationConfiguration {

  private final IGenericStorage genericStorage;
  private final ICompactPipelineTemplateStorage pipelineTemplateStorage;
  private final IPipelineElementDescriptionStorage descriptionStorage;
  private final AvailableExtensionsProvider availableExtensionsProvider;

  public InstallationConfiguration(IGenericStorage genericStorage,
                                   ICompactPipelineTemplateStorage pipelineTemplateStorage,
                                   IPipelineElementDescriptionStorage descriptionStorage,
                                   AvailableExtensionsProvider availableExtensionsProvider) {
    this.genericStorage = genericStorage;
    this.pipelineTemplateStorage = pipelineTemplateStorage;
    this.descriptionStorage = descriptionStorage;
    this.availableExtensionsProvider = availableExtensionsProvider;
  }

  public List<InstallationStep> getInstallationSteps(InitialSettings settings,
                                                            SpResourceManager resourceManager) {
    List<InstallationStep> steps = new ArrayList<>();

    steps.add(new SpCoreConfigurationStep(resourceManager.getCoreConfigurationStorage()));
    steps.add(new CouchDbInstallationStep(genericStorage, pipelineTemplateStorage));
    steps.add(new UserRegistrationInstallationStep(
        settings.getAdminEmail(),
        settings.getAdminPassword(),
        settings.getInitialServiceAccountName(),
        settings.getInitialServiceAccountSecret(),
        settings.getInitialAdminUserSid(),
        resourceManager.manageUsers().getDb()));

    return steps;
  }

  public List<Runnable> getBackgroundInstallationSteps(InitialSettings settings,
                                                              BackgroundTaskNotifier callback,
                                                              ExtensionServiceRequestManager extensionServiceRequestManager,
                                                              SpResourceManager resourceManager) {
    return List.of(new ExtensionsInstallationTask(
        settings,
        availableExtensionsProvider,
        descriptionStorage,
        callback,
        extensionServiceRequestManager,
        resourceManager
    ));
  }
}
