package de.fzi.cep.sepa.manager.pipeline;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.manager.util.ClientModelUtils;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.StaticPropertyType;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;

public class MappingCalculator {

	private Pipeline pipeline;
	
	public MappingCalculator(Pipeline pipeline)
	{
		this.pipeline = pipeline;
	}
	
	public SEPAClient computeMapping() throws Exception
	{
		SEPAClient rootElement = ClientModelUtils.getRootNode(pipeline);
		
		// prepare a list of all pipeline elements without the root element
		List<SEPAElement> sepaElements = new ArrayList<SEPAElement>();
		sepaElements.addAll(pipeline.getSepas());
		sepaElements.addAll(pipeline.getStreams());
		sepaElements.remove(rootElement);
		
		SEPA currentSEPA = ClientModelTransformer.fromSEPAClientModel(rootElement);
		
		if (sepaElements.size() == 1 && rootElement.getInputNodes() == 1)
		{
			return computeUnaryMapping(sepaElements.get(0), rootElement);
		}
		
		return null;
	}
	
	private static SEPAClient computeUnaryMapping(SEPAElement sepaElement, SEPAClient currentElement)
	{
		
		if (sepaElement instanceof StreamClient) 
		{
			EventStream stream = ClientModelTransformer.fromStreamClientModel((StreamClient) sepaElement);
			for(StaticProperty p : currentElement.getStaticProperties())
			{
				if (p.getType() == StaticPropertyType.MAPPING_PROPERTY)
				{
					SelectFormInput input = new SelectFormInput();
					List<Option> options = new ArrayList<Option>();
					for(EventProperty eventProperty : stream.getEventSchema().getEventProperties())
					{
						options.add(new Option(eventProperty.getRdfId().toString(), eventProperty.getPropertyName()));
					}
					input.setOptions(options);
					
					p.setInput(input);
				}
			}
		}
		return currentElement;
			
	}
}
