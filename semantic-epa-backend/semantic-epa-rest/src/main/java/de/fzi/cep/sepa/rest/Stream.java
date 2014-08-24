package de.fzi.cep.sepa.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;

import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.rest.http.HttpJsonParser;
import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.Transformer;

@Path("/streams")
public class Stream {
	
	StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
	
	/**
	 * add sep
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void addSEP(@FormParam("json") String json, @FormParam("uri") String uri)
	{
		System.out.println(uri);
		if (uri != null)
		{
			try {
				json = HttpJsonParser.getContentFromUrl(uri);
				System.out.println(json);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (json != null)
		{
			de.fzi.cep.sepa.model.impl.graph.SEP sep = Transformer.fromJsonLd(de.fzi.cep.sepa.model.impl.graph.SEP.class, json);
			if (requestor.exists(sep)) requestor.update(sep);
			else requestor.storeSEP(sep);
		}
	}
	
	/**
	 * get list of seps
	 */
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getStreams()
	{
		List<SEP> seps = requestor.getAllSEPs();
		return Utils.getGson().toJson(Transformer.toStreamClientModel(seps));
	}
	
	@Path("{sepId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSEPById(@PathParam("sourceId") String sepId)
	{
		try {
			SEP sep = requestor.getSEPById(sepId);
			System.out.println(sep.getName());
			return Utils.getGson().toJson(Transformer.toStreamClientModel(sep));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return "";
		}
		
	}
	
	
	/**
	 * deletes a sep
	 * @param sensId
	 * @return
	 */
	
	@Path("{sensId}")
	@DELETE
	public void deleteSEP(@PathParam("sensId") String sensId)
	{
		
	}
	
}
