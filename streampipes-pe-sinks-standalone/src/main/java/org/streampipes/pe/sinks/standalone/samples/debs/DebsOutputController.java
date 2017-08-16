package org.streampipes.pe.sinks.standalone.samples.debs;

import org.streampipes.commons.Utils;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DebsOutputController extends ActionController {

	@Override
	public SecDescription declareModel() {
		
		SecDescription sec = new SecDescription("debs", "Debs Challenge Output Generator", "", "");
		sec.setCategory(Arrays.asList(EcType.STORAGE.name()));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		FreeTextStaticProperty maxNumberOfRows = new FreeTextStaticProperty("path", "Path", "");
		staticProperties.add(maxNumberOfRows);

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		
		return sec;
	}

	@Override
	public Response invokeRuntime(SecInvocation sec) {
        String pipelineId = sec.getCorrespondingPipeline();
		String brokerUrl = createKafkaUri(sec);
		String inputTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		
		String path = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sec, "path"))).getValue();
		
		DebsParameters fileParameters = new DebsParameters(inputTopic, brokerUrl, path);
		
		//new Thread(new FileWriter(fileParameters)).start();

        return new Response(pipelineId, true);
	}

    @Override
    public Response detachRuntime(String pipelineId) {
        return new Response(pipelineId, true);
    }


}
