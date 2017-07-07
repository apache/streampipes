package de.fzi.cep.sepa.rest.api;

import javax.ws.rs.core.Response;

public interface IOntologyPipelineElement {

	Response getStreams();

	Response getSepas();

	Response getActions();

	Response getStream(String streamId, boolean keepIds);

	Response getSepa(String sepaId, boolean keepIds);

	Response getAction(String actionId, boolean keepIds);
	
}
