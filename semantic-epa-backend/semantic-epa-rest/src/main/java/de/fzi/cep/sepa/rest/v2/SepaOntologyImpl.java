package de.fzi.cep.sepa.rest.v2;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.util.GsonSerializer;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.v2.SepaOntology;
import de.fzi.cep.sepa.storage.controller.StorageManager;

@Path("/v2/ontology")
public class SepaOntologyImpl extends AbstractRestInterface implements SepaOntology {

	@Override
	@Path("/sources")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getStreams() {
		List<SepDescription> result = new ArrayList<>();
		List<SepDescription> sesameSeps = StorageManager.INSTANCE.getStorageAPI().getAllSEPs();
		
		for(SepDescription sep : sesameSeps)
			result.add(new SepDescription(sep));
		return GsonSerializer.getGson().toJson(result);
	}
	
	@Path("/sources/{sourceId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSourceDetails(@PathParam("sourceId") String sepaId, @QueryParam("keepIds") boolean keepIds) {
		
		try {
			SepDescription sepaDescription = new SepDescription(StorageManager.INSTANCE.getStorageAPI().getSEPById(sepaId));
			return getGson(keepIds).toJson(sepaDescription);
		} catch (URISyntaxException e) {
			return toJson(Notifications.error("Error"));
		}
	}

	@Override
	@Path("/sepas")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSepas() {
		JsonArray result = new JsonArray();
		List<SepaDescription> sesameSepas = StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();
		
		for(SepaDescription sepa : sesameSepas)
			result.add(getHeader(sepa.getUri(), sepa.getName()));
		return GsonSerializer.getGson().toJson(result);
		
	}

	@Override
	@Path("/actions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getActions() {
		List<SecDescription> result = new ArrayList<>();
		List<SecDescription> sesameSecs = StorageManager.INSTANCE.getStorageAPI().getAllSECs();
		
		for(SecDescription sec : sesameSecs)
			result.add(new SecDescription(sec));
		return GsonSerializer.getGson().toJson(result);
	}

	@Override
	public String getStream(String streamId, @QueryParam("keepIds") boolean keepIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/sepas/{sepaId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getSepa(@PathParam("sepaId") String sepaId, @QueryParam("keepIds") boolean keepIds) {
		
		try {
			SepaDescription sepaDescription = new SepaDescription(StorageManager.INSTANCE.getStorageAPI().getSEPAById(sepaId));
			return getGson(keepIds).toJson(sepaDescription);
		} catch (URISyntaxException e) {
			return toJson(Notifications.error("Error"));
		}
	}

	@Path("/actions/{actionId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getAction(@PathParam("actionId") String actionId, @QueryParam("keepIds") boolean keepIds) {
		try {
			SecDescription secDescription = new SecDescription(StorageManager.INSTANCE.getStorageAPI().getSECById(actionId));
			return getGson(keepIds).toJson(secDescription);
		} catch (URISyntaxException e) {
			return toJson(Notifications.error("Error"));
		}
	}
	
	public static void main(String[] args)
	{
		new SepaOntologyImpl().getSepas();
	}
	
	private JsonObject getHeader(String uri, String name)
	{
		JsonObject jsonObj = new JsonObject();
		jsonObj.add("uri", new JsonPrimitive(uri));
		jsonObj.add("name", new JsonPrimitive(name));
		
		return jsonObj;
	}
	
	private Gson getGson(boolean keepIds)
	{
		if (keepIds) return GsonSerializer.getGsonWithIds();
		else return GsonSerializer.getGsonWithoutIds();
	}
	

}
