package de.fzi.cep.sepa.manager.pipeline;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.exceptions.NoValidConnectionException;
import de.fzi.cep.sepa.manager.util.ClientModelUtils;
import de.fzi.cep.sepa.manager.util.TreeUtils;
import de.fzi.cep.sepa.manager.validator.ConnectionValidator;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.PipelineModification;
import de.fzi.cep.sepa.model.client.PipelineModificationMessage;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticPropertyType;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public class PipelineValidationHandler {

	Pipeline pipeline;
	PipelineModificationMessage pipelineModificationMessage;
	
	List<SEPAInvocationGraph> invocationGraphs;
	List<SEPAElement> sepaClientElements;
	
	SEPAClient rootElement;
	NamedSEPAElement sepaRootElement;
	
	GenericTree<SEPAElement> clientTree;
	GenericTree<NamedSEPAElement> modelTree;
	
	public PipelineValidationHandler(Pipeline pipeline, boolean isPartial) throws Exception
	{
		this.pipeline = pipeline;
		this.rootElement = ClientModelUtils.getRootNode(pipeline);
		this.sepaRootElement = ClientModelUtils.transform(rootElement);
		
		// prepare a list of all pipeline elements without the root element
		List<SEPAElement> sepaElements = new ArrayList<SEPAElement>();
		sepaElements.addAll(pipeline.getSepas());
		sepaElements.addAll(pipeline.getStreams());
		sepaElements.remove(rootElement);
		
		// we need a tree of invocation graphs if there is more than one SEPA 
		clientTree = new TreeBuilder(pipeline, rootElement).generateClientTree();
		if (clientTree.maxDepth(clientTree.getRoot()) > 2)
			modelTree = new TreeBuilder(pipeline, rootElement).generateTree(true);
		
		pipelineModificationMessage = new PipelineModificationMessage();
	}
	
	/**
	 * 
	 * @return
	 */
	public PipelineValidationHandler validateConnection() throws NoValidConnectionException
	{
		// determines if root element and current ancestor can be matched
		boolean match = false;
		
		// current root element can be either an action or a SEPA
		NamedSEPAElement rightElement = ClientModelUtils.transform(rootElement);
		
		// root element is an action
		if (rightElement instanceof SEC)
		{
			SEC sec = (SEC) rightElement;
			//match = ConnectionValidator.validateGrounding(left, sec.get);
		}
		
		// root element is SEPA
		if (rightElement instanceof SEPA)
		{
			SEPA sepa = (SEPA) rightElement;
			System.out.println("SEPA: " +sepa.getDescription());
			 
			if (rootElement.getConnectedTo().size() == 1)
			{
				List<EventSchema> left;
				System.out.println("Element: " +rootElement.getConnectedTo().get(0));
				System.out.println(pipeline.getSepas().size());
				System.out.println(pipeline.getStreams().size());
				SEPAElement element = TreeUtils.findSEPAElement(rootElement.getConnectedTo().get(0), pipeline.getSepas(), pipeline.getStreams());
				if (element instanceof StreamClient)
				{
					left = Utils.createList(((EventStream) ClientModelUtils.transform(element)).getEventSchema());
				}
				else
				{
					invocationGraphs = new InvocationGraphBuilder(new TreeBuilder(pipeline, element).generateTree(true), true).buildGraph();
					SEPAInvocationGraph ancestor = TreeUtils.findByDomId(element.getDOM(), invocationGraphs);
					left = Utils.createList(ancestor.getOutputStream().getEventSchema());
					
				}
				match = ConnectionValidator.validateSchema(left, Utils.createList(sepa.getEventStreams().get(0).getEventSchema()));
			}
			else if (rootElement.getConnectedTo().size() == 2)
			{
				List<EventSchema> firstLeft;
				List<EventSchema> secondLeft;
				
				
				
				//match = ConnectionValidator.validateSchema(firstLeft, secondLeft, Utils)
			}
		}
		if (!match) throw new NoValidConnectionException();
		
		return this;
	}
	
	public PipelineValidationHandler validateProperties()
	{
		
		
		return this;
	}
	
	/**
	 * dummy method to compute mapping properties (based on EXACT input/output matching)
	 * @return PipelineValidationHandler
	 */
	
	public PipelineValidationHandler computeMappingProperties()
	{
		List<String> connectedTo = rootElement.getConnectedTo();
		String domId = rootElement.getDOM();
		List<de.fzi.cep.sepa.model.client.StaticProperty> currentStaticProperties = rootElement.getStaticProperties();
		List<de.fzi.cep.sepa.model.client.StaticProperty> newStaticProperties = new ArrayList<>();
		
		if (connectedTo.size() == 1)
		{
			SEPAElement element = TreeUtils.findSEPAElement(rootElement.getConnectedTo().get(0), pipeline.getSepas(), pipeline.getStreams());
			
			//TODO: eliminate duplicates
			if (element instanceof SEPAClient)
			{
				SEPAInvocationGraph ancestor = TreeUtils.findByDomId(connectedTo.get(0), invocationGraphs);
				
				for(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty : currentStaticProperties)
				{
					if (clientStaticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY)
					{
						MappingProperty mp = TreeUtils.findMappingProperty(clientStaticProperty.getElementId(), ancestor);
						EventProperty eventProperty = TreeUtils.findEventProperty(mp.getMapsFrom().toString(), Utils.createList(ancestor.getOutputStream()));
						List<Option> options = new ArrayList<>();
						
						//TODO semantics
						for(URI subclass : eventProperty.getSubClassOf())
						{
							for(EventProperty streamProperty : ancestor.getOutputStream().getEventSchema().getEventProperties())
							{
								for(URI streamSubclassOf : streamProperty.getSubClassOf())
								{
									if (subclass.toString().equals(streamSubclassOf.toString()))
									{
										options.add(new Option(streamProperty.getRdfId().toString(), streamProperty.getPropertyName()));
									}
								}
							}
						}
						
						de.fzi.cep.sepa.model.client.StaticProperty newProperty = new de.fzi.cep.sepa.model.client.StaticProperty();
						newProperty.setName(clientStaticProperty.getName());
						newProperty.setDOM(clientStaticProperty.getDOM());
						newProperty.setElementId(clientStaticProperty.getElementId());
						newProperty.setInput(new SelectFormInput(options));
						newStaticProperties.add(newProperty);
						
					}
					else newStaticProperties.add(clientStaticProperty);
				}
				
			}
			else if (element instanceof StreamClient)
			{
				SEPA sepa = (SEPA) sepaRootElement;
				EventStream stream = (EventStream) ClientModelUtils.transform(element);
					
				for(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty : currentStaticProperties)
				{
					if (clientStaticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY)
					{
						MappingProperty mp = TreeUtils.findMappingProperty(clientStaticProperty.getElementId(), sepa);
						EventProperty eventProperty = TreeUtils.findEventProperty(mp.getMapsFrom().toString(), sepa.getEventStreams());
						List<Option> options = new ArrayList<>();
						
						for(URI subclass : eventProperty.getSubClassOf())
						{
							for(EventProperty streamProperty : stream.getEventSchema().getEventProperties())
							{
								for(URI streamSubclassOf : streamProperty.getSubClassOf())
								{
									if (subclass.toString().equals(streamSubclassOf.toString()))
									{
										options.add(new Option(streamProperty.getRdfId().toString(), streamProperty.getPropertyName()));
									}
								}
							}
						}
						
						de.fzi.cep.sepa.model.client.StaticProperty newProperty = new de.fzi.cep.sepa.model.client.StaticProperty();
						newProperty.setName(clientStaticProperty.getName());
						newProperty.setDOM(clientStaticProperty.getDOM());
						newProperty.setElementId(clientStaticProperty.getElementId());
						newProperty.setInput(new SelectFormInput(options));
						newStaticProperties.add(newProperty);
						
					}
					else newStaticProperties.add(clientStaticProperty);
				}
										
				PipelineModification modification = new PipelineModification(domId, rootElement.getElementId(), newStaticProperties);
				pipelineModificationMessage.addPipelineModification(modification);
			}
		}
		
		return this;
	}
	
	
	public PipelineValidationHandler computeMatchingProperties()
	{
		
		return this;
	}
	
	public PipelineModificationMessage getPipelineModificationMessage()
	{
		return pipelineModificationMessage;
	}
	
	
	
}
