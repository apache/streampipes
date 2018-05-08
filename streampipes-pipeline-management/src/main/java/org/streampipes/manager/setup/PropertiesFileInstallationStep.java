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

import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.client.setup.InitialSettings;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PropertiesFileInstallationStep implements InstallationStep {

	private InitialSettings settings;
	private File file;
	private File pathToFile;
	
	public PropertiesFileInstallationStep(File file, File pathToFile, InitialSettings settings) {
		this.settings = settings;
		this.file = file;
		this.pathToFile = pathToFile;
	}
	
	@Override
	public List<Message> install() {
//		try {
//			ConfigurationManager.storeWebappConfigurationToProperties(file, pathToFile, settings);

			return Arrays.asList(Notifications.success(getTitle()));
//		} catch (IOException e) {
//			e.printStackTrace();
//			return Arrays.asList(Notifications.error("Writing configuration to file..."));
//		}
	}

	@Override
	public String getTitle() {
		return "Writing configuration to file...";
	}

}
