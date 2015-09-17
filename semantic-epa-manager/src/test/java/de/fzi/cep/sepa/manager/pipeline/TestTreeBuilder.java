package de.fzi.cep.sepa.manager.pipeline;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.GenericTreeNode;
import de.fzi.cep.sepa.commons.GenericTreeTraversalOrderEnum;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.manager.execution.http.GraphSubmitter;
import de.fzi.cep.sepa.manager.matching.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.matching.TreeBuilder;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.FormInput;
import de.fzi.cep.sepa.model.client.input.RadioInput;
import de.fzi.cep.sepa.model.client.input.TextInput;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.api.StorageRequests;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;

public class TestTreeBuilder {

	public static void main(String[] args) throws URISyntaxException
	{
		StorageRequests req = StorageManager.INSTANCE.getStorageAPI();
		SepDescription sep = req.getSEPById("http://localhost:8089/twitter");
		StreamClient stream1 = ClientModelTransformer.toStreamClientModel(sep, sep.getEventStreams().get(0));
		
		StreamClient stream2 = ClientModelTransformer.toStreamClientModel(sep, sep.getEventStreams().get(1));
		
		
		//SEPAClient client1 = ClientModelTransformer.toSEPAClientModel(req.getSEPAById("http://localhost:8090/sepa/movement"));
		SEPAClient client2 = ClientModelTransformer.toSEPAClientModel(req.getSEPAById("http://localhost:8090/sepa/textfilter"));
		for(StaticProperty p : client2.getStaticProperties())
		{
			FormInput input = p.getInput();
			if (input instanceof RadioInput)
			{
				((RadioInput) input).getOptions().get(1).setSelected(true);
			}
			else if (input instanceof TextInput)
			{
				if (p.getElementId().equals("urn:clarkparsia.com:empire:9409fbb8-9e9b-4daa-aa2c-7e6c2da3710d")) 
				{
					((TextInput) input).setValue("urn:clarkparsia.com:empire:9d48c515-9f42-4cd4-bcb9-df3711d7be7b");
				}
				else if (p.getElementId().equals("urn:clarkparsia.com:empire:e908b14f-9275-4b1b-a048-08397cd96930"))
				{
					((TextInput) input).setValue("RT");
				}
			}
		}
		
		ActionClient action = ClientModelTransformer.toSECClientModel(req.getSECById("http://localhost:8091/jms"));
		
		action.setConnectedTo(Utils.createList(client2.getElementId()));
		client2.setConnectedTo(Utils.createList(stream1.getElementId()));
		//client1.setConnectedTo(Utils.createList(client2.getElementId()));
		
		
		List<StreamClient> streams = new ArrayList<StreamClient>();
		streams.add(stream1);
		streams.add(stream2);
		
		List<SEPAClient> sepas = new ArrayList<>();
		sepas.add(client2);
		//sepas.add(client1);
		
		
		Pipeline pipeline = new Pipeline();
		pipeline.setAction(action);
		pipeline.setSepas(sepas);
		pipeline.setStreams(streams);
		
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
		System.out.println(tree.getNumberOfNodes());
		List<GenericTreeNode<NamedSEPAElement>> list = tree.build(GenericTreeTraversalOrderEnum.POST_ORDER);
		for(GenericTreeNode<NamedSEPAElement> node : list)
		{
			System.out.println(node.getData().getName());
			for(GenericTreeNode<NamedSEPAElement> child : node.getChildren())
			{
				System.out.print("child:" +child.getData().getName());
			}
			System.out.println();
		}
		
		System.out.println(tree.toStringWithDepth());
		
		System.out.println("*********\n");
		
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false, null);
		List<InvocableSEPAElement> graphs = builder.buildGraph();
		System.out.println(graphs.size());
		
		for(InvocableSEPAElement graph : graphs)
		{
			/*
			for(EventStream inputStream : graph.getInputStreams())
			{
				//System.out.println("in: " +inputStream.getEventGrounding().getTopicName());
				for(EventProperty p : inputStream.getEventSchema().getEventProperties())
				{
					System.out.print(p.getPropertyName() +", ");
				}
				System.out.println();
			}
			System.out.println("out: " +graph.getOutputStream().getEventGrounding().getTopicName());
			for(EventProperty p :graph.getOutputStream().getEventSchema().getEventProperties())
			{
				System.out.print(p.getPropertyName() +", ");
			}
			System.out.println("----");
			*/
		}
		
		new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs).invokeGraphs();
	}
}
