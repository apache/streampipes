package de.fzi.cep.sepa.rest.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.execution.http.GraphSubmitter;
import de.fzi.cep.sepa.manager.matching.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.matching.TreeBuilder;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.sepa.model.client.util.Utils;

public class TestJSONConvert {
	
	

	public static void main(String[] argv){
		
		String content = null;
		
		
		try {
			content = new Scanner(new File("src\\main\\resources\\TestJSON.json")).useDelimiter("\\Z").next();
			System.out.println(content);
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		de.fzi.cep.sepa.model.client.Pipeline ServerPipeline = null;
		
		try{
		
		ServerPipeline = Utils.getGson().fromJson(content, de.fzi.cep.sepa.model.client.Pipeline.class);
		
		}catch (RuntimeException e){
			e.printStackTrace();
		}
		System.out.println("TEST");
		
		System.out.println("\n");
		System.out.println("Streams: " + ServerPipeline.getStreams().size());
		
		for (int i = 0; i < ServerPipeline.getStreams().size(); i++){
			System.out.println(ServerPipeline.getStreams().get(i).getName());
			System.out.println(ServerPipeline.getStreams().get(i).getElementId());
		}
		System.out.println("\n");
		System.out.println("Sepas: " + ServerPipeline.getSepas().size());
		for (int i = 0; i < ServerPipeline.getSepas().size(); i++){
			System.out.println(ServerPipeline.getSepas().get(i).getName());
			System.out.println(ServerPipeline.getSepas().get(i).getElementId());
		}
		System.out.println("\n");
		System.out.println("Action: ");
		System.out.println(ServerPipeline.getAction().getName());
		
		/*GenericTree<NamedSEPAElement> tree = new TreeBuilder(ServerPipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false);
		List<SEPAInvocationGraph> graphs = builder.buildGraph();*/
		//new GraphSubmitter(graphs).invokeGraphs();
	}
	
	
}
