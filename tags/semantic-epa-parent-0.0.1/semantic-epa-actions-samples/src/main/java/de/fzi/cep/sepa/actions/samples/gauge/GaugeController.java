package de.fzi.cep.sepa.actions.samples.gauge;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.util.ActionUtils;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;

public class GaugeController implements SemanticEventConsumerDeclarer {

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("gauge", "Gauge", "Monitors numeric values");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/gauge_icon.png");
		sec.setEcTypes(Arrays.asList(EcType.VISUALIZATION_CHART.name()));
		
		EventStream stream1 = new EventStream();
		EventSchema schema1 = new EventSchema();
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventProperty e1 = EpRequirements.numberReq();
		eventProperties.add(e1);
		schema1.setEventProperties(eventProperties);
		stream1.setEventSchema(schema1);		
		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		sec.addEventStream(stream1);
	
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new FreeTextStaticProperty("min", "min value", ""));
		staticProperties.add(new FreeTextStaticProperty("max", "max value", ""));
		
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "Mapping", "Select Mapping", ""));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sec.setStaticProperties(staticProperties);
		sec.setSupportedGrounding(ActionUtils.getSupportedGrounding());
		
		
		return sec;
	}

	@Override
	public Response invokeRuntime(SecInvocation invocationGraph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		String newUrl = graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getBrokerHostname().replace("tcp",  "ws") + ":61614";
		
		String variableName = SepaUtils.getMappingPropertyName(graph, "Mapping");
		int min =  Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(graph, "min"))).getValue());
		int max =  Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(graph, "max"))).getValue());
		GaugeParameters lineChart = new GaugeParameters("/topic/" + graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), newUrl, variableName, min, max);
		
		return new GaugeGenerator(lineChart).generateHtml();
	}

}
