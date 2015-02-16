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

import com.google.gson.JsonSyntaxException;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.manager.pipeline.GraphSubmitter;
import de.fzi.cep.sepa.manager.pipeline.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.pipeline.TreeBuilder;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.SuccessMessage;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.util.Utils;

@Path("/pipelines")
public class Pipeline extends AbstractRestInterface {
	
	/**
	 * 
	 * @return a JSON representation of all available pipelines or an empty JSON list
	 */
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getPipelines()
	{
		return toJson(pipelineStorage.getAllPipelines());
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
		de.fzi.cep.sepa.model.client.Pipeline serverPipeline = Utils.getGson().fromJson(pipeline, de.fzi.cep.sepa.model.client.Pipeline.class);
		serverPipeline.setPipelineId(UUID.randomUUID().toString());
		serverPipeline.setRunning(false);
		pipelineStorage.store(serverPipeline);	
		
		return constructSuccessMessage(NotificationType.PIPELINE_STORAGE_SUCCESS.uiNotification());
	}
	
	@Path("/{pipelineId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePipeline(@PathParam("pipelineId") String pipelineId)
	{
		try {
			pipelineStorage.deletePipeline(pipelineId);
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
		} catch (Exception e) {
			return constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage()));
		}
	}

}
