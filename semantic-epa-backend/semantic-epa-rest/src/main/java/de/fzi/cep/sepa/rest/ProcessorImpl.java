package de.fzi.cep.sepa.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;

import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.Processor;
import de.fzi.cep.sepa.rest.messages.NotificationType;
import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.manager.SEPAManager;

@Path("/sepas")
public class ProcessorImpl extends AbstractRestInterface implements Processor {

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllProcessors(@DefaultValue("1") @QueryParam("domains") String domain)
	{
		System.out.println(domain);
		List<SEPA> sepas;
		if (domain.equals("1")) sepas = requestor.getAllSEPAs();
		else sepas = requestor.getSEPAsByDomain(domain);
		
		System.out.println(sepas.size());
		
		return Utils.getGson().toJson(ClientModelTransformer.toSEPAClientModel(sepas));
	}
	
	@Path("{sepaId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getProcessor(@PathParam("sepaId") String sepaId)
	{
		return Utils.getGson().toJson(SEPAManager.INSTANCE.getSEPAById(sepaId));
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String postProcessor(@FormParam("uri") String uri)
	{
		SEPA sepa;
		String jsonldDescription = "";
		
		try {
			jsonldDescription = parseURIContent(uri);
		} catch (ClientProtocolException e) {
			return constructErrorMessage(e, NotificationType.UNKNOWN_ERROR.uiNotification());
		} catch (URISyntaxException e) {
			jsonldDescription = uri;
		} catch (IOException e) {
			return constructErrorMessage(e, NotificationType.UNKNOWN_ERROR.uiNotification());
		}
		
		try {
			sepa = parseObjectContent(SEPA.class, jsonldDescription);
			if (requestor.exists(sepa)) 
				requestor.update(sepa);
			else 
				requestor.storeSEPA(sepa);
		} catch (Exception e)
		{
			return constructErrorMessage(e, NotificationType.STORAGE_ERROR.uiNotification());
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

	@Path("{sepaId}")
	@DELETE
	@Override
	public String deleteProcessor(String sepaId) {
		// TODO Auto-generated method stub
		return null;
	}
}
