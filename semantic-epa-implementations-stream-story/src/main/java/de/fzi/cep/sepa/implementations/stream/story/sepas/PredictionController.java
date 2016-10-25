package de.fzi.cep.sepa.implementations.stream.story.sepas;


import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.implementations.stream.story.main.ModelInvocationRequestParameters;
import de.fzi.cep.sepa.implementations.stream.story.main.StreamStoryInit;
import de.fzi.cep.sepa.implementations.stream.story.utils.AkerVariables;
import de.fzi.cep.sepa.implementations.stream.story.utils.EnrichedUtils;
import de.fzi.cep.sepa.implementations.stream.story.utils.ProaSenseSettings;
import de.fzi.cep.sepa.implementations.stream.story.utils.Utils;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.builder.StaticProperties;
import de.fzi.cep.sepa.model.impl.*;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PredictionController implements SemanticEventProcessingAgentDeclarer {


	@Override
	public SepaDescription declareModel() {
		SepaDescription desc = new SepaDescription("prediction", "Prediction",
				"Prediction description");

		desc.setCategory(Arrays.asList(EpaType.ALGORITHM.name()));
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.Enriched.topic()));
		grounding
				.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));

		EventStream stream = EnrichedUtils.getEnrichedStream();

//		stream.setEventGrounding(grounding);
		desc.setSupportedGrounding(grounding);
		desc.addEventStream(stream);

		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(Utils.getPredictedScheme());
		desc.setOutputStrategies(strategies);



		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(StaticProperties.integerFreeTextProperty("modelId", "Model ID", "The id of the model"));
		desc.setStaticProperties(staticProperties);

		return desc;
	}

	protected String getInputTopic(InvocableSEPAElement graph) {
		return graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
	}

	protected String getOutputTopic(SepaInvocation graph) {
		return graph.getOutputStream().getEventGrounding().getTransportProtocol().getTopicName();
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		int modelId = Integer.parseInt(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "modelId"))).getValue());


		String pipelineId = invocationGraph.getCorrespondingPipeline();
		String errorMessage = "";
		String inputTopic = getInputTopic(invocationGraph);
		String outputTopic = getOutputTopic(invocationGraph);

		ModelInvocationRequestParameters params = Utils.getModelInvocationRequestParameters(pipelineId, modelId,
				inputTopic, outputTopic);

		// TODO ask Luka
		JsonObject payload = Utils.getModelInvocationMessage(params, "Prediction");

		try {
			org.apache.http.client.fluent.Response res = Request.Post(StreamStoryInit.STREAMSTORY_URL + "streampipes/invoke").useExpectContinue()
					.version(HttpVersion.HTTP_1_1).bodyString(payload.toString(), ContentType.APPLICATION_JSON)
					.execute();
			Response ress = handleResponse(res, pipelineId);
			return ress;
//			return handleResponse(res, pipelineId);
		} catch (ClientProtocolException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Response(pipelineId, false, errorMessage);
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		// TODO make modelId dynamic
//		int modelId = 1;
		String errorMessage = "";

		int i = pipelineId.lastIndexOf("-");
		pipelineId = pipelineId.substring(0, i);

		JsonObject params = Utils.getModelDetachMessage(pipelineId);

		try {
			org.apache.http.client.fluent.Response res = Request.Post(StreamStoryInit.STREAMSTORY_URL + "streampipes/detach").useExpectContinue()
					.version(HttpVersion.HTTP_1_1).bodyString(params.toString(), ContentType.APPLICATION_JSON)
					.execute();
			return handleResponse(res, pipelineId);
		} catch (ClientProtocolException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		}

		return new Response(pipelineId, false, errorMessage);
	}

	private Response handleResponse(org.apache.http.client.fluent.Response response, String elementId) {
		String errorMessage = "";

		try {
			HttpResponse resp = response.returnResponse();
			if (200 == resp.getStatusLine().getStatusCode() || 204 == resp.getStatusLine().getStatusCode()) {
				return new Response(elementId, true);
			} else {
				return new Response(elementId, false,
						"There is a problem with Service Stream Story!\n" + resp.getStatusLine());
			}
		} catch (ClientProtocolException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			errorMessage = e.toString();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Response(elementId, false, errorMessage);
	}

}
