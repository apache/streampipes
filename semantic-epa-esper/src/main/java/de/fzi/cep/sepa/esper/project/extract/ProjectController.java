package de.fzi.cep.sepa.esper.project.extract;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;

public class ProjectController extends EsperDeclarer<ProjectParameter>{

	@Override
	public SEPA declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();			
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);
		
		SEPA desc = new SEPA("/sepa/project", "General Project EPA", "Outputs a selectable subset of an input event type", "", "/sepa/project", domains);

		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new CustomOutputStrategy());
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		desc.setStaticProperties(staticProperties);
		
		return desc;

	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
		EventStream inputStream = sepa.getInputStreams().get(0);
		
		EventGrounding inputGrounding = inputStream.getEventGrounding();
		EventGrounding outputGrounding = sepa.getOutputStream().getEventGrounding();
		String topicPrefix = "topic://";
		
		String inName = topicPrefix + inputGrounding.getTopicName();
		String outName = topicPrefix + outputGrounding.getTopicName();
		
		List<NestedPropertyMapping> projectProperties = new ArrayList<>();
		
		for(EventProperty p : sepa.getOutputStream().getEventSchema().getEventProperties())
		{
			projectProperties.add(new NestedPropertyMapping(p.getPropertyName(), SEPAUtils.getFullPropertyName(p, sepa.getInputStreams().get(0).getEventSchema().getEventProperties(), "", '.')));
		}
		
		ProjectParameter staticParam = new ProjectParameter(
				inName, 
				outName, 
				inputStream.getEventSchema().toPropertyList(), 
				sepa.getOutputStream().getEventSchema().toPropertyList(), 
				projectProperties);
	
		try {
			return runEngine(staticParam, Project::new, sepa);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean detachRuntime(SEPAInvocationGraph sepa) {
		// TODO Auto-generated method stub
		return false;
	}

}
