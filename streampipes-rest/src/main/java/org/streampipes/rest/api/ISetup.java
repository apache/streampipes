package org.streampipes.rest.api;

import org.streampipes.commons.config.old.WebappConfigurationSettings;

import javax.ws.rs.core.Response;

public interface ISetup {

	Response isConfigured();

	Response configure(WebappConfigurationSettings settings);

	Response getConfiguration();

	Response updateConfiguration(WebappConfigurationSettings settings);
}
