package de.fzi.cep.sepa.esper.movement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.fzi.cep.sepa.desc.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.SEPA;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.DataType;
import de.fzi.cep.sepa.runtime.param.EndpointInfo;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.OutputStrategy;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

public class MovementController implements SemanticEventProcessingAgentDeclarer{
	private static final Logger logger = LoggerFactory.getLogger("MovementTest");
	private final String BROKER_ALIAS = "test";
	
	private static final Gson parser = new Gson();

	private static final CamelContext context = new DefaultCamelContext(); // routing context

	
	@Override
	public SEPA declareModel() {
		List<Domain> domains = new ArrayList<Domain>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT);
		SEPA desc = new SEPA("http://localhost:8089/sepa/movement", "Movement Analysis", "Movement Analysis Enricher", "/sepa/movement", domains);
		
		EventStream stream1 = new EventStream();
		
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();	
		eventProperties.add(new EventProperty(XSD._double.toString(), "abc", ""));
		eventProperties.add(new EventProperty(XSD._double.toString(), "abcd", ""));
		
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);
		stream1.setUri("http://localhost:8089/" +desc.getElementId());
		desc.addEventStream(stream1);
		
		return desc;
	}

	@Override
	public boolean invokeRuntime(SEPA sepa)  {
		
		for(EventStream stream : sepa.getEventStreams())
		{
			try {
				CamelConfig config = new CamelConfig.ActiveMQ(BROKER_ALIAS, "vm://localhost?broker.persistent=false");
				EndpointInfo source = EndpointInfo.of(BROKER_ALIAS + ":topic:" +stream.getEventGrounding().getTopicName(), DataType.JSON);
				EndpointInfo destination = EndpointInfo.of(BROKER_ALIAS + ":topic:MovementTopic", DataType.JSON);
		
				MovementParameter staticParam = new MovementParameter(stream.getName(), "MovementEvent", SEPAUtils.getStaticPropertyByName(sepa, "epsg").getValue(),
					Arrays.asList("userId", "timestamp", "latitude", "longitude"),
					Arrays.asList("userId"), "timestamp", "latitude", "longitude", 8000L); // TODO reduce param overhead
		
				String inEventName = "PositionEvent";
				Map<String, Class<?>> inEventType = stream.getEventSchema().toRuntimeMap();
		
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
		
	
		
				System.in.read(); // block til <ENTER>
			
				runtime.discard();
				System.exit(1);
				
			} catch(Exception e)
			{
				
			}
				
		}
		return false;
	}

	@Override
	public boolean detachRuntime(SEPA sepa) {
		// TODO Auto-generated method stub
		return false;
	}

}
