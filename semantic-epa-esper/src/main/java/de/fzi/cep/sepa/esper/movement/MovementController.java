package de.fzi.cep.sepa.esper.movement;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.desc.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.param.CamelConfig;
import de.fzi.cep.sepa.runtime.param.DataType;
import de.fzi.cep.sepa.runtime.param.EndpointInfo;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

public class MovementController implements SemanticEventProcessingAgentDeclarer {

	private static final Logger logger = LoggerFactory
			.getLogger("MovementTest");

	private static final String BROKER_ALIAS = "test-jms";
	private static final CamelContext context = new DefaultCamelContext(); // routing
																			// context

	private SEPAInvocationGraph graph;

	@Override
	public SEPA declareModel() {

		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		SEPA desc = new SEPA("/sepa/movement", "Movement Analysis",
				"Movement Analysis Enricher", "", "/sepa/movement", domains);
		// desc.setIconUrl(EsperConfig.iconBaseUrl + "/Proximity_Icon_HQ.png");
		try {
			EventStream stream1 = new EventStream();

			EventSchema schema1 = new EventSchema();
			List<EventProperty> eventProperties = new ArrayList<EventProperty>();
			EventProperty e1 = new EventProperty(de.fzi.cep.sepa.commons.Utils.createURI(
					"http://test.de/latitude"));
			EventProperty e2 = new EventProperty(de.fzi.cep.sepa.commons.Utils.createURI(
					"http://test.de/longitude"));
			eventProperties.add(e1);
			eventProperties.add(e2);
			
		

			schema1.setEventProperties(eventProperties);
			stream1.setEventSchema(schema1);
			stream1.setUri("http://localhost:8089/" + desc.getElementId());
			desc.addEventStream(stream1);

			List<OutputStrategy> outputStrategies = new ArrayList<OutputStrategy>();
			
			AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

			List<EventProperty> appendProperties = new ArrayList<EventProperty>();
			appendProperties.add(new EventProperty(XSD._double.toString(),
					"speed", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/speed")));
			appendProperties.add(new EventProperty(XSD._double.toString(),
					"bearing", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/bearing")));
			appendProperties.add(new EventProperty(XSD._double.toString(),
					"distance", "", de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/distance")));
			outputStrategy.setEventProperties(appendProperties);
			outputStrategies.add(outputStrategy);
			desc.setOutputStrategies(outputStrategies);
			
			List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
			
			OneOfStaticProperty epsg = new OneOfStaticProperty("epsg", "Select Projection");
			epsg.addOption(new Option("EPSG-4326"));
			epsg.addOption(new Option("EPSG-4329"));
			staticProperties.add(epsg);
			//TODO mapping properties
			staticProperties.add(new MappingProperty(new URI(e1.getElementName()), "latitude", "Select Latitude Mapping"));
			staticProperties.add(new MappingProperty(new URI(e2.getElementName()), "longitude", "Select Longitude Mapping"));
			desc.setStaticProperties(staticProperties);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Test invocation
		/*
		 * SEP sep; try { sep = Transformer.fromJsonLd(SEP.class,
		 * HttpJsonParser.getContentFromUrl("http://localhost:8089/twitter/t"));
		 *
		 * graph = new SEPAInvocationGraph();
		 * sep.getEventStreams().get(0).setName("TwitterEvent");
		 * graph.addInputStream(sep.getEventStreams().get(0));
		 * 
		 * EventStream outputStream = new EventStream();
		 * 
		 * EventSchema originalSchema =
		 * sep.getEventStreams().get(0).getEventSchema();
		 * originalSchema.addEventProperty(new
		 * EventProperty(XSD._string.toString(), "distance", ""));
		 * originalSchema.addEventProperty(new
		 * EventProperty(XSD._string.toString(), "speed", ""));
		 * originalSchema.addEventProperty(new
		 * EventProperty(XSD._string.toString(), "bearing", ""));
		 * 
		 * outputStream.setEventSchema(originalSchema);
		 * 
		 * EventGrounding targetEventGrounding = new EventGrounding();
		 * targetEventGrounding.setPort(61616);
		 * targetEventGrounding.setUri("tcp://localhost");
		 * targetEventGrounding.setTopicName("FZI.SEPA.Test");
		 * 
		 * 
		 * List<StaticProperty> staticProperties = new
		 * ArrayList<StaticProperty>(); StaticProperty epsg = new
		 * StaticProperty("epsg", "epsg-desc", XSD._string.toString());
		 * epsg.setValue("EPSG:4326"); staticProperties.add(epsg);
		 * StaticProperty xProp = new StaticProperty("latitude", "latitude",
		 * XSD._string.toString()); xProp.setValue("latitude");
		 * staticProperties.add(xProp);
		 * 
		 * StaticProperty yProp = new StaticProperty("longitude", "longitude",
		 * XSD._string.toString()); yProp.setValue("longitude");
		 * staticProperties.add(yProp);
		 * 
		 * graph.setStaticProperties(staticProperties);
		 * 
		 * outputStream.setEventGrounding(targetEventGrounding);
		 * graph.setOutputStream(outputStream); graph.setName("TwitterEvent");
		 * System.out.println(outputStream.getEventGrounding().getUri());
		 * 
		 * } catch (ClientProtocolException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (URISyntaxException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
		return desc;
	}

	public SEPAInvocationGraph declareInvocModel() {
		return graph;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
		EndpointInfo destination = EndpointInfo.of(BROKER_ALIAS + ":topic:"
				+ sepa.getOutputStream().getEventGrounding().getTopicName(),
				DataType.JSON);
		for (EventStream stream : sepa.getInputStreams()) {
			try {
				CamelConfig config = new CamelConfig.ActiveMQ(BROKER_ALIAS,
						stream.getEventGrounding().getUri());
				EndpointInfo source = EndpointInfo.of(BROKER_ALIAS + ":topic:"
						+ stream.getEventGrounding().getTopicName(),
						DataType.JSON);

				String epsgProperty = ((FreeTextStaticProperty) (SEPAUtils
						.getStaticPropertyByName(sepa, "epsg"))).getValue();
				String xProperty = SEPAUtils.getMappingPropertyName(sepa,
						"latitude");
				String yProperty = SEPAUtils.getMappingPropertyName(sepa,
						"longitude");

				MovementParameter staticParam = new MovementParameter(
						stream.getName(), sepa.getOutputStream().getName(),
						epsgProperty, stream.getEventSchema().toPropertyList(),
						Arrays.asList("userName"), "timestamp", xProperty,
						yProperty, 8000L); // TODO reduce param overhead

				String inEventName = stream.getName();
				Map<String, Class<?>> inEventType = stream.getEventSchema()
						.toRuntimeMap();

				EngineParameters<MovementParameter> engineParams = new EngineParameters<>(
						Collections.singletonMap(inEventName, inEventType),
						new de.fzi.cep.sepa.runtime.param.OutputStrategy.Rename(
								"MovementEvent", inEventName), staticParam);

				RuntimeParameters<MovementParameter> params = new RuntimeParameters<>(
						"movement-test", MovementAnalysis::new, engineParams,
						Arrays.asList(config), destination,
						Arrays.asList(source));

				EPRuntime runtime = new EPRuntime(context, params);

				context.addRoutes(new RouteBuilder() {
					@Override
					public void configure() throws Exception {
						from("test-jms:topic:MovementTopic") // .to("file://test")
								.process(
										ex -> logger.info(
												"Receiving Event: {}",
												new String((byte[]) ex.getIn()
														.getBody())));
					}
				});

				ProducerTemplate template = context.createProducerTemplate();
				template.setDefaultEndpointUri(BROKER_ALIAS
						+ ":topic:PositionTopic");

				context.start();

				System.in.read(); // block til <ENTER>

				runtime.discard();
				System.exit(1);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	@Override
	public boolean detachRuntime(SEPAInvocationGraph sepa) {
		// TODO Auto-generated method stub
		return false;
	}

}
