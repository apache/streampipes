package de.fzi.cep.sepa.storage.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.RandomStringUtils;

import com.clarkparsia.empire.SupportsRdfId.URIKey;

import de.fzi.cep.sepa.model.client.ActionClient;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SourceClient;
import de.fzi.cep.sepa.model.client.StaticPropertyType;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.CheckboxInput;
import de.fzi.cep.sepa.model.client.input.DomainConceptInput;
import de.fzi.cep.sepa.model.client.input.FormInput;
import de.fzi.cep.sepa.model.client.input.MultipleValueInput;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.PropertyMapping;
import de.fzi.cep.sepa.model.client.input.RadioGroupInput;
import de.fzi.cep.sepa.model.client.input.RadioInput;
import de.fzi.cep.sepa.model.client.input.ReplaceOutputInput;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.client.input.SelectInput;
import de.fzi.cep.sepa.model.client.input.SliderInput;
import de.fzi.cep.sepa.model.client.input.SupportedProperty;
import de.fzi.cep.sepa.model.client.input.TextInput;
import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ReplaceOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.UriPropertyMapping;
import de.fzi.cep.sepa.model.impl.staticproperty.AnyStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.MatchingStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class ClientModelTransformer {
	
	/**
	 * 
	 * @param sep
	 * @return
	 */
	
	public static List<StreamClient> toStreamClientModel(SepDescription sep)
	{
		List<StreamClient> result = new ArrayList<StreamClient>();
		sep.getEventStreams().forEach((stream) -> result.add(toStreamClientModel(sep, stream)));
		return result;
	}

	public static List<StreamClient> toStreamClientModel(List<SepDescription> seps)
	{		
		List<StreamClient> result = new ArrayList<StreamClient>();
		seps.forEach((sep) -> result.addAll(toStreamClientModel(sep)));
		return result;
	}

	public static SepDescription fromSourceClientModel(SourceClient client)
	{
		return StorageManager.INSTANCE.getStorageAPI().getSEPById(URI.create(client.getElementId()));
	}

	public static SourceClient toSourceClientModel(SepDescription sep)
	{
		SourceClient client = new SourceClient(sep.getName(), sep.getDescription());
		client.setIconUrl(sep.getIconUrl());
		client.setElementId(sep.getRdfId().toString());
		return client;
	}

	public static List<SourceClient> toSourceClientModel(List<SepDescription> seps)
	{
		List<SourceClient> result = new ArrayList<SourceClient>();
		seps.forEach((sep) -> result.add(toSourceClientModel(sep)));
		return result;
			
	}

	public static EventStream fromStreamClientModel(StreamClient client)
	{
		return StorageManager.INSTANCE.getStorageAPI().getEventStreamById(client.getElementId());
	}

	public static StreamClient toStreamClientModel(SepDescription sep, EventStream stream)
	{
		StreamClient client = new StreamClient(stream.getName(), stream.getDescription(), sep.getRdfId().toString());
		client.setIconUrl(stream.getIconUrl());
		client.setElementId(stream.getRdfId().toString());
		return client;
	}

	public static SourceClient toSEPClientModel(SepDescription sep)
	{
		return null;
	}

	public static SEPAClient toSEPAClientModel(SepaDescription sepa)
	{
		List<String> categories = sepa.getEpaTypes().size() > 0 ? sepa.getEpaTypes() : Arrays.asList(EpaType.UNCATEGORIZED.name());
		SEPAClient client = new SEPAClient(sepa.getName(), sepa.getDescription(), categories);
		client.setInputNodes(sepa.getEventStreams().size());
		client.setElementId(sepa.getRdfId().toString());
		client.setIconUrl(sepa.getIconUrl());
		client.setInputNodes(sepa.getEventStreams().size());
		
		List<de.fzi.cep.sepa.model.client.StaticProperty> clientStaticProperties = new ArrayList<>();
		sepa.getStaticProperties().forEach((p) -> clientStaticProperties.add(convertStaticProperty(p)));		
		
		//TODO support multiple output strategies
		if (sepa.getOutputStrategies().get(0) instanceof CustomOutputStrategy)
		{
			CustomOutputStrategy strategy = (CustomOutputStrategy) sepa.getOutputStrategies().get(0);
			clientStaticProperties.add(convertCustomOutput(strategy));
		}
		if (sepa.getOutputStrategies().get(0) instanceof ReplaceOutputStrategy) {
			ReplaceOutputStrategy strategy = (ReplaceOutputStrategy) sepa.getOutputStrategies().get(0);
			clientStaticProperties.add(convertReplaceOutput(strategy));
		}
	
		client.setStaticProperties(clientStaticProperties);	
		
		return client;
	}
	
	private static de.fzi.cep.sepa.model.client.StaticProperty convertReplaceOutput(
			ReplaceOutputStrategy strategy) {
		
		ReplaceOutputInput replaceOutput = new ReplaceOutputInput();
		List<PropertyMapping> pm = new ArrayList<>();
		for(UriPropertyMapping m : strategy.getReplaceProperties()) {
			PropertyMapping mapping = new PropertyMapping(new SelectFormInput(), m.getReplaceWith().getRuntimeName(), null, null);
			mapping.setRenameAllowed(m.isRenamingAllowed());
			mapping.setTypeCastAllowed(m.isTypeCastAllowed());
			mapping.setDomainPropertyCastAllowed(m.isDomainPropertyCastAllowed());
			mapping.setElementId(m.getElementId());
			pm.add(mapping);
		}
		replaceOutput.setPropertyMapping(pm);
		de.fzi.cep.sepa.model.client.StaticProperty clientProperty = new de.fzi.cep.sepa.model.client.StaticProperty(
				StaticPropertyType.REPLACE_OUTPUT, "replace", "Replace Property", "", replaceOutput);
		clientProperty.setElementId(strategy.getRdfId().toString());
		
		return clientProperty;
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertCustomOutput(
			CustomOutputStrategy strategy) {
		
		CheckboxInput checkboxInput = new CheckboxInput();
		List<Option> options = new ArrayList<>();
		
		for(EventProperty p : strategy.getEventProperties())
			options.add(new Option(p.getRdfId().toString(), p.getRuntimeName()));
		checkboxInput.setOptions(options);
		
		de.fzi.cep.sepa.model.client.StaticProperty clientProperty = new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.CUSTOM_OUTPUT, "custom", "Output properties:", "Output: ", checkboxInput);
		clientProperty.setElementId(strategy.getRdfId().toString());
		return clientProperty;
	}


	public static SepaDescription fromSEPAClientModel(SEPAClient sepaClient)
	{
		SepaDescription sepa = StorageManager.INSTANCE.getStorageAPI().getSEPAById(URI.create(sepaClient.getElementId()));
			
		List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties = sepaClient.getStaticProperties();
		sepa.setStaticProperties(convertStaticProperties(sepa, clientProperties));
		
		if (sepa.getOutputStrategies().get(0) instanceof ReplaceOutputStrategy) {
			ReplaceOutputStrategy strategy = (ReplaceOutputStrategy) sepa.getOutputStrategies().get(0);
			ReplaceOutputInput input = ((ReplaceOutputInput) Utils.getClientPropertyById(sepaClient.getStaticProperties(), strategy.getElementId()).getInput());
			
			for(int i = 0; i < input.getPropertyMapping().size(); i++) {
				PropertyMapping pm = input.getPropertyMapping().get(i);
				UriPropertyMapping upm = strategy.getReplaceProperties().get(i);
				Optional<Option> selectedProperty = pm.getInput().getOptions().stream().filter(p -> p.isSelected()).findFirst();
				if (selectedProperty.isPresent()) {
					upm.setReplaceTo(URI.create(selectedProperty.get().getElementId()));
					if (upm.isRenamingAllowed()) 
						upm.getReplaceWith().setRuntimeName(pm.getRuntimeName());
				}
			}	
		}
		if (sepa.getOutputStrategies().get(0) instanceof CustomOutputStrategy)
		{
			List<EventProperty> outputProperties = new ArrayList<EventProperty>();
			List<EventProperty> processedProperties = new ArrayList<>();
			CustomOutputStrategy strategy = (CustomOutputStrategy) sepa.getOutputStrategies().get(0);
			String id = strategy.getRdfId().toString();
			CheckboxInput input = (CheckboxInput) Utils.getClientPropertyById(sepaClient.getStaticProperties(), id).getInput();
			for(Option option : input.getOptions())
			{
				if (option.isSelected()) 
					{
						EventProperty matchedProperty;
						matchedProperty = StorageManager.INSTANCE.getEntityManager().find(EventProperty.class, option.getElementId());
						//TODO not working for j > 1
						if (matchedProperty == null)
						{
							int j = Integer.parseInt(option.getHumanDescription().substring(option.getHumanDescription().length()-1, option.getHumanDescription().length()));
							if (option.getElementId().substring(option.getElementId().length()-1, option.getElementId().length()).equals(String.valueOf(j)))
							{
								String fixedId = option.getElementId().substring(0, option.getElementId().length()-1);
								EventProperty tempProperty = StorageManager.INSTANCE.getEntityManager().find(EventProperty.class, fixedId);
								if (tempProperty != null)
								{
									if (tempProperty instanceof EventPropertyPrimitive)
									{
										EventPropertyPrimitive newProperty = new EventPropertyPrimitive(((EventPropertyPrimitive) tempProperty).getRuntimeType(), option.getHumanDescription()+j, ((EventPropertyPrimitive) tempProperty).getMeasurementUnit(), tempProperty.getDomainProperties());
										newProperty.setRdfId(new URIKey(URI.create(tempProperty.getRdfId().toString() +j)));
										matchedProperty = newProperty;
									}
								}
							}
							j++;
						}
						if (!processedProperties.contains(matchedProperty)) 
							{
								matchedProperty.setRuntimeName(option.getHumanDescription());
								outputProperties.add(matchedProperty);
							}
						if (matchedProperty instanceof EventPropertyNested) processedProperties.addAll(((EventPropertyNested) matchedProperty).getEventProperties());
						
					}
			}
			((CustomOutputStrategy) sepa.getOutputStrategies().get(0)).setEventProperties(outputProperties);
		}
			
		return sepa;
	}
	
	private static List<StaticProperty> convertStaticProperties(SecDescription sec, List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties)
	{
		return convertStaticProperties(sec.getStaticProperties(), clientProperties);
	}
	
	private static List<StaticProperty> convertStaticProperties(SepaDescription sepa, List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties)
	{
		return convertStaticProperties(sepa.getStaticProperties(), clientProperties);
	}
	
	private static List<StaticProperty> convertStaticProperties(List<StaticProperty> serverStaticProperties, List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties)
	{
		List<StaticProperty> resultProperties = new ArrayList<StaticProperty>();
		for(StaticProperty p : serverStaticProperties)
		{
			String id = p.getRdfId().toString();
			FormInput formInput = Utils.getClientPropertyById(clientProperties, id).getInput();
			if (p instanceof FreeTextStaticProperty) 
				{
					resultProperties.add(convertFreeTextStaticProperty((FreeTextStaticProperty) p, formInput));
				}
			else if (p instanceof AnyStaticProperty)
			{
				CheckboxInput input = (CheckboxInput) formInput;
				resultProperties.add(convertAnyStaticProperty((AnyStaticProperty) p, input));
			} else if (p instanceof OneOfStaticProperty)
			{
				RadioInput input = (RadioInput) formInput;
				resultProperties.add(convertOneOfStaticProperty((OneOfStaticProperty) p, input));
			} else if (p instanceof MappingProperty)
			{
				if (p instanceof MappingPropertyUnary)
				{
					SelectFormInput input = (SelectFormInput) formInput;
					resultProperties.add(convertMappingPropertyUnary((MappingPropertyUnary) p, input));
				}
				else
				{
					CheckboxInput input = (CheckboxInput) formInput;
					resultProperties.add(convertMappingPropertyNary((MappingPropertyNary) p, input));
				}
			} else if (p instanceof MatchingStaticProperty)
			{
				MatchingStaticProperty matchingProperty = (MatchingStaticProperty) p;
				RadioGroupInput input = (RadioGroupInput) formInput;
				resultProperties.add(convertMatchingStaticProperty(matchingProperty, input));
			} else if (p instanceof DomainStaticProperty)
			{
				DomainStaticProperty domainStaticProperty = (DomainStaticProperty) p;
				DomainConceptInput input = (DomainConceptInput) formInput;
				resultProperties.add(convertDomainStaticProperty(domainStaticProperty, input));
			} else if (p instanceof CollectionStaticProperty) {
				CollectionStaticProperty collectionStaticProperty = (CollectionStaticProperty) p;
				MultipleValueInput multipleValueInput = (MultipleValueInput) formInput;
				resultProperties.add(convertCollectionStaticProperty(collectionStaticProperty, multipleValueInput));
			}
		}

		return resultProperties;
	}
	
	private static StaticProperty convertCollectionStaticProperty(
			CollectionStaticProperty collectionStaticProperty,
			MultipleValueInput multipleValueInput) {
		
		
		StaticProperty propertyDescription = collectionStaticProperty.getMembers().get(0);
		List<StaticProperty> members = new ArrayList<>();
		collectionStaticProperty.setMemberType(multipleValueInput.getMemberType());
		
		
		for(de.fzi.cep.sepa.model.client.StaticProperty clientInput : multipleValueInput.getMembers()) {
			if (multipleValueInput.getMemberType().equals("de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty")) 
			{
				members.add(convertFreeTextStaticProperty(new FreeTextStaticProperty(propertyDescription.getInternalName(), propertyDescription.getLabel(), propertyDescription.getDescription()), clientInput.getInput()));
			}
			else if (multipleValueInput.getMemberType().equals("de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty"))
			{
				members.add(convertDomainStaticProperty(new DomainStaticProperty((DomainStaticProperty)propertyDescription), (DomainConceptInput) clientInput.getInput()));
			}
		}
		
		for(int i = 0; i < members.size(); i++) {
			if (i > 0) members.get(i).setElementName("urn.fzi.de:" + members.get(i).getClass().getSimpleName().toLowerCase() +":" +RandomStringUtils.randomAlphabetic(6));
		}
		
		collectionStaticProperty.setMembers(members);
		
		return collectionStaticProperty;
	}

	private static StaticProperty convertDomainStaticProperty(
			DomainStaticProperty domainStaticProperty, DomainConceptInput input) {
		for(de.fzi.cep.sepa.model.impl.staticproperty.SupportedProperty sp : domainStaticProperty.getSupportedProperties())
		{
			SupportedProperty clientProperty = input.getSupportedProperties()
				.stream()
				.filter(s -> s.getPropertyId().equals(sp.getPropertyId()))
				.findFirst()
				.get();
			
			sp.setValue(clientProperty.getValue());
				
		}
		
		return domainStaticProperty;
	}

	private static StaticProperty convertMatchingStaticProperty(
			MatchingStaticProperty matchingProperty, RadioGroupInput input) {
		for(Option option : input.getOptionLeft()) 
		{
			if (option.isSelected()) matchingProperty.setMatchLeft(URI.create(option.getElementId()));
		}
		for(Option option : input.getOptionRight()) 
		{
			if (option.isSelected()) matchingProperty.setMatchRight(URI.create(option.getElementId()));
		}
		return matchingProperty;
	}

	private static StaticProperty convertAnyStaticProperty(AnyStaticProperty p,
			CheckboxInput input) {
		for(de.fzi.cep.sepa.model.impl.staticproperty.Option sepaOption : p.getOptions())
		{
			Option clientOption = Utils.getOptionById(input.getOptions(), sepaOption.getRdfId().toString());
				if (clientOption.isSelected())
					sepaOption.setSelected(true);
		}
		return p;
	}
	
	private static StaticProperty convertOneOfStaticProperty(OneOfStaticProperty p,
			RadioInput input) {
		for(de.fzi.cep.sepa.model.impl.staticproperty.Option sepaOption : p.getOptions())
		{
			Option clientOption = Utils.getOptionById(input.getOptions(), sepaOption.getRdfId().toString());
				if (clientOption.isSelected())
					sepaOption.setSelected(true);
		}
		return p;
	}

	public static FreeTextStaticProperty convertFreeTextStaticProperty(FreeTextStaticProperty p, FormInput input)
	{
		String value;
		if (input instanceof SliderInput) value = String.valueOf(((SliderInput) input).getValue());
		else value = ((TextInput) input).getValue();
		p.setValue(value);
		return p;
	}
	
	public static MappingPropertyUnary convertMappingPropertyUnary(MappingPropertyUnary p, SelectFormInput input)
	{
		for(Option option : input.getOptions())
		{
			if (option.isSelected()) p.setMapsTo(URI.create(option.getElementId()));
		}
		if (input.getOptions().size() == 1) p.setMapsTo(URI.create(input.getOptions().get(0).getElementId()));
		return p;
	}
	
	public static MappingPropertyNary convertMappingPropertyNary(MappingPropertyNary p, CheckboxInput input)
	{
		List<URI> mapsTo = new ArrayList<URI>();
		for(Option option : input.getOptions())
		{
			if (option.isSelected()) 
				{
					mapsTo.add(URI.create(option.getElementId()));
				}
		}
		//if (input.getOptions().size() == 1) p.setMapsTo(URI.create(input.getOptions().get(0).getElementId()));
		p.setMapsTo(mapsTo);
		return p;
	}

	public static List<SEPAClient> toSEPAClientModel(List<SepaDescription> sepas)
	{
		List<SEPAClient> result = new ArrayList<SEPAClient>();
		for(SepaDescription sepa : sepas) result.add(toSEPAClientModel(sepa));
		return result;
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertFreeTextStaticProperty(
			FreeTextStaticProperty p) {
		FormInput input;
		if (p.getValueSpecification() != null)
		{
			input = new SliderInput();
			((SliderInput) input).setMinValue(p.getValueSpecification().getMinValue());
			((SliderInput) input).setMaxValue(p.getValueSpecification().getMaxValue());
			((SliderInput) input).setStep(p.getValueSpecification().getStep());
		}
		else {
			input = new TextInput();
			((TextInput) input).setValue("");
			if (p.getRequiredDomainProperty() != null) ((TextInput) input).setDomainProperty(p.getRequiredDomainProperty().toString());
			if (p.getRequiredDatatype() != null) ((TextInput) input).setDatatype(p.getRequiredDatatype()
					.toString());
		}
		return new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.STATIC_PROPERTY, p.getInternalName(), p.getLabel(), p.getDescription(), input);
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertOneOfStaticProperty(
			OneOfStaticProperty p) {
		List<Option> options = new ArrayList<Option>();
		for(de.fzi.cep.sepa.model.impl.staticproperty.Option option : p.getOptions())
		{
			Option thisOption = new Option(option.getRdfId().toString(), option.getName());
			options.add(thisOption);
		}
		RadioInput input = new RadioInput(options);
		return new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.STATIC_PROPERTY, p.getInternalName(), p.getLabel(), p.getDescription(), input);
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertAnyStaticProperty(
			AnyStaticProperty p) {
		List<Option> options = new ArrayList<Option>();
		for(de.fzi.cep.sepa.model.impl.staticproperty.Option option : p.getOptions())
		{
			options.add(new Option(option.getElementId(), option.getName()));
		}
		CheckboxInput input = new CheckboxInput(options);
		return new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.STATIC_PROPERTY, p.getInternalName(), p.getLabel(), p.getDescription(), input);
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertStaticProperty(
			StaticProperty p) {
		
		de.fzi.cep.sepa.model.client.StaticProperty property = new de.fzi.cep.sepa.model.client.StaticProperty();
		
		if (p instanceof FreeTextStaticProperty) property = convertFreeTextStaticProperty((FreeTextStaticProperty) p);
		else if (p instanceof OneOfStaticProperty) property = convertOneOfStaticProperty((OneOfStaticProperty) p);
		else if (p instanceof AnyStaticProperty) property = convertAnyStaticProperty((AnyStaticProperty) p);
		else if (p instanceof MappingProperty) 
			{
				if (p instanceof MappingPropertyUnary) property = convertMappingProperty((MappingProperty) p, new SelectFormInput());
				else property = convertMappingProperty((MappingProperty) p, new CheckboxInput());
			}
		else if (p instanceof MatchingStaticProperty) property = convertMatchingProperty((MatchingStaticProperty) p);
		else if (p instanceof DomainStaticProperty) property = convertDomainProperty((DomainStaticProperty) p);
		else if (p instanceof CollectionStaticProperty) property = convertCollectionProperty((CollectionStaticProperty) p);
		
		property.setElementId(p.getRdfId().toString());
		return property;
		//exceptions
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertCollectionProperty(
			CollectionStaticProperty p) {
		
		MultipleValueInput input = new MultipleValueInput();
		input.setMemberType(p.getMemberType());
		if (p.getMemberType().equals("de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty")) input.setMembers(Arrays.asList(convertFreeTextStaticProperty((FreeTextStaticProperty) p.getMembers().get(0))));
		else if (p.getMemberType().equals("de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty")) input.setMembers(Arrays.asList(convertDomainProperty((DomainStaticProperty) p.getMembers().get(0))));
		de.fzi.cep.sepa.model.client.StaticProperty clientProperty = new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.STATIC_PROPERTY, p.getInternalName(), p.getLabel(), p.getDescription(), input);
		clientProperty.setElementId(p.getRdfId().toString());
		
		return clientProperty;
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertDomainProperty(
			DomainStaticProperty p) {
		List<SupportedProperty> supportedProperties = new ArrayList<>();
		
		p.getSupportedProperties().forEach(sp -> supportedProperties.add(new SupportedProperty(sp.getRdfId().toString(), sp.getPropertyId(), sp.getValue(), sp.isValueRequired())));
		
		DomainConceptInput input = new DomainConceptInput(p.getRequiredClass(), supportedProperties);
		
		de.fzi.cep.sepa.model.client.StaticProperty clientProperty = new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.STATIC_PROPERTY, p.getInternalName(), p.getLabel(), p.getDescription(), input);
		clientProperty.setElementId(p.getRdfId().toString());
		return clientProperty;
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertMatchingProperty(
			MatchingStaticProperty p) {
		RadioGroupInput radioGroupInput = new RadioGroupInput();
		
		de.fzi.cep.sepa.model.client.StaticProperty clientProperty = new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.MATCHING_PROPERTY, p.getInternalName(), p.getLabel(), p.getDescription(), radioGroupInput);
		return clientProperty;
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertMappingProperty(
			MappingProperty p, SelectInput input) {
		List<Option> options = new ArrayList<>();
		
		//TODO remove later, just for testing purposes!
		options.add(new Option("elementId", "description"));
		input.setOptions(options);
		//SelectFormInput input = new SelectFormInput(options);
		
		
		de.fzi.cep.sepa.model.client.StaticProperty clientProperty = new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.MAPPING_PROPERTY, p.getInternalName(), p.getLabel(), p.getDescription(), input);
		clientProperty.setElementId(p.getRdfId().toString());
		return clientProperty;
	}

	public static SecDescription fromSECClientModel(ActionClient element) {
		SecDescription sec = StorageManager.INSTANCE.getStorageAPI().getSECById(URI.create(element.getElementId()));
		List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties = element.getStaticProperties();
		sec.setStaticProperties(convertStaticProperties(sec, clientProperties));
		return sec;
	}
	
	public static ActionClient toSECClientModel(SecDescription sec)
	{
		List<String> categories = sec.getEcTypes().size() > 0 ? sec.getEcTypes() : Arrays.asList(EcType.UNCATEGORIZED.name());
		ActionClient client = new ActionClient(sec.getName(), sec.getDescription(), categories);
		client.setElementId(sec.getRdfId().toString());
		client.setIconUrl(sec.getIconUrl());
		List<de.fzi.cep.sepa.model.client.StaticProperty> clientStaticProperties = new ArrayList<>();
		sec.getStaticProperties().forEach((p) -> clientStaticProperties.add(convertStaticProperty(p)));			
		client.setStaticProperties(clientStaticProperties);	
		return client;
	}

	public static List<ActionClient> toActionClientModel(List<SecDescription> secs) {
		List<ActionClient> result = new ArrayList<ActionClient>();
		for(SecDescription sec : secs)
		{
			result.add(toSECClientModel(sec));
		}
		return result;
	}
	
}