package org.streampipes.manager.setup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


import org.streampipes.commons.config.ConfigurationManager;
import org.streampipes.commons.config.WebappConfigurationSettings;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;

public class PropertiesFileInstallationStep implements InstallationStep {

	private WebappConfigurationSettings settings;
	private File file;
	private File pathToFile;
	
	public PropertiesFileInstallationStep(File file, File pathToFile, WebappConfigurationSettings settings) {
		this.settings = settings;
		this.file = file;
		this.pathToFile = pathToFile;
	}
	
	@Override
	public List<Message> install() {
		try {
			ConfigurationManager.storeWebappConfigurationToProperties(file, pathToFile, settings);

			return Arrays.asList(Notifications.success("Writing configuration to file..."));
		} catch (IOException e) {
			e.printStackTrace();
			return Arrays.asList(Notifications.error("Writing configuration to file..."));
		}
	}
	
}
