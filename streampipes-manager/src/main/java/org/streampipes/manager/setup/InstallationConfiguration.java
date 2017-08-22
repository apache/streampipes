package org.streampipes.manager.setup;

import org.streampipes.config.backend.BackendConfig;
import org.streampipes.model.client.setup.InitialSettings;
import org.streampipes.storage.util.CouchDbConfig;
import org.streampipes.storage.util.SesameConfig;

import java.util.ArrayList;
import java.util.List;

public class InstallationConfiguration {

	public static List<InstallationStep> getInstallationSteps(InitialSettings settings)
	{

		setInitialConfiguration(settings);
		List<InstallationStep> steps = new ArrayList<>();
		
		steps.add(new SesameDbInstallationStep());
		steps.add(new CouchDbInstallationStep());
		steps.add(new UserRegistrationInstallationStep(settings.getAdminEmail(), settings.getAdminPassword()));
		
		return steps;
	}

	/**
	 * Set the initial configuration when the user specifies it in the user interface during the installation
	 * @param s settings the user provided in the installation dialogue
	 */
	private static void setInitialConfiguration(InitialSettings s) {
	   if (!"".equals(s.getCouchDbHost())) {
		   CouchDbConfig.INSTANCE.setHost(s.getCouchDbHost());
	   }
	   if (!"".equals(s.getSesameHost())) {
		   SesameConfig.INSTANCE.setUri("http://" + s.getSesameHost() + ":8030/openrdf-sesame");
	   }
	   if (!"".equals(s.getKafkaHost())) {
		   BackendConfig.INSTANCE.setKafkaHost(s.getKafkaHost());
	   }
	   if (!"".equals(s.getZookeeperHost())) {
		   BackendConfig.INSTANCE.setZookeeperHost(s.getZookeeperHost());
	   }
	   if (!"".equals(s.getJmsHost())) {
		   BackendConfig.INSTANCE.setJmsHost(s.getJmsHost());
	   }
	}
}
