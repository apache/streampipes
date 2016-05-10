package de.fzi.cep.sepa.manager.mapping;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.exceptions.NoValidConnectionException;
import de.fzi.cep.sepa.manager.matching.PipelineValidationHandler;
import de.fzi.cep.sepa.messages.PipelineModification;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.StaticPropertyType;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.CheckboxInput;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;
import de.fzi.sepa.model.client.util.Utils;

public class TestCustomOutputStrategy {

	public static void main(String[] args) throws NoValidConnectionException, Exception
	{
		StorageRequests requests = StorageManager.INSTANCE.getStorageAPI();
		SepDescription sep = requests.getSEPById("http://localhost:8089/taxi");
		SepaDescription sepa = requests.getSEPAById("http://localhost:8090/sepa/project");
		SepaDescription sepa2 = requests.getSEPAById("http://localhost:8090/sepa/project");
		
		System.out.println(Utils.getGson().toJson(sep));
		
		SEPAClient sepaClient = ClientModelTransformer.toSEPAClientModel(sepa);
		sepaClient = updateSepaClient(sepaClient, sep.getEventStreams().get(0).getEventSchema().getEventProperties().get(0));
		
		SEPAClient sepaClient2 = ClientModelTransformer.toSEPAClientModel(sepa2);
		StreamClient streamClient = ClientModelTransformer.toStreamClientModel(sep, sep.getEventStreams().get(0));
		
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
		sepaClient2.setConnectedTo(Arrays.asList("p"));
		sepaClient2.setDOM("q");
		
		System.out.println(sepaClient.getDOM());
		Pipeline pipeline = new Pipeline();
		pipeline.setSepas(Arrays.asList(sepaClient, sepaClient2));
		pipeline.setStreams(Arrays.asList(streamClient));
		//pipeline.setAction(new ActionClient("", ""));
		
		PipelineModificationMessage message = new PipelineValidationHandler(pipeline, true).validateConnection().computeMappingProperties().getPipelineModificationMessage();
		System.out.println(Utils.getGson().toJson(message));
		/*
		for(PipelineModification modification : message.getPipelineModifications())
		{
			for(StaticProperty p : modification.getStaticProperties())
			{
				System.out.println(p.getType());
			}
		}
		*/
	}

	private static SEPAClient updateSepaClient(SEPAClient sepaClient, EventProperty prop) {
		SEPAClient result = sepaClient;
		
		for(StaticProperty p : result.getStaticProperties())
		{
			if (p.getType() == StaticPropertyType.CUSTOM_OUTPUT)
			{
				CheckboxInput input = ((CheckboxInput) p.getInput());
				List<Option> options = new ArrayList<>();
				Option option = new Option(prop.getRdfId().toString(), prop.getRuntimeName());
				option.setSelected(true);
				options.add(option);
				input.setOptions(options);
			}
		}
		return result;
	}

}
