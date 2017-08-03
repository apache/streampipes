package org.streampipes.manager.setup;

import org.streampipes.commons.config.old.WebappConfigurationSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InstallationConfiguration {

	public static List<InstallationStep> getInstallationSteps(File file, File pathToFile, WebappConfigurationSettings settings)
	{
		List<InstallationStep> steps = new ArrayList<>();
		
		steps.add(new PropertiesFileInstallationStep(file, pathToFile, settings));
		steps.add(new SesameDbInstallationStep(settings.getSesameUrl(), settings.getSesameDbName()));
		steps.add(new CouchDbInstallationStep());
		steps.add(new UserRegistrationInstallationStep(settings.getAdminEmail(), settings.getAdminUserName(), settings.getAdminPassword()));
		
		return steps;
	}
}
