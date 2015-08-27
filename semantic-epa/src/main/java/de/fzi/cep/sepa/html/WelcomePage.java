package de.fzi.cep.sepa.html;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

import de.fzi.cep.sepa.util.CorsHeaders;

public abstract class WelcomePage<T> extends Restlet {

	protected List<Description> producers;
	
	public WelcomePage()
	{
		super();
		this.producers = new ArrayList<>();
	}
	
	protected abstract void buildUris(String baseUri, List<T> declarers);
	
	 @Override
     public void handle(Request request, Response response) {
		 if (request.getClientInfo().getAcceptedMediaTypes().stream().anyMatch(p -> p.getMetadata() == MediaType.APPLICATION_JSON))
		 {
			response.setEntity(new StringRepresentation(new JSONGenerator(producers).buildJson(), MediaType.APPLICATION_JSON));
			new CorsHeaders().make(response);	
		 }
		 else
		 {
			 response.setEntity(new StringRepresentation(new HTMLGenerator(producers).buildHtml(), MediaType.TEXT_HTML));
		 }
    }
}
