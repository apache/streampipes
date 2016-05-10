package de.fzi.cep.sepa.manager.matching;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingFormatException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingProtocolException;
import de.fzi.cep.sepa.commons.exceptions.NoMatchingSchemaException;
import de.fzi.cep.sepa.manager.matching.validator.ConnectionValidator;
import de.fzi.cep.sepa.manager.util.ClientModelUtils;
import de.fzi.cep.sepa.manager.util.TreeUtils;
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
import de.fzi.cep.sepa.model.client.connection.Connection;
import de.fzi.cep.sepa.model.client.input.CheckboxInput;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.RadioGroupInput;
import de.fzi.cep.sepa.model.client.input.ReplaceOutputInput;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.client.input.SelectInput;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ReplaceOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.UriPropertyMapping;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MatchingStaticProperty;
import de.fzi.cep.sepa.storage.controller.StorageManager;


public class PipelineValidationHandler {

	Pipeline pipeline;
	PipelineModificationMessage pipelineModificationMessage;

	List<InvocableSEPAElement> invocationGraphs;
	List<SEPAElement> sepaClientElements;

	ConsumableSEPAElement clientRootElement;
	NamedSEPAElement rdfRootElement;

	GenericTree<SEPAElement> clientTree;
	GenericTree<NamedSEPAElement> rdfTree;

	public PipelineValidationHandler(Pipeline pipeline, boolean isPartial)
			throws Exception {
		
		this.pipeline = pipeline;
		this.clientRootElement = ClientModelUtils.getRootNode(pipeline);
		this.rdfRootElement = ClientModelUtils.transform(clientRootElement);
		this.invocationGraphs = new ArrayList<>();

		// prepare a list of all pipeline elements without the root element
		List<SEPAElement> sepaElements = new ArrayList<SEPAElement>();
		sepaElements.addAll(pipeline.getSepas());
		sepaElements.addAll(pipeline.getStreams());
		sepaElements.add(pipeline.getAction());
		sepaElements.remove(clientRootElement);

		// we need a tree of invocation graphs if there is more than one SEPA
		clientTree = new TreeBuilder(pipeline, clientRootElement).generateClientTree();
		if (clientTree.maxDepth(clientTree.getRoot()) > 2)
			rdfTree = new TreeBuilder(pipeline, clientRootElement).generateTree(true);

		pipelineModificationMessage = new PipelineModificationMessage();
	}

	/**
	 * 
	 * @return
	 * @throws NoMatchingFormatException 
	 * @throws NoMatchingSchemaException 
	 * @throws NoMatchingProtocolException 
	 */
	public PipelineValidationHandler validateConnection()
			throws NoMatchingFormatException, NoMatchingSchemaException, NoMatchingProtocolException {
		// determines if root element and current ancestor can be matched
		boolean schemaMatch = false;
		boolean formatMatch = false;
		boolean protocolMatch = false;

		// current root element can be either an action or a SEPA
		de.fzi.cep.sepa.model.ConsumableSEPAElement rightElement = ClientModelUtils.transformConsumable(clientRootElement);
		List<String> connectedTo = clientRootElement.getConnectedTo();
		
		if (connectedTo.size() == 1) {
			EventStream rightEventStream = rightElement.getEventStreams().get(0);
			EventSchema rightEventSchema = rightEventStream.getEventSchema();
			EventGrounding rightEventGrounding = rightElement.getSupportedGrounding();
			
			List<EventSchema> leftEventSchema;
			EventGrounding leftEventGrounding;
			
			SEPAElement leftRootElement = TreeUtils.findSEPAElement(connectedTo.get(0), pipeline.getSepas(), pipeline
					.getStreams());
			if (leftRootElement instanceof StreamClient) {
				EventStream leftEventStream = ClientModelUtils.transformStream(leftRootElement);
				leftEventSchema = asList(leftEventStream.getEventSchema());
				leftEventGrounding = leftEventStream.getEventGrounding();
			} else {
				invocationGraphs = makeInvocationGraphs(leftRootElement);
				SepaInvocation ancestor = findInvocationGraph(invocationGraphs, leftRootElement.getDOM());
				EventStream ancestorOutputStream = ancestor.getOutputStream();
				leftEventSchema = asList(ancestorOutputStream.getEventSchema());
				leftEventGrounding = ancestor.getSupportedGrounding();
				
			}
			schemaMatch = ConnectionValidator.validateSchema(
					leftEventSchema,
					asList(rightEventSchema));
			
			formatMatch = ConnectionValidator.validateTransportFormat(leftEventGrounding, rightEventGrounding);
			protocolMatch = ConnectionValidator.validateTransportProtocol(new EventGrounding(leftEventGrounding), new EventGrounding(rightEventGrounding));
		} else if (connectedTo.size() == 2) {
			
			SepaDescription sepa = (SepaDescription) rightElement;
			Iterator<String> it = connectedTo.iterator();
			List<EventStream> incomingStreams = new ArrayList<>();
			while(it.hasNext())
			{
				String domId = it.next();
				SEPAElement element = TreeUtils.findSEPAElement(domId, pipeline.getSepas(), pipeline.getStreams());
				if (element instanceof StreamClient) incomingStreams.add((EventStream) ClientModelUtils.transform(element));
				else
				{
					invocationGraphs.addAll(makeInvocationGraphs(element));
					SepaInvocation ancestor = findInvocationGraph(invocationGraphs, element.getDOM());
					EventStream incomingStream = ancestor.getOutputStream();
					incomingStream.setEventGrounding(ancestor.getSupportedGrounding());
					incomingStreams.add(incomingStream);
				}			
			}
		
			schemaMatch = ConnectionValidator.validateSchema(
					asList(incomingStreams.get(0).getEventSchema()),
					asList(incomingStreams.get(1).getEventSchema()),
					asList(sepa.getEventStreams().get(0)
							.getEventSchema()),
					asList(sepa.getEventStreams().get(1)
							.getEventSchema()));
			
			formatMatch = ConnectionValidator.validateTransportFormat(incomingStreams.get(0).getEventGrounding(), incomingStreams.get(1).getEventGrounding(), sepa.getSupportedGrounding());
			protocolMatch = ConnectionValidator.validateTransportProtocol(incomingStreams.get(0).getEventGrounding(), incomingStreams.get(1).getEventGrounding(), sepa.getSupportedGrounding());
			
		}
		if (!schemaMatch)
			throw new NoMatchingSchemaException();
		if (!formatMatch)
			throw new NoMatchingFormatException();
		if (!protocolMatch)
			throw new NoMatchingProtocolException();

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
		List<String> connectedTo = clientRootElement.getConnectedTo();
		String domId = clientRootElement.getDOM();
		
		List<de.fzi.cep.sepa.model.client.StaticProperty> currentStaticProperties = clientRootElement
				.getStaticProperties();
		
		currentStaticProperties = clearOptions(currentStaticProperties);
				
			for(int i = 0; i < connectedTo.size(); i++)
			{
				SEPAElement element = TreeUtils.findSEPAElement(clientRootElement
						.getConnectedTo().get(i), pipeline.getSepas(), pipeline
						.getStreams());
	
				if (element instanceof SEPAClient || element instanceof StreamClient || element instanceof ActionClient)
				{
					de.fzi.cep.sepa.model.ConsumableSEPAElement currentSEPA = (de.fzi.cep.sepa.model.ConsumableSEPAElement) rdfRootElement;
					
					if (element instanceof SEPAClient) {
						
						SepaInvocation ancestor = (SepaInvocation) TreeUtils.findByDomId(
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
								domId, clientRootElement.getElementId(), currentStaticProperties);
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
	
	public PipelineValidationHandler storeConnection() {
		String fromId = clientRootElement.getConnectedTo().get(clientRootElement.getConnectedTo().size() -1);
		SEPAElement sepaElement = TreeUtils.findSEPAElement(fromId, pipeline.getSepas(), pipeline.getStreams());
		
		Connection connection = new Connection(sepaElement.getElementId(), clientRootElement.getElementId());
		StorageManager.INSTANCE.getConnectionStorageApi().addConnection(connection);
		return this;
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
			if (clientStaticProperty.getType() == StaticPropertyType.MATCHING_PROPERTY) {
				List<Option> options;
				if (i == 0) options = ((RadioGroupInput) clientStaticProperty.getInput()).getOptionLeft();
				else options = ((RadioGroupInput) clientStaticProperty.getInput()).getOptionRight();
				
				if (options.size() > 0)	options.addAll(updateMatchingOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i));
				else options = updateMatchingOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i);
				newStaticProperties.add(updateStaticProperty(clientStaticProperty, options, i == 0));
			}
			else if (clientStaticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY) {
			
				List<Option> options = ((SelectInput) clientStaticProperty.getInput()).getOptions();
			
				if (options.size() > 0)	options.addAll(updateMappingOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i));
				else options = updateMappingOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i);
				newStaticProperties.add(updateStaticProperty(clientStaticProperty, options, true));

			} else if (clientStaticProperty.getType() == StaticPropertyType.CUSTOM_OUTPUT)
			{
				SepaDescription convertedSepaElement = (SepaDescription) currentSEPA;
				if (convertedSepaElement.getOutputStrategies().get(0) instanceof CustomOutputStrategy)
				{
					CustomOutputStrategy customOutput = (CustomOutputStrategy) convertedSepaElement.getOutputStrategies().get(0);
					List<Option> options = ((CheckboxInput) clientStaticProperty.getInput()).getOptions();
					if (options.size() == 0) options = convertCustomOutput(ancestorOutputStream.getEventSchema().getEventProperties(), new ArrayList<Option>());
					if (i > 0 && customOutput.isOutputRight())  options.addAll(convertCustomOutput(ancestorOutputStream.getEventSchema().getEventProperties(), new ArrayList<Option>()));
					newStaticProperties.add(updateStaticProperty(clientStaticProperty, options, true));
				}
			} else if (clientStaticProperty.getType() == StaticPropertyType.REPLACE_OUTPUT) {
				SepaDescription convertedSepaElement = (SepaDescription) currentSEPA;
				if (convertedSepaElement.getOutputStrategies().get(0) instanceof ReplaceOutputStrategy)
				{
					ReplaceOutputStrategy replaceOutput = (ReplaceOutputStrategy) convertedSepaElement.getOutputStrategies().get(0);
					ReplaceOutputInput input = ((ReplaceOutputInput) clientStaticProperty.getInput());
					for(int j = 0; j < replaceOutput.getReplaceProperties().size(); j++) {
						UriPropertyMapping upm = replaceOutput.getReplaceProperties().get(j);
						List<Option> options = updateOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i, upm.getReplaceFrom());
						input.getPropertyMapping().get(j).setInput(new SelectFormInput(options));
					}
					de.fzi.cep.sepa.model.client.StaticProperty newProperty = new de.fzi.cep.sepa.model.client.StaticProperty();
					newProperty.setName(clientStaticProperty.getName());
					newProperty.setDescription(clientStaticProperty
							.getDescription());
					newProperty.setDOM(clientStaticProperty.getDOM());
					newProperty.setElementId(clientStaticProperty
							.getElementId());
					newProperty.setType(clientStaticProperty.getType());
					newProperty.setInput(input);
					newStaticProperties.add(newProperty);	
					
				}
				
			} else
				newStaticProperties.add(clientStaticProperty);
		} 
		
		return newStaticProperties;
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

	private de.fzi.cep.sepa.model.client.StaticProperty updateStaticProperty(de.fzi.cep.sepa.model.client.StaticProperty currentStaticProperty, List<Option> newOption, boolean firstStream)
	{
		de.fzi.cep.sepa.model.client.StaticProperty newProperty = new de.fzi.cep.sepa.model.client.StaticProperty();
		newProperty.setName(currentStaticProperty.getName());
		newProperty.setDescription(currentStaticProperty
				.getDescription());
		newProperty.setDOM(currentStaticProperty.getDOM());
		newProperty.setElementId(currentStaticProperty
				.getElementId());
		
		if (currentStaticProperty.getInput() instanceof SelectFormInput) newProperty.setInput(new SelectFormInput(newOption));
		else if (currentStaticProperty.getInput() instanceof RadioGroupInput) {
			RadioGroupInput input = (RadioGroupInput) currentStaticProperty.getInput();
			if (firstStream) input.setOptionLeft(newOption);
			else input.setOptionRight(newOption);
			newProperty.setInput(input);
		}
		else newProperty.setInput(new CheckboxInput(newOption));
		newProperty.setType(currentStaticProperty.getType());
		return newProperty;
	}
	
	private List<Option> updateMatchingOptions(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty, de.fzi.cep.sepa.model.ConsumableSEPAElement sepa, EventStream stream, int i) {
		MatchingStaticProperty mp = TreeUtils.findMatchingProperty(clientStaticProperty.getElementId(), sepa);
		
		URI maps;
		if (i == 0) maps = mp.getMatchLeft();
		else maps = mp.getMatchRight();
		
		return updateOptions(clientStaticProperty, sepa, stream, i, maps);
	}
	
	private List<Option> updateMappingOptions (de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty, de.fzi.cep.sepa.model.ConsumableSEPAElement sepa, EventStream leftStream, int i) {
		MappingProperty mp = TreeUtils.findMappingProperty(
				clientStaticProperty.getElementId(), sepa);
		
		return updateOptions(clientStaticProperty, sepa, leftStream, i, mp.getMapsFrom());
	}
	
	private List<Option> updateOptions(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty, de.fzi.cep.sepa.model.ConsumableSEPAElement sepa, EventStream leftStream, int i, URI maps) {
		
		List<Option> options = new ArrayList<>();

		if (maps != null) {
			EventProperty rightProperty = TreeUtils
					.findEventProperty(maps
							.toString(), sepa.getEventStreams());

			
			if (sepa.getEventStreams().get(i).getEventSchema().getEventProperties().contains(rightProperty))
			{
				
				List<EventProperty> leftMatchingProperties = new Matcher().matchesProperties(
						rightProperty, leftStream.getEventSchema()
								.getEventProperties());
	
				for (EventProperty matchedStreamProperty : leftMatchingProperties) {
					options.add(new Option(matchedStreamProperty
							.getElementId().toString(),
							matchedStreamProperty.getRuntimeName()));
				}
			}
			else
			{
				List<EventProperty> leftMatchingProperties = new Matcher().matchesPropertiesList(
						rightProperty, leftStream.getEventSchema()
								.getEventProperties());
	
				for (EventProperty matchedStreamProperty : leftMatchingProperties) {
					options.add(new Option(matchedStreamProperty
							.getElementId().toString(),
							matchedStreamProperty.getRuntimeName()));
				}
			}
			
		} else {
			for (EventProperty streamProperty : leftStream
					.getEventSchema().getEventProperties()) {
				if ((streamProperty instanceof EventPropertyPrimitive) || streamProperty instanceof EventPropertyList)
				{
					options.add(new Option(streamProperty
							.getRdfId().toString(), streamProperty
							.getRuntimeName()));
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
			if (p instanceof EventPropertyPrimitive) options.add(new Option(p.getRdfId().toString(), p.getRuntimeName()));
			else options.addAll(addNestedOptions(properties));
		}
		return options;
	}
	
	private List<InvocableSEPAElement> makeInvocationGraphs(SEPAElement rootElement)
	{
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(
				pipeline, rootElement).generateTree(true);
		return new InvocationGraphBuilder(tree, true, null).buildGraph();
	}
	
	private SepaInvocation findInvocationGraph(List<InvocableSEPAElement> graphs, String domId)
	{
		return (SepaInvocation) TreeUtils.findByDomId(domId, invocationGraphs);
	}
	
	private <T> List<T> asList(T object)
	{
		return Utils.createList(object);
	}
	
	private List<Option> convertCustomOutput(List<EventProperty> eventProperties, List<Option> options) {
		
		for(EventProperty p : eventProperties)
		{
			if (p instanceof EventPropertyPrimitive || p instanceof EventPropertyList) {
				String runtimeName = p.getRuntimeName();
				if (p instanceof EventPropertyPrimitive) options.add(new Option(p.getElementId(), runtimeName));
				else if (p instanceof EventPropertyList) options.add(new Option(p.getElementId(), runtimeName));
			}
			else if (p instanceof EventPropertyNested) 
				{
					options.add(new Option(p.getElementId(), p.getRuntimeName()));
					options.addAll(convertCustomOutput(((EventPropertyNested) p).getEventProperties(), new ArrayList<Option>()));
				}
		}
		return options;
	}

}
