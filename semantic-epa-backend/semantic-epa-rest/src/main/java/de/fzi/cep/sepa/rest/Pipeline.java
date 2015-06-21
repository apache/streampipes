package de.fzi.cep.sepa.rest;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import de.fzi.cep.sepa.commons.exceptions.NoMatchingFormatException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingProtocolException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingSchemaException;
import de.fzi.cep.sepa.commons.exceptions.NoSuitableSepasAvailableException;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pipeline REST interface.
 */
@Path("/pipelines")
public class Pipeline extends AbstractRestInterface {

	Logger LOG = LoggerFactory.getLogger(Pipeline.class);

	CouchDbClient dbClient = new CouchDbClient("couchdb-users.properties");

	/**
	 * 
	 * @return a JSON representation of all available pipelines or an empty JSON list
	 */
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getPipelines()
	{
		return toJson(pipelineStorage.getAllUserPipelines());
		//return toJson(pipelineStorage.getAllPipelines());
	}
	
	/**
	 * 
	 * @param pipeline a JSON representation of a pipeline
	 * @return a JSON representation of a success message or an error message otherwise
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addPipelines(String pipeline)
	{
		//Store in pipeline database
		de.fzi.cep.sepa.model.client.Pipeline serverPipeline = Utils.getGson().fromJson(pipeline, de.fzi.cep.sepa.model.client.Pipeline.class);
		serverPipeline.setPipelineId(UUID.randomUUID().toString());
		serverPipeline.setRunning(false);
		pipelineStorage.store(serverPipeline);


		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			userStorage.addPipeline(username, serverPipeline.getPipelineId());
		}

		return constructSuccessMessage(NotificationType.PIPELINE_STORAGE_SUCCESS.uiNotification());
	}
	
	@Path("/{pipelineId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePipeline(@PathParam("pipelineId") String pipelineId)
	{
		try {
			//TODO you probably just want to delete the reference on the current user otherwise you have ot find all references
			//pipelineStorage.deletePipeline(pipelineId);
			if (getCurrentUsername() != null) userStorage.deletePipeline(getCurrentUsername(), pipelineId);

			return constructSuccessMessage(NotificationType.PIPELINE_STORAGE_SUCCESS.uiNotification());
		} catch (Exception e)
		{
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
	
	@Path("/{pipelineId}/start")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String startPipeline(@PathParam("pipelineId") String pipelineId)
	{
		try {
		de.fzi.cep.sepa.model.client.Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId);
		Operations.startPipeline(pipeline);
		return constructSuccessMessage(NotificationType.PIPELINE_START_SUCCESS.uiNotification());
		} catch (Exception e)
		{
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
	
	@Path("/{pipelineId}/stop")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String stopPipeline(@PathParam("pipelineId") String pipelineId)
	{
		try {
			de.fzi.cep.sepa.model.client.Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId);
			Operations.stopPipeline(pipeline);
			return constructSuccessMessage(NotificationType.PIPELINE_STOP_SUCCESS.uiNotification());
			} catch (Exception e)
			{
				return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
			}
	}
	
	@Path("/{pipelineId}/verify")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String verifyPipeline(@PathParam("pipelineId") String pipelineId)
	{
		//TODO
		return null;
	}
	
	@Path("/update")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String updateSEPAs(String pipeline)
	{
		try {
			return toJson(Operations.validatePipeline(Utils.getGson().fromJson(pipeline, de.fzi.cep.sepa.model.client.Pipeline.class), true));
		} catch (JsonSyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		} catch(NoMatchingSchemaException e) {
			return constructErrorMessage(new Notification(NotificationType.NO_VALID_CONNECTION.title(), NotificationType.NO_VALID_CONNECTION.description(), e.getMessage()));
		} catch(NoMatchingFormatException e) {
			return constructErrorMessage(new Notification(NotificationType.NO_MATCHING_FORMAT_CONNECTION.title(), NotificationType.NO_MATCHING_FORMAT_CONNECTION.description(), e.getMessage()));
		} catch(NoMatchingProtocolException e) {
			return constructErrorMessage(new Notification(NotificationType.NO_MATCHING_PROTOCOL_CONNECTION.title(), NotificationType.NO_MATCHING_PROTOCOL_CONNECTION.description(), e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}
	
	@Path("/recommend")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String recommendSepa(String pipeline)
	{
		try {
			return toJson(Operations.findRecommendedElements(Utils.getGson().fromJson(pipeline, de.fzi.cep.sepa.model.client.Pipeline.class)));
		} catch (JsonSyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		} catch (NoSuitableSepasAvailableException e) {
			return constructErrorMessage(new Notification(NotificationType.NO_SEPA_FOUND.title(), NotificationType.NO_SEPA_FOUND.description(), e.getMessage()));
		}  
		catch (Exception e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}

}
