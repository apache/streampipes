package de.fzi.cep.sepa.runtime.activity.detection.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.json.Json;
import javax.json.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.clarkparsia.empire.SupportsRdfId.RdfKey;
import com.clarkparsia.empire.annotation.RdfGenerator;
import com.clarkparsia.empire.annotation.RdfId;
import com.clarkparsia.empire.annotation.SupportsRdfIdImpl;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.runtime.activity.detection.main.ActivityDetectionController;
import de.fzi.cep.sepa.runtime.activity.detection.main.ModelInvocationRequestParameters;

public class UtilsTest {
	@Test
	public void testGetActivityDetection() {
		OutputStrategy output = Utils.getActivityDetection();

		assertTrue(output instanceof FixedOutputStrategy);

		FixedOutputStrategy actual = (FixedOutputStrategy) output;

		assertEquals(actual.getEventProperties().size(), 4);
		assertEquals(actual.getEventProperties().get(0).getRuntimeName(), "activityId");
		assertEquals(actual.getEventProperties().get(1).getRuntimeName(), "startTime");
		assertEquals(actual.getEventProperties().get(2).getRuntimeName(), "endTime");
		assertEquals(actual.getEventProperties().get(3).getRuntimeName(), "description");

	}

	@Test
	public void testGetModelInvocationRequestParameters() {
		String pipelineId = "pipelineId";
		int modelId = 1111;
		String inputTopic = "inputTopic";
		String outputTopic = "outputTopic";
		ModelInvocationRequestParameters expected = new ModelInvocationRequestParameters(pipelineId, modelId,
				ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort(),
				inputTopic, ClientConfiguration.INSTANCE.getKafkaHost(), ClientConfiguration.INSTANCE.getKafkaPort(),
				outputTopic);
		ModelInvocationRequestParameters actual = Utils.getModelInvocationRequestParameters(pipelineId, modelId,
				inputTopic, outputTopic);

		assertEquals(expected, actual);
	}

	@Test
	public void testModelInvocationMessage() {

		ModelInvocationRequestParameters params = new ModelInvocationRequestParameters("abc", 1, "http://localhost",
				2181, "inputTopic", "http://localhost", 9092, "outputTopic");
		JsonObject actual = Utils.getModelInvocationMessage(params);
		JsonObject expected = getModelInvocationJsonTemplate(new ModelInvocationRequestParameters("abc", 1,
				"http://localhost", 2181, "inputTopic", "http://localhost", 9092, "outputTopic"));

		assertEquals(expected, actual);
	}

	public static JsonObject getModelInvocationJsonTemplate(ModelInvocationRequestParameters params) {
		return Json.createObjectBuilder().add("pipelineId", params.getPipelineId())
				.add("analyticsOperation", "ActivityDetection") //
				.add("modelId", params.getModelId()) //
				.add("input",
						Json.createObjectBuilder() //
								.add("zookeeperHost", params.getZookeeperHost()) //
								.add("zookeeperPort", params.getZookeeperPort()) //
								.add("inputTopic", params.getInputTopic())) //
				.add("output",
						Json.createObjectBuilder().add("kafkaHost", params.getKafkaHost()) //
								.add("kafkaPort", params.getKafkaPort()) //
								.add("outputTopic", params.getOutputTopic()))
				.build();
	}

}
