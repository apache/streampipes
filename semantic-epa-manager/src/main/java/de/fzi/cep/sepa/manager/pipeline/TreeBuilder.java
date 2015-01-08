package de.fzi.cep.sepa.manager.pipeline;

import java.util.List;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.GenericTreeNode;
import de.fzi.cep.sepa.manager.util.ClientModelUtils;
import de.fzi.cep.sepa.manager.util.TreeUtils;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public class TreeBuilder {

	private Pipeline rawPipeline;
	private SEPAElement rootElement;
	
	public TreeBuilder(Pipeline rawPipeline)
	{
		this.rawPipeline = rawPipeline;
		rootElement = rawPipeline.getAction();
	}
	
	public TreeBuilder(Pipeline rawPipeline, SEPAElement rootElement)
	{
		this.rawPipeline = rawPipeline;
		this.rootElement = rootElement;
	}
	
	public GenericTree<NamedSEPAElement> generateTree(boolean makeInvocationGraph)
	{
		GenericTree<NamedSEPAElement> tree = new GenericTree<NamedSEPAElement>();
		
		// Action as root node
		GenericTreeNode<NamedSEPAElement> rootNode = new GenericTreeNode<>();
		NamedSEPAElement element = ClientModelUtils.transform(rootElement);
		
		if (makeInvocationGraph)
		{
			element = new SEPAInvocationGraph((SEPA)element, rootElement.getDOM());
		}
		rootNode.setData(element);
		
		tree.setRoot(constructTree(rootElement.getConnectedTo(), rootNode, makeInvocationGraph));
		
		// Construct Tree
		return tree;
	}
	
	public GenericTree<SEPAElement> generateClientTree() throws Exception
	{
		GenericTree<SEPAElement> tree = new GenericTree<SEPAElement>();
		SEPAElement root = ClientModelUtils.getRootNode(rawPipeline);
		GenericTreeNode<SEPAElement> rootNode = new GenericTreeNode<>();
		rootNode.setData(root);
		tree.setRoot(constructClientTree(root.getConnectedTo(), rootNode));
		return tree;
		
	}
	
	private GenericTreeNode<SEPAElement> constructClientTree(List<String> connectedTo, GenericTreeNode<SEPAElement> node) 
	{
		for(String edge : connectedTo)
		{
			SEPAElement element = TreeUtils.findSEPAElement(edge, rawPipeline.getSepas(), rawPipeline.getStreams());
			GenericTreeNode<SEPAElement> nodeElement = new GenericTreeNode<SEPAElement>(element);
			node.addChild(nodeElement);
			if (element.getConnectedTo() != null) constructClientTree(element.getConnectedTo(), nodeElement);
		}
		return node;
	}
	
	private GenericTreeNode<NamedSEPAElement> constructTree(List<String> connectedTo, GenericTreeNode<NamedSEPAElement> node, boolean makeInvocationGraph) 
	{
		for(String edge : connectedTo)
		{
			SEPAElement element = TreeUtils.findSEPAElement(edge, rawPipeline.getSepas(), rawPipeline.getStreams());
			NamedSEPAElement child = ClientModelUtils.transform(element);
			
			if (makeInvocationGraph)
			{
				if (child instanceof SEPA) 
					{
						child = new SEPAInvocationGraph((SEPA)child, element.getDOM());
					}
			}
			GenericTreeNode<NamedSEPAElement> nodeElement = new GenericTreeNode<NamedSEPAElement>(child);
			node.addChild(nodeElement);
			if (element.getConnectedTo() != null) constructTree(element.getConnectedTo(), nodeElement, makeInvocationGraph);
		}
		return node;
	}
}
