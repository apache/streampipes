package org.streampipes.pe.sinks.standalone.samples.barchart;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.JmsTransportProtocol;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.MessageFormat;

public class BarChartController implements SemanticEventConsumerDeclarer {

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("bar", "Bar charts", "Real-Time bar chart", "");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/barchart_icon.png");
		sec.setCategory(Arrays.asList(EcType.VISUALIZATION_CHART.name()));
	
		EventPropertyList outputProperty = new EventPropertyList();
		outputProperty.setRuntimeName("output");
		
		EventProperty e1 = EpRequirements.stringReq();
		EventProperty e2 = EpRequirements.numberReq();
		
		outputProperty.setEventProperties(Arrays.asList(e1, e2));
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(Arrays.asList(outputProperty));
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "key", "Select key property", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(e2.getElementName()), "value", "Select value property", ""));

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportFormats(Arrays.asList(new TransportFormat(MessageFormat.Json)));
		grounding.setTransportProtocol(new JmsTransportProtocol());
		sec.setSupportedGrounding(grounding);
		
		return sec;
	}
    @Override
    public Response invokeRuntime(SecInvocation invocationGraph) {
        String pipelineId = invocationGraph.getCorrespondingPipeline();
        return new Response(pipelineId, true);
    }

    @Override
    public Response detachRuntime(String pipelineId) {
        return new Response(pipelineId, true);
    }

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation graph) {
		String newUrl = graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getBrokerHostname().replace("tcp",  "ws") + ":61614";
		System.out.println(SepaUtils.getMappingPropertyName(graph, "key"));
		String listPropertyName = SepaUtils.getMappingPropertyName(graph, "key").split(",")[0];
		String keyName = SepaUtils.getMappingPropertyName(graph, "key").split(",")[1];
		String valueName = SepaUtils.getMappingPropertyName(graph, "value").split(",")[1];
		
		System.out.println(keyName);
		System.out.println(valueName);
		
		BarChartParameters params = new BarChartParameters("/topic/" + graph.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName(), newUrl, listPropertyName, keyName, valueName);
		
		return new BarchartGenerator(params).generateHtml();
	}

}
