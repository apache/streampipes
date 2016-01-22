package semantic_epa_sources_demonstrator.sources_demonstrator;

import static org.junit.Assert.*;

import org.junit.Rule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;


public class SensorTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(18089);


	@Test
	public void testRequestData() {
		String url = "http://localhost:18089/test";
		String expected = "This is the request data";
		stubFor(get(urlEqualTo("/test"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/html").withBody(expected)));

		Sensor sensor = new FlowRateSensor(url, "");
		String actual = sensor.requestData();

		assertEquals(expected, actual);

	}

}
