package org.streampipes.rest.api;

import javax.ws.rs.core.Response;

public interface IPipelineElementCategory {

	Response getEps();

	Response getEpaCategories();

	Response getEcCategories();
	
}
