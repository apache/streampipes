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

import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.Source;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.util.Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/sources")
public class SourceImpl extends AbstractRestInterface implements Source {
	
	Logger LOG = LoggerFactory.getLogger(SourceImpl.class);
	/**
	 * get all event sources
	 */
	@GET
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getAllSources(@DefaultValue("1") @QueryParam("domain") String domain)
	{
		LOG.info("Got called with: " + domain);
		if (domain.equals("1")) return Utils.getGson().toJson(ClientModelTransformer.toSourceClientModel(requestor.getAllSEPs()));
		else return toJson(ClientModelTransformer.toSourceClientModel(requestor.getSEPsByDomain(domain)));
	}

	@Path("user")
	@GET
	public String getAllUserSources() {
		CouchDbClient dbClientUser = de.fzi.cep.sepa.storage.util.Utils.getCouchDbUserClient();
		List<SepDescription> seps = new ArrayList<>();
		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			JsonArray sepIds = dbClientUser.view("users/sources").key(username).query(JsonObject.class).get(0).get("value").getAsJsonArray();
			try {
				for (JsonElement sepaId : sepIds) {
					seps.add(requestor.getSEPById(sepaId.getAsString()));
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return toJson(ClientModelTransformer.toSourceClientModel(seps));
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
		LOG.info("Got called with sourceID: " + sourceId);
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
	@Produces(MediaType.APPLICATION_JSON)
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