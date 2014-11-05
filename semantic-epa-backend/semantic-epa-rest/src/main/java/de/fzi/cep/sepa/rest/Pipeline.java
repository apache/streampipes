package de.fzi.cep.sepa.rest;

import java.util.List;

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
import de.fzi.cep.sepa.rest.messages.NotificationType;
import de.fzi.cep.sepa.rest.util.Utils;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;

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
		System.out.println(pipeline);
		
		de.fzi.cep.sepa.model.client.Pipeline ServerPipeline = Utils.getGson().fromJson(pipeline, de.fzi.cep.sepa.model.client.Pipeline.class);
		
		System.out.println("TEST");
		
		System.out.println("\n");
		System.out.println("Streams: " + ServerPipeline.getStreams().size());
		
		for (int i = 0; i < ServerPipeline.getStreams().size(); i++){
			System.out.println(ServerPipeline.getStreams().get(i).getName());
			System.out.println(ServerPipeline.getStreams().get(i).getElementId());
		}
		System.out.println("\n");
		System.out.println("Sepas: " + ServerPipeline.getSepas().size());
		for (int i = 0; i < ServerPipeline.getSepas().size(); i++){
			System.out.println(ServerPipeline.getSepas().get(i).getName());
			System.out.println(ServerPipeline.getSepas().get(i).getElementId());
		}
		System.out.println("\n");
		System.out.println("Action: ");
		System.out.println(ServerPipeline.getAction().getName());
		
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(ServerPipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false);
		List<SEPAInvocationGraph> graphs = builder.buildGraph();
		new GraphSubmitter(graphs).invokeGraphs();
		
		return "success";
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
			return constructErrorMessage(e, NotificationType.UNKNOWN_ERROR.uiNotification());
		}
	}
	
	@Path("/{pipelineId}/start")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String startPipeline(@PathParam("pipelineId") String pipelineId)
	{
		//TODO
		return null;
	}
	
	@Path("/{pipelineId}/stop")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String stopPipeline(@PathParam("pipelineId") String pipelineId)
	{
		//TODO
		return null;
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
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String updateSEPAs(String pipeline)
	{
		try {
			return toJson(Operations.validatePipeline(Utils.getGson().fromJson(pipeline, de.fzi.cep.sepa.model.client.Pipeline.class), true));
		} catch (JsonSyntaxException e) {
			return constructErrorMessage(e, NotificationType.UNKNOWN_ERROR.uiNotification());
		} catch (Exception e) {
			return constructErrorMessage(e, NotificationType.UNKNOWN_ERROR.uiNotification());
		}
	}

}
