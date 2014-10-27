package de.fzi.cep.sepa.manager.pipeline;

import java.util.List;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.GenericTreeNode;
import de.fzi.cep.sepa.manager.util.TreeUtils;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;

public class TreeBuilder {

	private Pipeline rawPipeline;
	
	public TreeBuilder(Pipeline rawPipeline)
	{
		this.rawPipeline = rawPipeline;
	}
	
	public GenericTree<NamedSEPAElement> generateTree()
	{
		GenericTree<NamedSEPAElement> tree = new GenericTree<NamedSEPAElement>();
		
		// Action as root node
		GenericTreeNode<NamedSEPAElement> rootNode = new GenericTreeNode<>();
		rootNode.setData(transform(rawPipeline.getAction()));
		
		tree.setRoot(constructTree(rawPipeline.getAction().getConnectedTo(), rootNode));
		
		// Construct Tree
		return tree;
	}
	
	private GenericTreeNode<NamedSEPAElement> constructTree(List<String> connectedTo, GenericTreeNode<NamedSEPAElement> node) 
	{
		for(String edge : connectedTo)
		{
			SEPAElement element = TreeUtils.findSEPAElement(edge, rawPipeline.getSepas(), rawPipeline.getStreams());
			NamedSEPAElement child = transform(element);
			GenericTreeNode<NamedSEPAElement> nodeElement = new GenericTreeNode<NamedSEPAElement>(child);
			node.addChild(nodeElement);
			if (element.getConnectedTo() != null) constructTree(element.getConnectedTo(), nodeElement);
		}
		return node;
	}
	
	private NamedSEPAElement transform(SEPAElement element)
	{
		if (element instanceof ActionClient) return ClientModelTransformer.fromSECClientModel(((ActionClient) element));
		else if (element instanceof SEPAClient) return ClientModelTransformer.fromSEPAClientModel(((SEPAClient) element));
		else if (element instanceof StreamClient) return ClientModelTransformer.fromStreamClientModel(((StreamClient) element));
		//exceptions
		return null;
		
	}
}
