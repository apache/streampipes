package de.fzi.cep.sepa.manager.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.manager.matching.PipelineValidationHandler;
import de.fzi.cep.sepa.messages.PipelineModification;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.StaticPropertyType;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.util.Utils;

public class TestMapping {

	public static void main(String[] args) throws Exception
	{
		StorageRequests requests = StorageManager.INSTANCE.getStorageAPI();
		SEP sep = requests.getSEPById("http://localhost:8089/twitter");
		SEPA sepa = requests.getSEPAById("http://localhost:8090/sepa/movement");
		
		SEPAClient sepaClient = ClientModelTransformer.toSEPAClientModel(sepa);
		StreamClient streamClient = ClientModelTransformer.toStreamClientModel(sep, sep.getEventStreams().get(1));
		
		System.out.println(sep.getName());
		System.out.println(sepa.getName());
		System.out.println(sepaClient.getInputNodes());
		System.out.println(streamClient.getName());
		System.out.println("--------");
		
		List<SEPAElement> elements = new ArrayList<>();
		elements.add(streamClient);
		
		sepaClient.setConnectedTo(Arrays.asList("s"));
		streamClient.setDOM("s");
		sepaClient.setDOM("p");
		
		Pipeline pipeline = new Pipeline();
		pipeline.setSepas(Arrays.asList(sepaClient));
		pipeline.setStreams(Arrays.asList(streamClient));
		
		PipelineModificationMessage message = new PipelineValidationHandler(pipeline, true).validateConnection().computeMappingProperties().getPipelineModificationMessage();
	
		System.out.println(Utils.getGson().toJson(message));
		for(PipelineModification modification : message.getPipelineModifications())
		{
			for(StaticProperty staticProperty : modification.getStaticProperties())
			{
				if (staticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY)
				{
					
					SelectFormInput input = (SelectFormInput) staticProperty.getInput();
					for(Option o : input.getOptions())
					{
						//System.out.println(o.getHumanDescription());
					}
				}
			}
		}
		
		/*
		try {
			//SEPAClient client = new MappingCalculator(pipeline).computeMapping();
			
			for(StaticProperty staticProperty : client.getStaticProperties())
			{
			System.out.println("SP, " +staticProperty.getName());
				if (staticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY)
				{
					System.out.println("MP");
					SelectFormInput input = (SelectFormInput) staticProperty.getInput();
					for(Option o : input.getOptions())
					{
						System.out.println(o.getHumanDescription());
					}
				}
			}
			
		} catch (TooManyEdgesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
}
