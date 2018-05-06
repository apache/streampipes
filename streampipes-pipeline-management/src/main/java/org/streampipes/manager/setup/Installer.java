/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.setup;

import org.streampipes.config.backend.BackendConfig;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.SetupStatusMessage;
import org.streampipes.model.client.setup.InitialSettings;

import java.util.ArrayList;
import java.util.List;

public class Installer {

	private List<InstallationStep> installationSteps;

	public Installer(InitialSettings settings) {
		this.installationSteps = InstallationConfiguration.getInstallationSteps(settings);
	}
	
	public SetupStatusMessage install(Integer currentInstallationStepIndex) {
		String nextInstallationStepTitle = "";
		InstallationStep currentInstallationStep = installationSteps.get(currentInstallationStepIndex);
		List<Message> result = new ArrayList<>();
		result.addAll(currentInstallationStep.install());

		if (currentInstallationStepIndex == (this.installationSteps.size() - 1)) {
			BackendConfig.INSTANCE.setIsConfigured(true);
		} else {
			nextInstallationStepTitle = installationSteps.get(currentInstallationStepIndex +1).getTitle();
		}

		return new SetupStatusMessage(currentInstallationStepIndex, installationSteps.size(), result, nextInstallationStepTitle);
	}
	
}
