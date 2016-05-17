package de.fzi.proasense.demonstrator.adapter;

import static org.junit.Assert.*;

import org.junit.Rule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import de.fzi.proasense.demonstrator.adapter.siemens.FlowRateSensor;
import de.fzi.proasense.demonstrator.adapter.siemens.SiemensSensor;


public class SiemensSensorTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(18089);


	@Test
	public void testRequestData() {
		String url = "http://localhost:18089/test";
		String expected = "This is the request data";
		stubFor(get(urlEqualTo("/test"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/html").withBody(expected)));

		SiemensSensor sensor = new FlowRateSensor(url, "");
		String actual = sensor.requestData();

		assertEquals(expected, actual);

	}

}
