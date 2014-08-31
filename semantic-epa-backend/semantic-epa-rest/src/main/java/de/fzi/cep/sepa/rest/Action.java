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

import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.rest.http.HttpJsonParser;
import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.cep.sepa.storage.util.Transformer;

@Path("/actions")
public class Action {
	
	StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
	
	/**
	 * add sep
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void addSEP(@FormParam("json") String json, @FormParam("uri") String uri)
	{
		if (uri != null)
		{
			try {
				json = HttpJsonParser.getContentFromUrl(uri);
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
			de.fzi.cep.sepa.model.impl.graph.SEC sec = Transformer.fromJsonLd(de.fzi.cep.sepa.model.impl.graph.SEC.class, json);
			System.out.println(sec.getDescription());
			if (requestor.exists(sec)) requestor.update(sec);
			else requestor.storeSEC(sec);
		}
	}
	
	/**
	 * get list of seps
	 */
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getStreams()
	{
		List<SEC> secs = requestor.getAllSECs();
		return Utils.getGson().toJson(ClientModelTransformer.toActionClientModel(secs));
	}
	
	
	/**
	 * deletes a sep
	 * @param sensId
	 * @return
	 */
	
	@Path("{sepId}")
	@DELETE
	public void deleteSEP(@PathParam("sepId") String sensId)
	{
		
	}
	
}
