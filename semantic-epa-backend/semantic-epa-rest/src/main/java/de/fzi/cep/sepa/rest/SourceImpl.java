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

import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.Source;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.util.Utils;


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
		else return toJson(ClientModelTransformer.toSourceClientModel(requestor.getSEPsByDomain(domain)));
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
			return toJson(ClientModelTransformer.toSourceClientModel(requestor.getSEPById(sourceId)));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.URIOFFLINE.title(), NotificationType.URIOFFLINE.description(), e.getMessage()));
		} catch (Exception e)
		{
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
	

	@Path("{sourceId}/events")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getStreamsBySource(@PathParam("sourceId") String sourceId)
	{
		try {
			return toJson(ClientModelTransformer.toStreamClientModel(requestor.getSEPById(sourceId)));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.URIOFFLINE.title(), NotificationType.URIOFFLINE.description(), e.getMessage()));
		} catch (Exception e)
		{
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
		
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public String postSource(@FormParam("uri") String uri) {
		SepDescription sep;
		String jsonldDescription = "";
		try {
			
			jsonldDescription = parseURIContent(uri);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			jsonldDescription = uri;
		} catch (IOException e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
		
		try {
			sep = parseObjectContent(SepDescription.class, jsonldDescription);
			if (requestor.exists(sep)) 
				requestor.update(sep);
			else 
				requestor.storeSEP(sep);
		} catch (Exception e)
		{
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description(), e.getMessage()));
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
			return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description(), e.getMessage()));
		}
	}
	
	public static void main(String[] args)
	{
		new SourceImpl().postSource("http://localhost:8089/proveit");
	}
}