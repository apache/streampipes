package de.fzi.cep.sepa.manager.setup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.commons.config.WebappConfigurationSettings;
import de.fzi.cep.sepa.model.client.messages.Message;
import de.fzi.cep.sepa.model.client.messages.Notifications;

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
