package de.fzi.cep.sepa.rest.api;

import de.fzi.cep.sepa.commons.config.WebappConfigurationSettings;

import javax.ws.rs.core.Response;

public interface ISetup {

	Response isConfigured();

	Response configure(WebappConfigurationSettings settings);

	Response getConfiguration();

	Response updateConfiguration(WebappConfigurationSettings settings);
}
