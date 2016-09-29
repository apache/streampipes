package de.fzi.cep.sepa.esper.project.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.StandardTransportFormat;

public class ProjectController extends FlatEpDeclarer<ProjectParameter>{

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

		return submit(staticParam, Project::new, sepa);

	}
}
