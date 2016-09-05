package de.fzi.cep.sepa.rest.api;

import javax.ws.rs.core.Response;

public interface IPipelineElementCategory {

	Response getEps();

	Response getEpaCategories();

	Response getEcCategories();
	
}
