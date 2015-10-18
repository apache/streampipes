package de.fzi.cep.sepa.rest.api.v2;

import java.io.InputStream;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface Context {

	public String getAvailableContexts();
	
	public String addContext(InputStream inputFile, String json);
	
	public String deleteContext(String contextId);
}
