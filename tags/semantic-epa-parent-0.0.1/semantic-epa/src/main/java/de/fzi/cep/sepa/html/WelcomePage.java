package de.fzi.cep.sepa.html;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

import de.fzi.cep.sepa.util.CorsHeaders;

public class WelcomePage extends Restlet {

	protected List<Description> descriptions;
	
	public WelcomePage(List<Description> descriptions)
	{
		super();
		this.descriptions = descriptions;
	}
	
	
	 @Override
     public void handle(Request request, Response response) {
		 if (request.getClientInfo().getAcceptedMediaTypes().stream().anyMatch(p -> p.getMetadata() == MediaType.APPLICATION_JSON))
		 {
			response.setEntity(new StringRepresentation(new JSONGenerator(descriptions).buildJson(), MediaType.APPLICATION_JSON));
			new CorsHeaders().make(response);	
		 }
		 else
		 {
			 response.setEntity(new StringRepresentation(new HTMLGenerator(descriptions).buildHtml(), MediaType.TEXT_HTML));
		 }
    }
}
