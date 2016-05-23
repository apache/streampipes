package de.fzi.cep.sepa.implementations.stream.story.activitydetection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

import javax.json.Json;

import de.fzi.cep.sepa.implementations.stream.story.main.ModelInvocationRequestParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.clarkparsia.empire.SupportsRdfId.RdfKey;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.implementations.stream.story.utils.AkerVariables;
import de.fzi.cep.sepa.implementations.stream.story.utils.UtilsTest;

public class ActivityDetectionControllerTest {
	private static final int WIREMOCK_PORT = 18089;
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);

	private String tmpUrl;
	private static String pipelineId = "pip1";

	@Before
	public void before() {
		tmpUrl = ActivityDetectionController.STREAMSTORY_URL;
		ActivityDetectionController.STREAMSTORY_URL = "http://localhost:" + WIREMOCK_PORT;
	}

	@After
	public void after() {
		ActivityDetectionController.STREAMSTORY_URL = tmpUrl;
	}

	@Test
	public void testInvokeRuntimeSuccessfully() {
		ModelInvocationRequestParameters params = getTestParams();

		stubFor(post(urlEqualTo("/invoke"))
				.withRequestBody(WireMock.equalToJson(UtilsTest.getModelInvocationJsonTemplate(params).toString()))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")));

		SepaInvocation invocation = getTestInvocation();
		Response actual = new ActivityDetectionController().invokeRuntime(invocation);
		Response expected = new Response(pipelineId, true);

		assertEquals(expected, actual);

		WireMock.verify(postRequestedFor(urlEqualTo("/invoke"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withRequestBody(equalToJson(UtilsTest.getModelInvocationJsonTemplate(params).toString())));
	}
	
	@Test
	public void testInvokeRuntimeFailure() {
		SepaInvocation invocation = getTestInvocation();
		Response actual = new ActivityDetectionController().invokeRuntime(invocation);
		Response expected = new Response(pipelineId, false, "There is a problem with Service Stream Story!\n" + 
				"HTTP/1.1 404 Not Found");

		assertEquals(expected, actual);
	}

	@Test
	public void testDetachRuntime() {
		stubFor(post(urlEqualTo("/detach"))
				.willReturn(aResponse().withStatus(200)));
		
		String expectedParams = Json.createObjectBuilder().add("pipelineId", pipelineId)
				.add("modelId", 1).build().toString();
		
		
		Response actual = new ActivityDetectionController().detachRuntime(pipelineId);
		Response expected = new Response(pipelineId, true);
		

		assertEquals(expected, actual);
		WireMock.verify(postRequestedFor(urlEqualTo("/detach"))
				.withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
				.withRequestBody(equalToJson(expectedParams)));
	
	}

	private ModelInvocationRequestParameters getTestParams() {
		return new ModelInvocationRequestParameters(pipelineId, 1,
				ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort(),
				AkerVariables.Enriched.topic(), ClientConfiguration.INSTANCE.getKafkaHost(),
				ClientConfiguration.INSTANCE.getKafkaPort(), "testtopic");

	}

	private SepaInvocation getTestInvocation() {
		SepaDescription sepa = new ActivityDetectionController().declareModel();
		RdfKey<String> id = new RdfKey<String>() {
			@Override
			public String value() {
				return "lalak";
			}
		};
		sepa.setRdfId(id);
		SepaInvocation invocation = new SepaInvocation(sepa);
		EventStream outputStream = new EventStream();
		EventGrounding outputGrounding = new EventGrounding();
		TransportProtocol outputProtocol = new KafkaTransportProtocol("", 0, "testtopic", "", 0);
		outputGrounding.setTransportProtocol(outputProtocol);
		outputStream.setEventGrounding(outputGrounding);
		invocation.setOutputStream(outputStream);
		invocation.setCorrespondingPipeline(pipelineId);
		return invocation;

	}
}
