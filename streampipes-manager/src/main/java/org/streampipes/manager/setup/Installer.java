package org.streampipes.manager.setup;

import org.streampipes.commons.config.WebappConfigurationSettings;
import org.streampipes.model.client.messages.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
		steps.forEach(s -> result.addAll(s.install()));
		return result;
	}
	
}
