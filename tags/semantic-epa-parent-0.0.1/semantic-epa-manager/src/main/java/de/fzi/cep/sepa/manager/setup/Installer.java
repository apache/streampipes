package de.fzi.cep.sepa.manager.setup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.config.WebappConfigurationSettings;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notifications;

public class Installer {

	private WebappConfigurationSettings settings;
	private File file;
	private File pathToFile;
	
	public Installer(WebappConfigurationSettings settings, File file, File pathToFile) {
		this.file = file;
		this.settings = settings;
		this.pathToFile = pathToFile;
	}
	
	public List<Message> install() {	
		List<InstallationStep> steps = InstallationConfiguration.getInstallationSteps(file, pathToFile, settings);
		List<Message> result = new ArrayList<>();
//		result.add(Notifications.success("Test"));
//		result.add(Notifications.success("Test2"));
//		result.add(Notifications.error("Test2"));
		steps.forEach(s -> result.addAll(s.install()));
		return result;
	}
	
}
