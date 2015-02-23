package de.fzi.cep.sepa.manager.pipeline;

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
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.client.StaticPropertyType;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.CheckboxInput;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.client.input.SelectInput;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyList;
import de.fzi.cep.sepa.model.impl.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;

public class PipelineValidationHandler {

	Pipeline pipeline;
	PipelineModificationMessage pipelineModificationMessage;

	List<InvocableSEPAElement> invocationGraphs;
	List<SEPAElement> sepaClientElements;

	ConsumableSEPAElement rootElement;
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
		sepaElements.add(pipeline.getAction());
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

		
		// root element is SEPA
		if (! (rightElement instanceof SEP)) {
			
			EventSchema rightEventSchema;
			
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

					SEPAInvocationGraph ancestor = (SEPAInvocationGraph) TreeUtils.findByDomId(
							element.getDOM(), invocationGraphs);
					left = Utils.createList(ancestor.getOutputStream()
							.getEventSchema());
					// System.out.println(ancestor.getOutputStream().getEventSchema().getEventProperties().size());
				}
				
				if (rightElement instanceof SEPA)
				{
					rightEventSchema = ((SEPA) rightElement).getEventStreams().get(0).getEventSchema();
				}
				else rightEventSchema = ((SEC) rightElement).getEventStreams().get(0).getEventSchema();
				
				match = ConnectionValidator.validateSchema(
						left,
						Utils.createList(rightEventSchema));
				System.out.println(match);
			} else if (rootElement.getConnectedTo().size() == 2) {
				
				SEPA sepa = (SEPA) rightElement;
				
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

						SEPAInvocationGraph ancestor = (SEPAInvocationGraph) TreeUtils.findByDomId(
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
		try {
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
	
				if (element instanceof SEPAClient || element instanceof StreamClient || element instanceof ActionClient)
				{
					de.fzi.cep.sepa.model.ConsumableSEPAElement currentSEPA = (de.fzi.cep.sepa.model.ConsumableSEPAElement) sepaRootElement;
					
					if (element instanceof SEPAClient) {
						
						SEPAInvocationGraph ancestor = (SEPAInvocationGraph) TreeUtils.findByDomId(
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
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}
	
	private static List<Option> convertCustomOutput(List<EventProperty> eventProperties, List<Option> options) {
		
		for(EventProperty p : eventProperties)
		{
			if (p instanceof EventPropertyPrimitive) options.add(new Option(p.getRdfId().toString(), p.getPropertyName()));
			else if (p instanceof EventPropertyList) options.add(new Option(p.getRdfId().toString(), p.getPropertyName()));
			else if (p instanceof EventPropertyNested) 
				{
					options.add(new Option(p.getRdfId().toString(), p.getPropertyName()));
					options.addAll(convertCustomOutput(((EventPropertyNested) p).getEventProperties(), new ArrayList<Option>()));
				}
		}
		return options;
	}
	

	private List<StaticProperty> clearOptions(
			List<StaticProperty> currentStaticProperties) {
		Iterator<StaticProperty> it = currentStaticProperties.iterator();
		while(it.hasNext())
		{
			StaticProperty p = (StaticProperty) it.next();
			if (p.getType() == StaticPropertyType.MAPPING_PROPERTY)
			{
				SelectInput input = (SelectInput) p.getInput();
				input.setOptions(new ArrayList<>());
			}
			else if ((p.getType() == StaticPropertyType.CUSTOM_OUTPUT))
			{
				CheckboxInput input = (CheckboxInput) p.getInput();
				input.setOptions(new ArrayList<Option>());
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

	
	private List<de.fzi.cep.sepa.model.client.StaticProperty> updateStaticProperties(List<de.fzi.cep.sepa.model.client.StaticProperty> currentStaticProperties, de.fzi.cep.sepa.model.ConsumableSEPAElement currentSEPA, EventStream ancestorOutputStream, int i)
	{
		List<de.fzi.cep.sepa.model.client.StaticProperty> newStaticProperties = new ArrayList<>();
		
		for (de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty : currentStaticProperties) {
			if (clientStaticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY) {
			
				List<Option> options = ((SelectInput) clientStaticProperty.getInput()).getOptions();
			
				if (options.size() > 0)	options.addAll(updateOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i));
				else options = updateOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i);
				newStaticProperties.add(updateStaticProperty(clientStaticProperty, options));

			} else if (clientStaticProperty.getType() == StaticPropertyType.CUSTOM_OUTPUT)
			{
				SEPA convertedSepaElement = (SEPA) currentSEPA;
				if (convertedSepaElement.getOutputStrategies().get(0) instanceof CustomOutputStrategy)
				{
					List<Option> options = ((CheckboxInput) clientStaticProperty.getInput()).getOptions();
					if (options.size() == 0) options = convertCustomOutput(ancestorOutputStream.getEventSchema().getEventProperties(), new ArrayList<Option>());
					newStaticProperties.add(updateStaticProperty(clientStaticProperty, options));
				}
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
		if (currentStaticProperty.getInput() instanceof SelectFormInput) newProperty.setInput(new SelectFormInput(newOption));
		else newProperty.setInput(new CheckboxInput(newOption));
		newProperty.setType(currentStaticProperty.getType());
		return newProperty;
	}
	
	private List<Option> updateOptions(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty, de.fzi.cep.sepa.model.ConsumableSEPAElement sepa, EventStream leftStream, int i) {
			
		MappingProperty mp = TreeUtils.findMappingProperty(
				clientStaticProperty.getElementId(), sepa);
		List<Option> options = new ArrayList<>();

		if (mp.getMapsFrom() != null) {
			EventProperty rightProperty = TreeUtils
					.findEventProperty(mp.getMapsFrom()
							.toString(), sepa.getEventStreams());
		
			if (sepa.getEventStreams().get(i).getEventSchema().getEventProperties().contains(rightProperty))
			{
				List<EventProperty> leftMatchingProperties = new Matcher().matchesProperties(
						rightProperty, leftStream.getEventSchema()
								.getEventProperties());
	
				for (EventProperty matchedStreamProperty : leftMatchingProperties) {
					System.out.println(matchedStreamProperty.getRdfId().toString());
					options.add(new Option(matchedStreamProperty
							.getRdfId().toString(),
							matchedStreamProperty.getPropertyName()));
				}
			}
		} else {
			for (EventProperty streamProperty : leftStream
					.getEventSchema().getEventProperties()) {
				if ((streamProperty instanceof EventPropertyPrimitive) || streamProperty instanceof EventPropertyList)
				{
					options.add(new Option(streamProperty
							.getRdfId().toString(), streamProperty
							.getPropertyName()));
				} else {
					options.addAll(addNestedOptions((EventPropertyNested) streamProperty));
				}
			}
		}
		return options;
	}
	
	private List<Option> addNestedOptions(EventPropertyNested properties)
	{
		List<Option> options = new ArrayList<>();
		for(EventProperty p : properties.getEventProperties())
		{
			if (p instanceof EventPropertyPrimitive) options.add(new Option(p.getRdfId().toString(), p.getPropertyName()));
			else options.addAll(addNestedOptions(properties));
		}
		return options;
	}

}
