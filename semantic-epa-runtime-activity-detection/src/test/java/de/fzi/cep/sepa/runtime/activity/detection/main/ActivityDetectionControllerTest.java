package de.fzi.cep.sepa.runtime.activity.detection.main;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

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
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.activity.detection.utils.AkerVariables;
import de.fzi.cep.sepa.runtime.activity.detection.utils.Utils;
import de.fzi.cep.sepa.runtime.activity.detection.utils.UtilsTest;

public class ActivityDetectionControllerTest {
	private static final int WIREMOCK_PORT = 18089;
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(WIREMOCK_PORT);

	private String tmpUrl;

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
	public void testInvokeRuntime() {
		ModelInvocationRequestParameters params = new ModelInvocationRequestParameters("pip1", 1,
				ClientConfiguration.INSTANCE.getZookeeperHost(), ClientConfiguration.INSTANCE.getZookeeperPort(),
				AkerVariables.Enriched.topic(), ClientConfiguration.INSTANCE.getKafkaHost(),
				ClientConfiguration.INSTANCE.getKafkaPort(), "testtopic");
		stubFor(get(urlEqualTo("/init"))
				.withRequestBody(WireMock.equalToJson(UtilsTest.getModelInvocationJsonTemplate(params).toString()))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/html")));

		SepaInvocation invocation = getTestInvocation();
		new ActivityDetectionController().invokeRuntime(invocation);

		WireMock.verify(WireMock.postRequestedFor(urlEqualTo("/init"))
				.withRequestBody(WireMock.equalToJson(UtilsTest.getModelInvocationJsonTemplate(params).toString())));
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
		invocation.setCorrespondingPipeline("pip1");
		return invocation;

	}
}
