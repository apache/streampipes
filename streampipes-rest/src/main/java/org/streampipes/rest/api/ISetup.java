package org.streampipes.rest.api;

import org.streampipes.model.client.setup.InitialSettings;

import javax.ws.rs.core.Response;

public interface ISetup {

	Response isConfigured();

	Response configure(InitialSettings settings);

	Response getConfiguration();

	Response updateConfiguration(InitialSettings settings);
}
