package de.fzi.cep.sepa.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.shiro.SecurityUtils;
import org.lightcouch.CouchDbClient;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.Processor;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.manager.SEPAManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/sepas")
public class ProcessorImpl extends AbstractRestInterface implements Processor {

	Logger LOG = LoggerFactory.getLogger(ProcessorImpl.class);

	@GET 
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Returns all sepa elements from a user, or all sepa elements if no user is passed.
	 */
	public String getAllProcessors(@DefaultValue("1") @QueryParam("domains") String domain)
	{
		LOG.info("Get all Processors with domain: " + domain);
		List<SepaDescription> sepas;
		if (domain.equals("1")) sepas = requestor.getAllSEPAs();
		else sepas = requestor.getSEPAsByDomain(domain);

		if (sepas == null) {
			LOG.info("sepas are null. Exiting");
			return "";
		}
		return toJson(ClientModelTransformer.toSEPAClientModel(sepas));
	}

	public String getAllUserProcessors() {
		CouchDbClient dbClientUser = de.fzi.cep.sepa.storage.util.Utils.getCouchDbUserClient();
		List<SepaDescription> sepas = new ArrayList<>();
		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			JsonArray sepaIds = dbClientUser.view("users/seoas").key(username).query(JsonObject.class).get(0).get("value").getAsJsonArray();
			try {
				for (JsonElement sepaId : sepaIds) {
					SepaDescription sepa = requestor.getSEPAById(sepaId.getAsString());
					if (sepa != null) sepas.add(sepa);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return toJson(ClientModelTransformer.toSEPAClientModel(sepas));
	}

	
	@Path("{sepaId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getProcessor(@PathParam("sepaId") String sepaId)
	{
		return toJson(SEPAManager.INSTANCE.getSEPAById(sepaId));
	}
	
	@Path("{sepaId}/jsonld")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getProcessorAsJsonLd(@PathParam("sepaId") String sepaId)
	{
		try {
			return toJsonLd(requestor.getSEPAById(sepaId));
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String postProcessor(@FormParam("uri") String uri)
	{
		SepaDescription sepa;
		String jsonldDescription = "";
		
		try {
			jsonldDescription = parseURIContent(uri);
		} catch (ClientProtocolException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		} catch (URISyntaxException e) {
			jsonldDescription = uri;
		} catch (IOException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
		
		try {
			sepa = parseObjectContent(SepaDescription.class, jsonldDescription);
			if (requestor.exists(sepa)) 
				requestor.update(sepa);
			else 
				requestor.storeSEPA(sepa);
		} catch (Exception e)
		{
			return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description(), e.getMessage()));
		}
		return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
	}

	@Path("{sepaId}")
	@DELETE
	@Override
	public String deleteProcessor(@PathParam("sepaId") String sepaId) {
		try {
			requestor.deleteSEPA(requestor.getSEPAById(sepaId));
			return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
		} catch (URISyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description(), e.getMessage()));
		}
	}

	@Override
	public String checkBinding(String subPipeline) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Path("{sepaId}/check")
//	@GET
//	@Override
//	public String checkBinding(String subPipeline) {
//		// create pipeline
//		de.fzi.cep.sepa.model.client.Pipeline pipeline = Utils.getGson().fromJson(subPipeline, de.fzi.cep.sepa.model.client.Pipeline.class);
//		
//		// compute SEPA payload
//		
//		return null;
//	}
}
