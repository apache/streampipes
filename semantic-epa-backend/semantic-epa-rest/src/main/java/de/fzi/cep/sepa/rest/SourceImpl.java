package de.fzi.cep.sepa.rest;

import java.io.IOException;
import java.net.URISyntaxException;

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

import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.Source;
import de.fzi.cep.sepa.rest.messages.NotificationType;
import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;


@Path("/sources")
public class SourceImpl extends AbstractRestInterface implements Source {
	
	
	/**
	 * get all event sources
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getAllSources(@DefaultValue("1") @QueryParam("domain") String domain)
	{
		if (domain.equals("1")) return Utils.getGson().toJson(ClientModelTransformer.toSourceClientModel(requestor.getAllSEPs()));
		else return Utils.getGson().toJson(ClientModelTransformer.toSourceClientModel(requestor.getSEPsByDomain(domain)));
	}
	
	/**
	 * get description of specific source
	 */
	
	@Path("{sourceId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getSource(@PathParam("sourceId") String sourceId)
	{
		try {
			return Utils.getGson().toJson(ClientModelTransformer.toSourceClientModel(requestor.getSEPById(sourceId)));
		} catch (URISyntaxException e) {
			return constructErrorMessage(e, NotificationType.URIOFFLINE.uiNotification());
		} catch (Exception e)
		{
			return constructErrorMessage(e, NotificationType.UNKNOWN_ERROR.uiNotification());
		}
	}
	

	@Path("{sourceId}/events")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getStreamsBySource(@PathParam("sourceId") String sourceId)
	{
		try {
			return Utils.getGson().toJson(ClientModelTransformer.toStreamClientModel(requestor.getSEPById(sourceId)));
		} catch (URISyntaxException e) {
			return constructErrorMessage(e, NotificationType.URIOFFLINE.uiNotification());
		} catch (Exception e)
		{
			return constructErrorMessage(e, NotificationType.UNKNOWN_ERROR.uiNotification());
		}
		
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String postSource(@FormParam("uri") String uri) {
		SEP sep;
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
			sep = parseObjectContent(SEP.class, jsonldDescription);
			if (requestor.exists(sep)) 
				requestor.update(sep);
			else 
				requestor.storeSEP(sep);
		} catch (Exception e)
		{
			return constructErrorMessage(e, NotificationType.STORAGE_ERROR.uiNotification());
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

	@Path("{sepId}")
	@DELETE
	@Override
	public String deleteSource(@PathParam("sepId") String sepId) {
		try {
			requestor.deleteSEP(requestor.getSEPById(sepId));
			return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
		} catch (URISyntaxException e) {
			return constructErrorMessage(e, NotificationType.STORAGE_ERROR.uiNotification());
		}
	}
}