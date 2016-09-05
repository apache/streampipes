package de.fzi.cep.sepa.manager.matching;

import java.util.List;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.GenericTreeNode;
import de.fzi.cep.sepa.manager.util.TreeUtils;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

public class TreeBuilder {

	private Pipeline rawPipeline;
	private NamedSEPAElement rootElement;
	
	public TreeBuilder(Pipeline rawPipeline)
	{
		this.rawPipeline = rawPipeline;
		rootElement = rawPipeline.getAction();
	}
	
	public TreeBuilder(Pipeline rawPipeline, NamedSEPAElement rootElement)
	{
		this.rawPipeline = rawPipeline;
		this.rootElement = rootElement;
	}
	
	public GenericTree<NamedSEPAElement> generateTree(boolean makeInvocationGraph)
	{
		GenericTree<NamedSEPAElement> tree = new GenericTree<NamedSEPAElement>();
		
		// Action as root node
		GenericTreeNode<NamedSEPAElement> rootNode = new GenericTreeNode<>();

		if (makeInvocationGraph)
		{
			if (rootElement instanceof SepaInvocation) rootElement = new SepaInvocation((SepaInvocation)rootElement);
			else rootElement = new SecInvocation((SecInvocation)rootElement);
		}
		rootNode.setData(rootElement);
		
		tree.setRoot(constructTree(rootElement.getConnectedTo(), rootNode, makeInvocationGraph));
		tree.toStringWithDepth();
		// Construct Tree
		return tree;
	}

	private GenericTreeNode<NamedSEPAElement> constructTree(List<String> connectedTo, GenericTreeNode<NamedSEPAElement> node, boolean makeInvocationGraph) 
	{
		for(String edge : connectedTo)
		{
			NamedSEPAElement element = TreeUtils.findSEPAElement(edge, rawPipeline.getSepas(), rawPipeline.getStreams());

			if (makeInvocationGraph)
			{
				if (element instanceof SepaDescription)
					{
						element = new SepaInvocation((SepaDescription)element);
					}
			}
			GenericTreeNode<NamedSEPAElement> nodeElement = new GenericTreeNode<NamedSEPAElement>(element);
			node.addChild(nodeElement);
			if (element.getConnectedTo() != null) constructTree(element.getConnectedTo(), nodeElement, makeInvocationGraph);
		}
		return node;
	}
}
