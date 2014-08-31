package de.fzi.cep.sepa.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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
import de.fzi.cep.sepa.rest.http.HttpJsonParser;
import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.cep.sepa.storage.util.Transformer;
import de.fzi.sepa.model.client.manager.SEPAManager;

@Path("/sepas")
public class Processor {

	StorageRequests requestor = StorageManager.INSTANCE.getStorageAPI();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSEPAs(@DefaultValue("1") @QueryParam("domains") String domain)
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
	public String getSEPAById(@PathParam("sepaId") String sepaId)
	{
		return Utils.getGson().toJson(SEPAManager.INSTANCE.getSEPAById(sepaId));
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void addSEPA(@FormParam("json") String json, @FormParam("uri") String uri)
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
		de.fzi.cep.sepa.model.impl.graph.SEPA sepa = Transformer.fromJsonLd(de.fzi.cep.sepa.model.impl.graph.SEPA.class, json);
		if (requestor.exists(sepa)) requestor.update(sepa);
		else requestor.storeSEPA(sepa);
	}
}
