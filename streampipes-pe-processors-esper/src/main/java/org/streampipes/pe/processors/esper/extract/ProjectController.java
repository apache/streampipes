package org.streampipes.pe.processors.esper.extract;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.model.impl.EpaType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.CustomOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarerSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProjectController extends StandaloneEventProcessorDeclarerSingleton<ProjectParameter> {

	@Override
	public SepaDescription declareModel() {
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();			
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SepaDescription desc = new SepaDescription("project", "Projection", "Outputs a selectable subset of an input event type");

		stream1.setUri(EsperConfig.serverUrl +"/" +desc.getElementId() +"/stream");
		desc.addEventStream(stream1);
		desc.setCategory(Arrays.asList(EpaType.TRANSFORM.name()));
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new CustomOutputStrategy());
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		
		return desc;

	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
					System.out.println("invoke");
		List<NestedPropertyMapping> projectProperties = new ArrayList<>();
		
		for(EventProperty p : sepa.getOutputStream().getEventSchema().getEventProperties())
		{
			projectProperties.add(new NestedPropertyMapping(p.getRuntimeName(), SepaUtils.getFullPropertyName(p, sepa.getInputStreams().get(0).getEventSchema().getEventProperties(), "", '.')));
		}
		
		ProjectParameter staticParam = new ProjectParameter(
				sepa, 
				projectProperties);

		return submit(staticParam, Project::new);

	}
}
