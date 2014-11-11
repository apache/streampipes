package de.fzi.cep.sepa.manager.mapping;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.exceptions.TooManyEdgesException;
import de.fzi.cep.sepa.manager.pipeline.MappingCalculator;
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

public class TestMapping {

	public static void main(String[] args) throws Exception
	{
		StorageRequests requests = StorageManager.INSTANCE.getStorageAPI();
		SEP sep = requests.getSEPById("http://localhost:8089/twitter");
		SEPA sepa = requests.getSEPAById("http://localhost:8090/sepa/movement");
		
		SEPAClient sepaClient = ClientModelTransformer.toSEPAClientModel(sepa);
		StreamClient streamClient = ClientModelTransformer.toStreamClientModel(sep, sep.getEventStreams().get(0));
		
		System.out.println(sep.getName());
		System.out.println(sepa.getName());
		System.out.println(sepaClient.getInputNodes());
		System.out.println(streamClient.getName());
		
		List<SEPAElement> elements = new ArrayList<>();
		elements.add(streamClient);
		
		sepaClient.setConnectedTo(Arrays.asList("s"));
		streamClient.setDOM("s");
		sepaClient.setDOM("p");
		
		Pipeline pipeline = new Pipeline();
		pipeline.setSepas(Arrays.asList(sepaClient));
		pipeline.setStreams(Arrays.asList(streamClient));
		
		try {
			SEPAClient client = new MappingCalculator(pipeline).computeMapping();
			
			for(StaticProperty staticProperty : client.getStaticProperties())
			{
			System.out.println("SP");
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
		
	}
}
