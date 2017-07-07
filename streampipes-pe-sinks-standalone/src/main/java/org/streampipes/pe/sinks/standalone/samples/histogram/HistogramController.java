package org.streampipes.pe.sinks.standalone.samples.histogram;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.util.ActionUtils;
import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;

public class HistogramController implements SemanticEventConsumerDeclarer{

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("histogram", "Histogram", "Generates a histogram for time-series data", "http://localhost:8080/img");
		sec.setCategory(Arrays.asList(EcType.VISUALIZATION_CHART.name()));
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
		sec.setSupportedGrounding(ActionUtils.getSupportedGrounding());
		
		
		try {
			staticProperties.add(new MappingPropertyUnary(new URI(e1.getElementName()), "Mapping", "Select Mapping", ""));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sec.setStaticProperties(staticProperties);
		
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
		// TODO Auto-generated method stub
		return null;
	}

}
