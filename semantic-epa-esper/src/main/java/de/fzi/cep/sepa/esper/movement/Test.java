package de.fzi.cep.sepa.esper.movement;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.DataType;
import de.fzi.cep.sepa.runtime.param.EndpointInfo;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.OutputStrategy;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

public class Test {
	private static final Logger logger = LoggerFactory.getLogger("MovementTest");

	private static final Gson parser = new Gson();

	private static final CamelContext context = new DefaultCamelContext(); // routing context

	private static final String BROKER_ALIAS = "test-jms";

	public static void main(String[] args) throws Exception {
		CamelConfig config = new CamelConfig.ActiveMQ(BROKER_ALIAS, "vm://localhost?broker.persistent=false");
		EndpointInfo source = EndpointInfo.of(BROKER_ALIAS + ":topic:PositionTopic", DataType.JSON);
		EndpointInfo destination = EndpointInfo.of(BROKER_ALIAS + ":topic:MovementTopic", DataType.JSON);

		MovementParameter staticParam = new MovementParameter("PositionEvent", "MovementEvent", "EPSG:4326",
			Arrays.asList("userId", "timestamp", "latitude", "longitude"),
			Arrays.asList("userId"), "timestamp", "latitude", "longitude", 8000L); // TODO reduce param overhead

		String inEventName = "PositionEvent";
		HashMap<String, Class<?>> inEventType = new HashMap<>();
		inEventType.put("userId", Integer.class);
		inEventType.put("timestamp", Long.class); // double ?
		inEventType.put("latitude", Double.class);
		inEventType.put("longitude", Double.class);
		inEventType.put("name", String.class);

		EngineParameters<MovementParameter> engineParams = new EngineParameters<>(
			Collections.singletonMap(inEventName, inEventType),
			new OutputStrategy.Rename("MovementEvent", inEventName), staticParam);

		RuntimeParameters<MovementParameter> params = new RuntimeParameters<>("movement-test",
			MovementAnalysis::new, engineParams, Arrays.asList(config), destination, Arrays.asList(source));

		EPRuntime runtime = new EPRuntime(context, params);

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("test-jms:topic:MovementTopic") // .to("file://test")
				.process(ex -> logger.info("Receiving Event: {}", new String((byte[]) ex.getIn().getBody())));
			}
		});

		ProducerTemplate template = context.createProducerTemplate();
		template.setDefaultEndpointUri(BROKER_ALIAS + ":topic:PositionTopic");

		context.start();

		template.sendBody(sampleEvent(1, System.currentTimeMillis(), 49.009304, 8.410172)); // TODO use themepark gen
		template.sendBody(sampleEvent(1, System.currentTimeMillis() + 1000L, 49.011071, 8.410168));

		System.in.read(); // block til <ENTER>

		runtime.discard();
		System.exit(1);

	}

	private static String sampleEvent(int userId, long timestamp, double lat, double lng) {
		Map<String, Object> event = new HashMap<>();
		event.put("userId", userId);
		event.put("timestamp", timestamp); // TODO Problem: will be deserialized to double
		event.put("latitude", lat);
		event.put("longitude", lng);
		event.put("name", "PositionEvent");
		return parser.toJson(event);
	}
}
