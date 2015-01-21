package de.fzi.cep.sepa.esper.aggregate.rate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ontoware.rdf2go.vocabulary.XSD;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.AbstractEsperTemplate;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.filter.numerical.NumericalFilter;
import de.fzi.cep.sepa.esper.filter.numerical.NumericalFilterParameter;
import de.fzi.cep.sepa.esper.util.NumericalOperator;
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
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.util.SEPAUtils;
import de.fzi.cep.sepa.storage.util.Transformer;

public class EventRateController extends AbstractEsperTemplate<EventRateParameter> {

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
		
		SEPA desc = new SEPA("/sepa/eventrate", "Event rate", "Computes current event rate", "", "/sepa/eventrate", domains);
		
		//TODO check if needed
		stream1.setUri(EsperConfig.serverUrl +desc.getElementId());
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		
		
		EventProperty outputProperty = new EventProperty(XSD._double.toString(),
				"rate", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"));
		FixedOutputStrategy outputStrategy = new FixedOutputStrategy(Utils.createList(outputProperty));
		strategies.add(outputStrategy);
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		staticProperties.add(new FreeTextStaticProperty("rate", "average/sec"));
		staticProperties.add(new FreeTextStaticProperty("output", "output every (seconds)"));
		desc.setStaticProperties(staticProperties);
		
		return desc;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
	
		String avgRate = ((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "rate"))).getValue();
		
		String outputRate = ((FreeTextStaticProperty) (SEPAUtils
				.getStaticPropertyByName(sepa, "output"))).getValue();
	
		String topicPrefix = "topic://";
		EventRateParameter staticParam = new EventRateParameter(topicPrefix + sepa.getInputStreams().get(0).getEventGrounding().getTopicName(), topicPrefix + sepa.getOutputStream().getEventGrounding().getTopicName(), sepa.getInputStreams().get(0).getEventSchema().toPropertyList(), sepa.getOutputStream().getEventSchema().toPropertyList(), Integer.parseInt(avgRate), Integer.parseInt(outputRate), topicPrefix + sepa.getOutputStream().getEventGrounding().getTopicName());
		
		
		try {
			
			return super.bind(staticParam, EventRate::new, sepa);
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