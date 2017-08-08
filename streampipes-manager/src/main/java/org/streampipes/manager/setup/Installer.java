package org.streampipes.manager.setup;

import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.setup.InitialSettings;

import java.util.ArrayList;
import java.util.List;

public class Installer {

	private InitialSettings settings;

	public Installer(InitialSettings settings) {
		this.settings = settings;
	}
	
	public List<Message> install() {


		List<InstallationStep> steps = InstallationConfiguration.getInstallationSteps(settings);
		List<Message> result = new ArrayList<>();
		steps.forEach(s -> result.addAll(s.install()));
		return result;
	}
	
}
