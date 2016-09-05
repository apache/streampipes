package de.fzi.cep.sepa.rest.impl;

import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonSyntaxException;

import de.fzi.cep.sepa.commons.exceptions.NoMatchingFormatException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingProtocolException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingSchemaException;
import de.fzi.cep.sepa.commons.exceptions.NoSuitableSepasAvailableException;
import de.fzi.cep.sepa.manager.execution.status.PipelineStatusManager;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.messages.PipelineOperationStatus;
import de.fzi.cep.sepa.messages.SuccessMessage;
import de.fzi.cep.sepa.model.client.exception.InvalidConnectionException;
import de.fzi.cep.sepa.rest.annotation.GsonWithIds;
import de.fzi.cep.sepa.rest.api.IPipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

@Path("/v2/users/{username}/pipelines")
public class Pipeline extends AbstractRestInterface implements IPipeline {

	
	@Override
	public Response getAvailable(String username) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Response getFavorites(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/own")
	@GsonWithIds
	@Override
	public Response getOwn(@PathParam("username") String username) {
		return ok(userService.getOwnPipelines(username));
	}

	@Override
	public Response addFavorite(String username, String elementUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response removeFavorite(String username, String elementUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{pipelineId}/status")
	@GsonWithIds
	@Override
	public Response getPipelineStatus(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
		return ok(PipelineStatusManager.getPipelineStatus(pipelineId, 5));
	}
	
	@DELETE
	@Path("/{pipelineId}")
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	public Response removeOwn(@PathParam("username") String username, @PathParam("pipelineId") String elementUri) {
		StorageManager.INSTANCE.getPipelineStorageAPI().deletePipeline(elementUri);
		return statusMessage(Notifications.success("Pipeline deleted"));
	}

	@Override
	public String getAsJsonLd(String elementUri) {
		return null;
	}

	@GET
	@Path("/{pipelineId}")
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response getElement(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId) {
		return ok(StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId));
	}

	@Path("/{pipelineId}/start")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	public Response start(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId)
	{
		try {
		de.fzi.cep.sepa.model.client.Pipeline pipeline = userService.getPipeline(username, pipelineId);
		PipelineOperationStatus status = Operations.startPipeline(pipeline);
		return ok(status);
		} catch (Exception e)
		{
			e.printStackTrace();
			return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR));
		}
	}
	
	@Path("/{pipelineId}/stop")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	public Response stop(@PathParam("username") String username, @PathParam("pipelineId") String pipelineId)
	{
		try {
			de.fzi.cep.sepa.model.client.Pipeline pipeline = userService.getPipeline(username, pipelineId);
			PipelineOperationStatus status = Operations.stopPipeline(pipeline);
			return ok(status);
			} catch (Exception e)
			{
				return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
			}
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	public Response addPipeline(@PathParam("username") String username, de.fzi.cep.sepa.model.client.Pipeline pipeline)
	{
		String pipelineId = UUID.randomUUID().toString();
		pipeline.setPipelineId(pipelineId);
		pipeline.setRunning(false);
		pipeline.setCreatedByUser(username);
		//userService.addOwnPipeline(username, pipeline);
		Operations.storePipeline(pipeline);
		SuccessMessage message = Notifications.success(NotificationType.PIPELINE_STORAGE_SUCCESS);
		message.addNotification(new Notification("id", pipelineId));
		return ok(message);
	}

	@Path("/recommend")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	public Response recommend(de.fzi.cep.sepa.model.client.Pipeline pipeline)
	{
		try {
			return ok(Operations.findRecommendedElements(pipeline));
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

	@Path("/update")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	public Response update(de.fzi.cep.sepa.model.client.Pipeline pipeline)
	{
		try {
			return ok(Operations.validatePipeline(pipeline, true));
		} catch (JsonSyntaxException e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		} catch(NoMatchingSchemaException e) {
			return constructErrorMessage(new Notification(NotificationType.NO_VALID_CONNECTION.title(), NotificationType.NO_VALID_CONNECTION.description(), e.getMessage()));
		} catch(NoMatchingFormatException e) {
			return constructErrorMessage(new Notification(NotificationType.NO_MATCHING_FORMAT_CONNECTION.title(), NotificationType.NO_MATCHING_FORMAT_CONNECTION.description(), e.getMessage()));
		} catch(NoMatchingProtocolException e) {
			return constructErrorMessage(new Notification(NotificationType.NO_MATCHING_PROTOCOL_CONNECTION.title(), NotificationType.NO_MATCHING_PROTOCOL_CONNECTION.description(), e.getMessage()));
		} catch (InvalidConnectionException e) {
			return ok(e.getErrorLog());
		} catch (Exception e) {
			e.printStackTrace();
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}


	@PUT
	@Path("/{pipelineId}")
	@Produces(MediaType.APPLICATION_JSON)
	@GsonWithIds
	@Override
	public Response overwritePipeline(@PathParam("username") String username, de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		StorageManager.INSTANCE.getPipelineStorageAPI().updatePipeline(pipeline);
		return statusMessage(Notifications.success("Pipeline modified"));
	}

}
