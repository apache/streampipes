package de.fzi.cep.sepa.manager.pipeline;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.exceptions.NoValidConnectionException;
import de.fzi.cep.sepa.manager.util.ClientModelUtils;
import de.fzi.cep.sepa.manager.util.TreeUtils;
import de.fzi.cep.sepa.manager.validator.ConnectionValidator;
import de.fzi.cep.sepa.messages.PipelineModification;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticProperty;
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

	public PipelineValidationHandler(Pipeline pipeline, boolean isPartial)
			throws Exception {
		this.pipeline = pipeline;
		this.rootElement = ClientModelUtils.getRootNode(pipeline);
		this.sepaRootElement = ClientModelUtils.transform(rootElement);
		this.invocationGraphs = new ArrayList<>();

		// prepare a list of all pipeline elements without the root element
		List<SEPAElement> sepaElements = new ArrayList<SEPAElement>();
		sepaElements.addAll(pipeline.getSepas());
		sepaElements.addAll(pipeline.getStreams());
		sepaElements.remove(rootElement);

		// we need a tree of invocation graphs if there is more than one SEPA
		clientTree = new TreeBuilder(pipeline, rootElement)
				.generateClientTree();
		if (clientTree.maxDepth(clientTree.getRoot()) > 2)
			modelTree = new TreeBuilder(pipeline, rootElement)
					.generateTree(true);

		pipelineModificationMessage = new PipelineModificationMessage();
	}

	/**
	 * 
	 * @return
	 */
	public PipelineValidationHandler validateConnection()
			throws NoValidConnectionException {
		// determines if root element and current ancestor can be matched
		boolean match = false;

		// current root element can be either an action or a SEPA
		NamedSEPAElement rightElement = ClientModelUtils.transform(rootElement);

		// root element is an action
		if (rightElement instanceof SEC) {
			SEC sec = (SEC) rightElement;
			// match = ConnectionValidator.validateGrounding(left, sec.get);
		}

		// root element is SEPA
		if (rightElement instanceof SEPA) {
			SEPA sepa = (SEPA) rightElement;

			if (rootElement.getConnectedTo().size() == 1) {
				List<EventSchema> left;
				SEPAElement element = TreeUtils.findSEPAElement(rootElement
						.getConnectedTo().get(0), pipeline.getSepas(), pipeline
						.getStreams());
				if (element instanceof StreamClient) {
					left = Utils.createList(((EventStream) ClientModelUtils
							.transform(element)).getEventSchema());
				} else {
					GenericTree<NamedSEPAElement> tree = new TreeBuilder(
							pipeline, element).generateTree(true);
					invocationGraphs = new InvocationGraphBuilder(tree, true)
							.buildGraph();

					SEPAInvocationGraph ancestor = TreeUtils.findByDomId(
							element.getDOM(), invocationGraphs);
					left = Utils.createList(ancestor.getOutputStream()
							.getEventSchema());
					// System.out.println(ancestor.getOutputStream().getEventSchema().getEventProperties().size());
				}
				match = ConnectionValidator.validateSchema(
						left,
						Utils.createList(sepa.getEventStreams().get(0)
								.getEventSchema()));
				System.out.println(match);
			} else if (rootElement.getConnectedTo().size() == 2) {
				
				Iterator<String> it = rootElement.getConnectedTo().iterator();
				List<EventStream> incomingStreams = new ArrayList<>();
				while(it.hasNext())
				{
					String domId = it.next();
					SEPAElement element = TreeUtils.findSEPAElement(domId, pipeline.getSepas(), pipeline.getStreams());
					if (element instanceof StreamClient) incomingStreams.add((EventStream) ClientModelUtils.transform(element));
					else
					{
						GenericTree<NamedSEPAElement> tree = new TreeBuilder(
								pipeline, element).generateTree(true);
						invocationGraphs.addAll(new InvocationGraphBuilder(tree, true)
								.buildGraph());

						SEPAInvocationGraph ancestor = TreeUtils.findByDomId(
								element.getDOM(), invocationGraphs);
						incomingStreams.add(ancestor.getOutputStream());
					}
					
				}
			
				match = ConnectionValidator.validateSchema(
						Utils.createList(incomingStreams.get(0).getEventSchema()),
						Utils.createList(incomingStreams.get(1).getEventSchema()),
						Utils.createList(sepa.getEventStreams().get(0)
								.getEventSchema()),
						Utils.createList(sepa.getEventStreams().get(1)
								.getEventSchema()));
			}
		}
		if (!match)
			throw new NoValidConnectionException();

		return this;
	}

	/**
	 * dummy method to compute mapping properties (based on EXACT input/output
	 * matching)
	 * 
	 * @return PipelineValidationHandler
	 */

	public PipelineValidationHandler computeMappingProperties() {
		List<String> connectedTo = rootElement.getConnectedTo();
		String domId = rootElement.getDOM();
		
		List<de.fzi.cep.sepa.model.client.StaticProperty> currentStaticProperties = rootElement
				.getStaticProperties();
		
		currentStaticProperties = clearOptions(currentStaticProperties);
				
			for(int i = 0; i < connectedTo.size(); i++)
			{
				SEPAElement element = TreeUtils.findSEPAElement(rootElement
						.getConnectedTo().get(i), pipeline.getSepas(), pipeline
						.getStreams());
	
				if (element instanceof SEPAClient || element instanceof StreamClient)
				{
					SEPA currentSEPA = (SEPA) sepaRootElement;
					
					if (element instanceof SEPAClient) {
						
						SEPAInvocationGraph ancestor = TreeUtils.findByDomId(
								connectedTo.get(i), invocationGraphs);
					
						currentStaticProperties = updateStaticProperties(currentStaticProperties, currentSEPA, ancestor.getOutputStream(), i);		
					} else if (element instanceof StreamClient) {
						EventStream stream = (EventStream) ClientModelUtils
								.transform(element);
		
						currentStaticProperties = updateStaticProperties(currentStaticProperties, currentSEPA, stream, i);	
					}
					if ( currentSEPA.getEventStreams().size()-1 == i)
					{
						PipelineModification modification = new PipelineModification(
								domId, rootElement.getElementId(), currentStaticProperties);
						pipelineModificationMessage
								.addPipelineModification(modification);
					}
				}
			}

		return this;
	}

	private List<StaticProperty> clearOptions(
			List<StaticProperty> currentStaticProperties) {
		Iterator<StaticProperty> it = currentStaticProperties.iterator();
		while(it.hasNext())
		{
			StaticProperty p = (StaticProperty) it.next();
			if (p.getType() == StaticPropertyType.MAPPING_PROPERTY)
			{
				SelectFormInput input = (SelectFormInput) p.getInput();
				input.setOptions(new ArrayList<>());
			}
			
		}
		return currentStaticProperties;
	}

	public PipelineValidationHandler computeMatchingProperties() {

		return this;
	}

	public PipelineModificationMessage getPipelineModificationMessage() {
		return pipelineModificationMessage;
	}

	private List<EventProperty> matches(EventProperty right,
			List<EventProperty> left) {
		List<EventProperty> matchingProperties = new ArrayList<>();
		for (EventProperty l : left) {
			if (matches(right, l))
				matchingProperties.add(l);
		}
		return matchingProperties;
	}

	private boolean matches(EventProperty right, EventProperty left) {
		boolean match = true;
		List<URI> leftUris = left.getSubClassOf();
		for (URI uri : right.getSubClassOf()) {
			if (!leftUris.contains(uri))
				match = false;
		}
		return match;
	}
	private List<de.fzi.cep.sepa.model.client.StaticProperty> updateStaticProperties(List<de.fzi.cep.sepa.model.client.StaticProperty> currentStaticProperties, SEPA currentSEPA, EventStream ancestorOutputStream, int i)
	{
		List<de.fzi.cep.sepa.model.client.StaticProperty> newStaticProperties = new ArrayList<>();
		
		for (de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty : currentStaticProperties) {
			if (clientStaticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY) {
			
				List<Option> options = ((SelectFormInput) clientStaticProperty.getInput()).getOptions();
			
				if (options.size() > 0)	options.addAll(updateOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i));
				else options = updateOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i);
				newStaticProperties.add(updateStaticProperty(clientStaticProperty, options));

			} else
				newStaticProperties.add(clientStaticProperty);
		} 
		
		return newStaticProperties;
	}

	private de.fzi.cep.sepa.model.client.StaticProperty updateStaticProperty(de.fzi.cep.sepa.model.client.StaticProperty currentStaticProperty, List<Option> newOption)
	{
		de.fzi.cep.sepa.model.client.StaticProperty newProperty = new de.fzi.cep.sepa.model.client.StaticProperty();
		newProperty.setName(currentStaticProperty.getName());
		newProperty.setDescription(currentStaticProperty
				.getDescription());
		newProperty.setDOM(currentStaticProperty.getDOM());
		newProperty.setElementId(currentStaticProperty
				.getElementId());
		newProperty.setInput(new SelectFormInput(newOption));
		newProperty.setType(currentStaticProperty.getType());
		return newProperty;
	}
	
	private List<Option> updateOptions(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty, SEPA sepa, EventStream leftStream, int i) {
			
		MappingProperty mp = TreeUtils.findMappingProperty(
				clientStaticProperty.getElementId(), sepa);
		List<Option> options = new ArrayList<>();

		if (mp.getMapsFrom() != null) {
			EventProperty rightProperty = TreeUtils
					.findEventProperty(mp.getMapsFrom()
							.toString(), sepa.getEventStreams());
		
			if (sepa.getEventStreams().get(i).getEventSchema().getEventProperties().contains(rightProperty))
			{
				List<EventProperty> leftMatchingProperties = matches(
						rightProperty, leftStream.getEventSchema()
								.getEventProperties());
	
				for (EventProperty matchedStreamProperty : leftMatchingProperties) {
				
					options.add(new Option(matchedStreamProperty
							.getRdfId().toString(),
							matchedStreamProperty.getPropertyName()));
				}
			}
		} else {
			for (EventProperty streamProperty : leftStream
					.getEventSchema().getEventProperties()) {
				options.add(new Option(streamProperty
						.getRdfId().toString(), streamProperty
						.getPropertyName()));
			}
		}
		return options;
	}

}
