package de.fzi.cep.sepa.actions.samples.areachart;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyList;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;

public class AreaChartController implements SemanticEventConsumerDeclarer {

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("/area", "Area chart", "Real-Time area chart", "");
		//sec.setIconUrl(ActionConfig.iconBaseUrl + "/Map_Icon_HQ.png");
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		EventPropertyList outputProperty = new EventPropertyList();
		outputProperty.setRuntimeName("output");
		
		EventProperty e1 = new EventPropertyPrimitive(Utils.createURI(SO.Text));
		EventProperty e2 = new EventPropertyPrimitive(Utils.createURI(SO.Number));
		
		outputProperty.setEventProperties(Arrays.asList(e1, e2));
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(Arrays.asList(outputProperty));
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "key", "Select key property"));
		staticProperties.add(new MappingPropertyUnary(URI.create(e2.getElementName()), "value", "Select value property"));

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		grounding.setTransportProtocol(new JmsTransportProtocol());
		sec.setSupportedGrounding(grounding);
		
		return sec;
	}

	@Override
	public boolean invokeRuntime(SecInvocation invocationGraph) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean detachRuntime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		String newUrl = graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getBrokerHostname().replace("tcp",  "ws") + ":61614";
		
		String listPropertyName = SepaUtils.getMappingPropertyName(graph, "key").split(",")[0];
		String keyName = SepaUtils.getMappingPropertyName(graph, "key").split(",")[1];
		String valueName = SepaUtils.getMappingPropertyName(graph, "value").split(",")[1];
		
		System.out.println(keyName);
		System.out.println(valueName);
		
		AreaChartParameters params = new AreaChartParameters("/topic/" + graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), newUrl, listPropertyName, keyName, valueName);
		
		return new AreaChartGenerator(params).generateHtml();
	}

}
