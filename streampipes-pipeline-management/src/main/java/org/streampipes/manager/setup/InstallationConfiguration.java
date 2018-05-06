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
import org.streampipes.manager.endpoint.EndpointFetcher;
import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.model.client.setup.InitialSettings;
import org.streampipes.storage.couchdb.utils.CouchDbConfig;
import org.streampipes.storage.rdf4j.util.SesameConfig;

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

		if (settings.getInstallPipelineElements()) {
			for(RdfEndpoint endpoint : new EndpointFetcher().getEndpoints()) {
				steps.add(new PipelineElementInstallationStep(endpoint, settings.getAdminEmail()));
			}
		}
		
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
		   SesameConfig.INSTANCE.setUri("http://" + s.getSesameHost() + ":8030/rdf4j-server");
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
