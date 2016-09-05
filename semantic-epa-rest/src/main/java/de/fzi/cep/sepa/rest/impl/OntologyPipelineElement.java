package de.fzi.cep.sepa.rest.impl;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.util.GsonSerializer;
import de.fzi.cep.sepa.rest.annotation.GsonWithIds;
import de.fzi.cep.sepa.rest.api.IOntologyPipelineElement;
import de.fzi.cep.sepa.storage.controller.StorageManager;

@Path("/v2/ontology")
public class OntologyPipelineElement extends AbstractRestInterface implements IOntologyPipelineElement {

	@Override
	@Path("/sources")
	@GET
	@GsonWithIds
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStreams() {
		List<SepDescription> result = new ArrayList<>();
		List<SepDescription> sesameSeps = StorageManager.INSTANCE.getStorageAPI().getAllSEPs();
		
		for(SepDescription sep : sesameSeps)
			result.add(new SepDescription(sep));
		return ok(result);
	}
	
	@Path("/sources/{sourceId}")
	@GET
	@GsonWithIds
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSourceDetails(@PathParam("sourceId") String sepaId, @QueryParam("keepIds") boolean keepIds) {
		
		try {
			SepDescription sepaDescription = new SepDescription(StorageManager.INSTANCE.getStorageAPI().getSEPById(sepaId));
			return ok(sepaDescription);
		} catch (URISyntaxException e) {
			return ok(Notifications.error("Error"));
		}
	}

	@Override
	@Path("/sepas")
	@GsonWithIds
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSepas() {
		JsonArray result = new JsonArray();
		List<SepaDescription> sesameSepas = StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();
		
		for(SepaDescription sepa : sesameSepas)
			result.add(getHeader(sepa.getUri(), sepa.getName()));
		return ok(result);
		
	}

	@Override
	@Path("/actions")
	@GET
	@GsonWithIds
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActions() {
		List<SecDescription> result = new ArrayList<>();
		List<SecDescription> sesameSecs = StorageManager.INSTANCE.getStorageAPI().getAllSECs();
		
		for(SecDescription sec : sesameSecs)
			result.add(new SecDescription(sec));
		return ok(result);
	}

	@Override
	public Response getStream(String streamId, @QueryParam("keepIds") boolean keepIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Path("/sepas/{sepaId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getSepa(@PathParam("sepaId") String sepaId, @QueryParam("keepIds") boolean keepIds) {
		
		try {
			SepaDescription sepaDescription = new SepaDescription(StorageManager.INSTANCE.getStorageAPI().getSEPAById(sepaId));
			return ok(sepaDescription);
		} catch (URISyntaxException e) {
			return ok(Notifications.error("Error"));
		}
	}

	@Path("/actions/{actionId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getAction(@PathParam("actionId") String actionId, @QueryParam("keepIds") boolean keepIds) {
		try {
			SecDescription secDescription = new SecDescription(StorageManager.INSTANCE.getStorageAPI().getSECById(actionId));
			return ok(secDescription);
		} catch (URISyntaxException e) {
			return ok(Notifications.error("Error"));
		}
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
