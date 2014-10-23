package de.fzi.cep.sepa.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.Stream;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;

/**
 * HTTP endpoint for streams
 * supported methods: GET, POST, DELETE
 */

@Path("/streams")
public class StreamImpl extends AbstractRestInterface implements Stream {
	
	StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
	
	/**
	 * add sep
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String postSource(@FormParam("uri") String uri)
	{
		return new SourceImpl().postSource(uri);
	}
	
	/**
	 * get list of seps
	 */
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getAllStreams()
	{
		List<SEP> seps = requestor.getAllSEPs();
		return toJson(ClientModelTransformer.toStreamClientModel(seps));
	}
	
	@Path("{sourceId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getStreamById(@PathParam("sourceId") String sepId)
	{
		try {
			SEP sep = requestor.getSEPById(sepId);
			return toJson(ClientModelTransformer.toStreamClientModel(sep));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return "";
		}
		
	}
		
}
