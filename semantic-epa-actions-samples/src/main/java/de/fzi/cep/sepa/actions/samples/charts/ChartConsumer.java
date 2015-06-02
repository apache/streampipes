package de.fzi.cep.sepa.actions.samples.charts;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;

public class ChartConsumer implements SemanticEventConsumerDeclarer {

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("/chart", "Line Chart", "Generates a line chart", "http://localhost:8080/img");
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = new EventPropertyPrimitive(de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number", MhWirth.RamVelSetpoint, MhWirth.RamPosSetpoint));
		eventProperties.add(e1);
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);		
		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		sec.addEventStream(stream1);
	
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		FreeTextStaticProperty chartTitle = new FreeTextStaticProperty("title", "Chart title");
		staticProperties.add(chartTitle);
		
		FreeTextStaticProperty xAxis = new FreeTextStaticProperty("xTitle", "X-Axis title");
		staticProperties.add(xAxis);
		
		FreeTextStaticProperty yAxis = new FreeTextStaticProperty("yTitle", "Y-Axis title");
		staticProperties.add(yAxis);
		
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "Mapping", "Select Mapping"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sec.setStaticProperties(staticProperties);
		
		return sec;
	}

	@Override
	public String invokeRuntime(SecInvocation graph) {
		String newUrl = graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getUri().replace("tcp",  "ws") + ":61614";
		
		String variableName = SepaUtils.getMappingPropertyName(graph, "Mapping");
		String title = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(graph, "title"))).getValue();
		String xAxis = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(graph, "xTitle"))).getValue();
		String yAxis = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(graph, "yTitle"))).getValue();
		
		LineChartParameters lineChart = new LineChartParameters(title, xAxis, yAxis, "/topic/" + graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), newUrl, variableName);
		
		return new ChartGenerator(lineChart).generateHtml();
	}

	@Override
	public boolean detachRuntime(SecInvocation graph) {
		// TODO Auto-generated method stub
		return false;
	}

}
