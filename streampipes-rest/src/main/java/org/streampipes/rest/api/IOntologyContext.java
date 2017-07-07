package org.streampipes.rest.api;

import org.streampipes.model.client.ontology.Context;

import javax.ws.rs.core.Response;
import java.io.InputStream;


public interface IOntologyContext {

	Response getAvailableContexts();

	Response addContext(InputStream inputFile, Context contextInfo);

	Response deleteContext(String contextId);
}
