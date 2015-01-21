package de.fzi.cep.sepa.storage.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.client.*;
import de.fzi.cep.sepa.model.client.input.CheckboxInput;
import de.fzi.cep.sepa.model.client.input.FormInput;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.RadioGroupInput;
import de.fzi.cep.sepa.model.client.input.RadioInput;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.client.input.TextInput;
import de.fzi.cep.sepa.model.impl.AnyStaticProperty;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.MatchingStaticProperty;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class ClientModelTransformer {
	
	/**
	 * 
	 * @param sep
	 * @return
	 */
	
	public static List<StreamClient> toStreamClientModel(SEP sep)
	{
		List<StreamClient> result = new ArrayList<StreamClient>();
		sep.getEventStreams().forEach((stream) -> result.add(toStreamClientModel(sep, stream)));
		return result;
	}

	public static List<StreamClient> toStreamClientModel(List<SEP> seps)
	{		
		List<StreamClient> result = new ArrayList<StreamClient>();
		seps.forEach((sep) -> result.addAll(toStreamClientModel(sep)));
		return result;
	}

	public static SEP fromSourceClientModel(SourceClient client)
	{
		return StorageManager.INSTANCE.getEntityManager().find(SEP.class, client.getElementId());
	}

	public static SourceClient toSourceClientModel(SEP sep)
	{
		SourceClient client = new SourceClient(sep.getName(), sep.getDescription(), sep.getDomains());
		client.setIconUrl(sep.getIconUrl());
		client.setElementId(sep.getRdfId().toString());
		return client;
	}

	public static List<SourceClient> toSourceClientModel(List<SEP> seps)
	{
		List<SourceClient> result = new ArrayList<SourceClient>();
		seps.forEach((sep) -> result.add(toSourceClientModel(sep)));
		return result;
			
	}

	public static EventStream fromStreamClientModel(StreamClient client)
	{
		return StorageManager.INSTANCE.getEntityManager().find(EventStream.class, client.getElementId());
	}

	public static StreamClient toStreamClientModel(SEP sep, EventStream stream)
	{
		StreamClient client = new StreamClient(stream.getName(), stream.getDescription(), sep.getRdfId().toString());
		client.setIconUrl(stream.getIconUrl());
		client.setElementId(stream.getRdfId().toString());
		return client;
	}

	public static SourceClient toSEPClientModel(SEP sep)
	{
		return null;
	}

	public static SEPAClient toSEPAClientModel(SEPA sepa)
	{
		SEPAClient client = new SEPAClient(sepa.getName(), sepa.getDescription(), sepa.getDomains());
		client.setInputNodes(sepa.getEventStreams().size());
		client.setElementId(sepa.getRdfId().toString());
		client.setIconUrl(sepa.getIconUrl());
		client.setInputNodes(sepa.getEventStreams().size());
		
		List<de.fzi.cep.sepa.model.client.StaticProperty> clientStaticProperties = new ArrayList<>();
		sepa.getStaticProperties().forEach((p) -> clientStaticProperties.add(convertStaticProperty(p)));			
		client.setStaticProperties(clientStaticProperties);	
		
		return client;
	}

	public static SEPA fromSEPAClientModel(SEPAClient sepaClient)
	{
		SEPA sepa = StorageManager.INSTANCE.getEntityManager().find(SEPA.class, sepaClient.getElementId());
		
		List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties = sepaClient.getStaticProperties();
		sepa.setStaticProperties(convertStaticProperties(sepa, clientProperties));
		
		return sepa;
	}
	
	private static List<StaticProperty> convertStaticProperties(SEC sec, List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties)
	{
		return convertStaticProperties(sec.getStaticProperties(), clientProperties);
	}
	
	private static List<StaticProperty> convertStaticProperties(SEPA sepa, List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties)
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
					resultProperties.add(convertFreeTextStaticProperty((FreeTextStaticProperty) p, ((TextInput) formInput)));
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
				SelectFormInput input = (SelectFormInput) formInput;
				resultProperties.add(convertMappingProperty((MappingProperty) p, input));
			} else if (p instanceof MatchingStaticProperty)
			{
				MatchingStaticProperty matchingProperty = (MatchingStaticProperty) p;
				RadioGroupInput input = (RadioGroupInput) formInput;
				resultProperties.add(convertMatchingStaticProperty(matchingProperty, input));
			}
		}
		return resultProperties;
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
		for(de.fzi.cep.sepa.model.impl.Option sepaOption : p.getOptions())
		{
			Option clientOption = Utils.getOptionById(input.getOptions(), sepaOption.getRdfId().toString());
				if (clientOption.isSelected())
					sepaOption.setSelected(true);
		}
		return p;
	}
	
	private static StaticProperty convertOneOfStaticProperty(OneOfStaticProperty p,
			RadioInput input) {
		for(de.fzi.cep.sepa.model.impl.Option sepaOption : p.getOptions())
		{
			Option clientOption = Utils.getOptionById(input.getOptions(), sepaOption.getRdfId().toString());
				if (clientOption.isSelected())
					sepaOption.setSelected(true);
		}
		return p;
	}

	public static FreeTextStaticProperty convertFreeTextStaticProperty(FreeTextStaticProperty p, TextInput input)
	{
		p.setValue(input.getValue());
		return p;
	}
	
	public static MappingProperty convertMappingProperty(MappingProperty p, SelectFormInput input)
	{
		for(Option option : input.getOptions())
		{
			if (option.isSelected()) p.setMapsTo(URI.create(option.getElementId()));
		}
		if (input.getOptions().size() == 1) p.setMapsTo(URI.create(input.getOptions().get(0).getElementId()));
		return p;
	}

	public static List<SEPAClient> toSEPAClientModel(List<SEPA> sepas)
	{
		List<SEPAClient> result = new ArrayList<SEPAClient>();
		for(SEPA sepa : sepas) result.add(toSEPAClientModel(sepa));
		return result;
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertFreeTextStaticProperty(
			FreeTextStaticProperty p) {
		TextInput input = new TextInput();
		input.setValue("");
		return new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.STATIC_PROPERTY, p.getName(), p.getDescription(), input);
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertOneOfStaticProperty(
			OneOfStaticProperty p) {
		List<Option> options = new ArrayList<Option>();
		for(de.fzi.cep.sepa.model.impl.Option option : p.getOptions())
		{
			Option thisOption = new Option(option.getRdfId().toString(), option.getName());
			options.add(thisOption);
		}
		RadioInput input = new RadioInput(options);
		return new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.STATIC_PROPERTY, p.getName(), p.getDescription(), input);
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertAnyStaticProperty(
			AnyStaticProperty p) {
		List<Option> options = new ArrayList<Option>();
		for(de.fzi.cep.sepa.model.impl.Option option : p.getOptions())
		{
			options.add(new Option(option.getElementId(), option.getName()));
		}
		CheckboxInput input = new CheckboxInput(options);
		return new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.STATIC_PROPERTY, p.getName(), p.getDescription(), input);
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertStaticProperty(
			StaticProperty p) {
		
		de.fzi.cep.sepa.model.client.StaticProperty property = new de.fzi.cep.sepa.model.client.StaticProperty();
		
		if (p instanceof FreeTextStaticProperty) property = convertFreeTextStaticProperty((FreeTextStaticProperty) p);
		else if (p instanceof OneOfStaticProperty) property = convertOneOfStaticProperty((OneOfStaticProperty) p);
		else if (p instanceof AnyStaticProperty) property = convertAnyStaticProperty((AnyStaticProperty) p);
		else if (p instanceof MappingProperty) property = convertMappingProperty((MappingProperty) p);
		else if (p instanceof MatchingStaticProperty) property = convertMatchingProperty((MatchingStaticProperty) p);
		
		property.setElementId(p.getRdfId().toString());
		return property;
		//exceptions
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertMatchingProperty(
			MatchingStaticProperty p) {
		RadioGroupInput radioGroupInput = new RadioGroupInput();
		
		de.fzi.cep.sepa.model.client.StaticProperty clientProperty = new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.MATCHING_PROPERTY, p.getName(), p.getDescription(), radioGroupInput);
		return clientProperty;
	}

	private static de.fzi.cep.sepa.model.client.StaticProperty convertMappingProperty(
			MappingProperty p) {
		List<Option> options = new ArrayList<>();
		
		//TODO remove later, just for testing purposes!
		options.add(new Option("elementId", "description"));
		
		SelectFormInput input = new SelectFormInput(options);
		
		
		de.fzi.cep.sepa.model.client.StaticProperty clientProperty = new de.fzi.cep.sepa.model.client.StaticProperty(StaticPropertyType.MAPPING_PROPERTY, p.getName(), p.getDescription(), input);
		clientProperty.setElementId(p.getRdfId().toString());
		return clientProperty;
	}

	public static SEC fromSECClientModel(ActionClient element) {
		SEC sec = StorageManager.INSTANCE.getEntityManager().find(SEC.class, element.getElementId());
		List<de.fzi.cep.sepa.model.client.StaticProperty> clientProperties = element.getStaticProperties();
		sec.setStaticProperties(convertStaticProperties(sec, clientProperties));
		return sec;
	}
	
	public static ActionClient toSECClientModel(SEC sec)
	{
		ActionClient client = new ActionClient(sec.getName(), sec.getDescription());
		client.setElementId(sec.getRdfId().toString());
		List<de.fzi.cep.sepa.model.client.StaticProperty> clientStaticProperties = new ArrayList<>();
		sec.getStaticProperties().forEach((p) -> clientStaticProperties.add(convertStaticProperty(p)));			
		client.setStaticProperties(clientStaticProperties);	
		return client;
	}

	public static List<ActionClient> toActionClientModel(List<SEC> secs) {
		List<ActionClient> result = new ArrayList<ActionClient>();
		for(SEC sec : secs)
		{
			result.add(toSECClientModel(sec));
		}
		return result;
	}
	
}