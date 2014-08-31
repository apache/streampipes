package de.fzi.cep.sepa.rest;

import java.net.URISyntaxException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;


@Path("/sources")
public class Source {
	
	StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
	
	/**
	 * get all event sources
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSources(@DefaultValue("1") @QueryParam("domain") String domain)
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
	public String getSource(@PathParam("sourceId") String sourceId)
	{
		try {
			return Utils.getGson().toJson(ClientModelTransformer.toSourceClientModel(requestor.getSEPById(sourceId)));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: error handling
		return null;
	}
	

	@Path("{sourceId}/events")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSEPsBySource(@PathParam("sourceId") String sourceId)
	{
		try {
			return Utils.getGson().toJson(ClientModelTransformer.toStreamClientModel(requestor.getSEPById(sourceId)));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO: error handling
		return null;
	}
}