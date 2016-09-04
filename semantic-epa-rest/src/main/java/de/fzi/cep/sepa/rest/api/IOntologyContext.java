package de.fzi.cep.sepa.rest.api;

import de.fzi.cep.sepa.model.client.ontology.Context;

import javax.ws.rs.core.Response;
import java.io.InputStream;


public interface IOntologyContext {

	Response getAvailableContexts();

	Response addContext(InputStream inputFile, Context contextInfo);

	Response deleteContext(String contextId);
}
