package org.streampipes.model.util;

import org.streampipes.model.base.ConsumableStreamPipesEntity;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.staticproperty.AnyStaticProperty;
import org.streampipes.model.staticproperty.DomainStaticProperty;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.model.staticproperty.MappingPropertyNary;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.MatchingStaticProperty;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.RemoteOneOfStaticProperty;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SepaUtils {

	// TODO move this to SDK (extractors)

	public static String getSupportedPropertyValue(DomainStaticProperty dsp, String propertyId)
	{
		Optional<SupportedProperty> matchedProperty = dsp.getSupportedProperties().stream().filter(sp -> sp.getPropertyId().equals(propertyId)).findFirst();
		if (matchedProperty.isPresent()) return matchedProperty.get().getValue();
		else return "";
	}
	
	public static DomainStaticProperty getDomainStaticPropertyBy(InvocableStreamPipesEntity sepa, String internalName)
	{
		Optional<StaticProperty> matchedProperty = sepa.getStaticProperties().stream().filter(sp -> (sp instanceof DomainStaticProperty) && (sp.getInternalName().equals(internalName))).findFirst();
		if (matchedProperty.isPresent()) return (DomainStaticProperty) matchedProperty.get();
		else return null;
	}
	
	public static StaticProperty getStaticPropertyByInternalName(ConsumableStreamPipesEntity sepa, String internalName)
	{
		return getStaticPropertyByName(sepa.getStaticProperties(), internalName);
	}
	
	public static String getFreeTextStaticPropertyValue(InvocableStreamPipesEntity graph, String internalName)
	{
		StaticProperty staticProperty = getStaticPropertyByInternalName(graph, internalName);
		if (staticProperty instanceof FreeTextStaticProperty)
			return ((FreeTextStaticProperty) staticProperty).getValue();
		return null;
	}
	
	public static StaticProperty getStaticPropertyByInternalName(InvocableStreamPipesEntity seg, String internalName)
	{
		return getStaticPropertyByName(seg.getStaticProperties(), internalName);
	}
	
	public static <T> T getStaticPropertyByInternalName(InvocableStreamPipesEntity seg, String internalName, Class<T> clazz)
	{
		return clazz.cast(getStaticPropertyByInternalName(seg, internalName));
	}
	
	public static StaticProperty getStaticPropertyByInternalName(DataSinkInvocation sec, String internalName)
	{
		return getStaticPropertyByName(sec.getStaticProperties(), internalName);
	}
	
	// TODO: fetch event property from db for given static property name
	public static String getMappingPropertyName(InvocableStreamPipesEntity sepa, String staticPropertyName)
	{
		return getMappingPropertyName(sepa, staticPropertyName, false);
	}
	
	public static String getMappingPropertyName(InvocableStreamPipesEntity sepa, String staticPropertyName, boolean completeNames)
	{
		URI propertyURI = getURIFromStaticProperty(sepa, staticPropertyName);
		for(SpDataStream stream : sepa.getInputStreams())
		{
			List<String> matchedProperties = getMappingPropertyName(stream.getEventSchema().getEventProperties(), propertyURI, completeNames, "");
			if (matchedProperties.size() > 0) return matchedProperties.get(0);
		}
		return null;
		//TODO: exceptions
	}
	
	public static List<String> getMatchingPropertyNames(InvocableStreamPipesEntity sepa, String staticPropertyName) {
		
		List<String> propertyNames = new ArrayList<>();
		for(int i = 0; i <= 1; i++)
		{
			URI propertyURI = getMatchingPropertyURI(sepa, staticPropertyName, i == 0);
			SpDataStream stream = sepa.getInputStreams().get(i);
			propertyNames.add(stream.getEventSchema().getEventProperties().stream().filter(p -> p.getElementId().equals(propertyURI.toString())).findFirst().get().getRuntimeName());
		}
		return propertyNames;
		//TODO: exceptions
	}
	public static List<String> getMultipleMappingPropertyNames(InvocableStreamPipesEntity sepa, String staticPropertyName, boolean completeNames)
	{
		List<URI> propertyUris = getMultipleURIsFromStaticProperty(sepa, staticPropertyName);
		
		List<String> result = new ArrayList<String>();
		for(URI propertyUri : propertyUris)
		{
			for(SpDataStream stream : sepa.getInputStreams())
			{
				result.addAll(getMappingPropertyName(stream.getEventSchema().getEventProperties(), propertyUri, completeNames, ""));
			}
		}
		return result;
		//TODO: exceptions
	}
	
	//TODO fix return null
	private static List<String> getMappingPropertyName(List<EventProperty> eventProperties, URI propertyURI, boolean completeNames, String prefix)
	{
		List<String> result = new ArrayList<String>();
		for(EventProperty p : eventProperties)
		{
			if (p instanceof EventPropertyPrimitive || p instanceof EventPropertyList)
			{	
				if (p.getElementId().equals(propertyURI.toString()))
					{
						if (!completeNames) result.add(p.getRuntimeName());
						else 
								result.add(prefix + p.getRuntimeName());
					}
				if (p instanceof EventPropertyList)
				{
					for(EventProperty sp : ((EventPropertyList) p).getEventProperties())
					{
						if (sp.getRdfId().toString().equals(propertyURI.toString()))
						{
							result.add(p.getRuntimeName() + "," +sp.getRuntimeName());
						}
					}
				}
			}
			else if (p instanceof EventPropertyNested)
			{
				result.addAll(getMappingPropertyName(((EventPropertyNested) p).getEventProperties(), propertyURI, completeNames, prefix + p.getRuntimeName() +"."));
			}
		}
		return result;
	}
	
	//TODO check if correct
	private static String getEventPropertyNameByPrefix(List<EventProperty> eventProperties, String namePrefix, boolean completeNames, String propertyPrefix)
	{
		for(EventProperty p : eventProperties)
		{
			if (p instanceof EventPropertyPrimitive || p instanceof EventPropertyList)
			{
				if (p.getRuntimeName().startsWith(namePrefix)) 
					{
						if (!completeNames) return p.getRuntimeName();
						else return propertyPrefix + p.getRuntimeName();
					}
			}
			else if (p instanceof EventPropertyNested)
			{
				//propertyPrefix = propertyPrefix + p.getPropertyName() +".";
				//getEventPropertyNameByPrefix(((EventPropertyNested) p).getEventProperties(), namePrefix, completeNames, propertyPrefix);
				if (p.getRuntimeName().startsWith(namePrefix))
				{
					if (!completeNames) return p.getRuntimeName();
					else return propertyPrefix + p.getRuntimeName();
				}
			}
		}
		return null;
	}
	
	public static String getEventPropertyName(List<EventProperty> properties, String namePrefix)
	{
		return getEventPropertyNameByPrefix(properties, namePrefix, true, "");
	}
	
	private static URI getMatchingPropertyURI(InvocableStreamPipesEntity sepa, String propertyName, boolean first) {
		List<MatchingStaticProperty> properties = sepa
				.getStaticProperties()
				.stream()
				.filter(sp -> sp instanceof MatchingStaticProperty)
				.map(m -> ((MatchingStaticProperty)m))
				.collect(Collectors.toList());
		
		for(MatchingStaticProperty m : properties) {
				if (m.getInternalName().equals(propertyName)) {
					if (first) return m.getMatchLeft();
					else return m.getMatchRight();
				}
		}
		return null;
	}
	private static URI getURIFromStaticProperty(InvocableStreamPipesEntity sepa, String staticPropertyName)
	{
		for(StaticProperty p : sepa.getStaticProperties())
		{		
			if (p instanceof MappingPropertyUnary)
			{
				MappingPropertyUnary mp = (MappingPropertyUnary) p;
				// check if anything else breaks
				if (mp.getInternalName().equals(staticPropertyName)) return mp.getMapsTo();
			}
		}
		return null;
		//TODO: exceptions
	}
	
	private static List<URI> getMultipleURIsFromStaticProperty(InvocableStreamPipesEntity sepa, String staticPropertyName)
	{
		for(StaticProperty p : sepa.getStaticProperties())
		{
			if (p instanceof MappingPropertyNary)
			{
				MappingPropertyNary mp = (MappingPropertyNary) p;
				if (mp.getInternalName().equals(staticPropertyName)) return mp.getMapsTo();
			}
		}
		return null;
		//TODO: exceptions
	}
	
	public static URI getURIbyPropertyName(SpDataStream stream, String propertyName)
	{
		for(EventProperty p : stream.getEventSchema().getEventProperties())
		{
			if (p.getRuntimeName().equals(propertyName))
				try {
					return new URI(p.getRdfId().toString());
				} catch (URISyntaxException e) {
					return null;
				}
		}
		return null;
		//TODO exceptions
	}
	
	
	private static StaticProperty getStaticPropertyByName(List<StaticProperty> properties, String name)
	{
		for(StaticProperty p : properties)
		{
			if (p.getInternalName().equals(name)) return p;
		}
		return null;
	}

	public static String getOneOfProperty(InvocableStreamPipesEntity sepa,
			String staticPropertyName) {
		for(StaticProperty p : sepa.getStaticProperties())
		{
			if (p.getInternalName().equals(staticPropertyName))
			{
				if (p instanceof OneOfStaticProperty)
				{
					OneOfStaticProperty thisProperty = (OneOfStaticProperty) p;
					for(Option option : thisProperty.getOptions())
					{
						if (option.isSelected()) return option.getName();
					}
				}
			}
		}
		return null;
		//TODO exceptions
	}

	public static String getRemoteOneOfProperty(InvocableStreamPipesEntity sepa,
			String staticPropertyName) {
		for(StaticProperty p : sepa.getStaticProperties())
		{
			if (p.getInternalName().equals(staticPropertyName))
			{
				if (p instanceof RemoteOneOfStaticProperty)
				{
					RemoteOneOfStaticProperty thisProperty = (RemoteOneOfStaticProperty) p;
					for(Option option : thisProperty.getOptions())
					{
						if (option.isSelected()) return option.getName();
					}
				}
			}
		}
		return null;
		//TODO exceptions
	}
	
	public static List<StaticProperty> cloneStaticProperties(List<StaticProperty> staticProperties)
	{
		List<StaticProperty> result = new ArrayList<>();
		for(StaticProperty property : staticProperties)
		{
			if (property instanceof FreeTextStaticProperty)
				result.add(generateClonedFreeTextProperty((FreeTextStaticProperty) property));
			else if (property instanceof OneOfStaticProperty)
					result.add(generateClonedOneOfStaticProperty((OneOfStaticProperty) property));
				else if (property instanceof MappingProperty)
					result.add(generateClonedMappingProperty((MappingProperty) property));
					else if (property instanceof MatchingStaticProperty)
						result.add(generateClonedMatchingStaticProperty((MatchingStaticProperty) property));
					else if (property instanceof AnyStaticProperty)
						result.add(generateAnyStaticProperty((AnyStaticProperty) property));
		}
		return result;
	}

	private static StaticProperty generateAnyStaticProperty(
			AnyStaticProperty property) {
		AnyStaticProperty newProperty = new AnyStaticProperty(property.getInternalName(), property.getLabel(), property.getDescription());
		newProperty.setOptions(cloneOptions(property.getOptions()));
		return newProperty;
		
	}

	private static List<Option> cloneOptions(List<Option> options) {
		List<Option> result = new ArrayList<Option>();
		for(Option option : options)
		{
			Option newOption = new Option(option.getName(), option.isSelected());
			result.add(newOption);
		}
		return result;
	}

	private static StaticProperty generateClonedMatchingStaticProperty(
			MatchingStaticProperty property) {
		MatchingStaticProperty mp = new MatchingStaticProperty(property.getInternalName(), property.getLabel(), property.getDescription());
		mp.setMatchLeft(property.getMatchLeft());
		mp.setMatchRight(property.getMatchRight());
		return mp;
	}

	private static StaticProperty generateClonedMappingProperty(
			MappingProperty property) {
		
		if (property instanceof MappingPropertyUnary)
		{
			MappingPropertyUnary unaryProperty = (MappingPropertyUnary) property;
			MappingPropertyUnary mp = new MappingPropertyUnary(unaryProperty.getMapsFrom(), unaryProperty.getInternalName(), unaryProperty.getLabel(), unaryProperty.getDescription());
			mp.setMapsTo(unaryProperty.getMapsTo());
			return mp;
		}
		else {
			MappingPropertyNary naryProperty = (MappingPropertyNary) property;
			MappingPropertyNary mp = new MappingPropertyNary(naryProperty.getMapsFrom(), naryProperty.getInternalName(), naryProperty.getLabel(), naryProperty.getDescription());
			mp.setMapsTo(naryProperty.getMapsTo());
			return mp;
		}
	}

	private static StaticProperty generateClonedOneOfStaticProperty(
			OneOfStaticProperty property) {
		OneOfStaticProperty osp = new OneOfStaticProperty(property.getInternalName(), property.getLabel(), property.getDescription());
		osp.setOptions(cloneOptions(osp.getOptions()));
		return osp;
	}

	private static StaticProperty generateClonedFreeTextProperty(
			FreeTextStaticProperty property) {
		FreeTextStaticProperty ftsp = new FreeTextStaticProperty(property.getInternalName(), property.getLabel(), property.getDescription());
		ftsp.setRequiredDomainProperty(property.getRequiredDomainProperty());
		ftsp.setValue(property.getValue());
		return ftsp;
	}
	
	public static String getFullPropertyName(EventProperty property, List<EventProperty> topLevelProperties, String initialPrefix, char delimiter)
	{
		for(EventProperty schemaProperty : topLevelProperties)
		{
			if (property.getRdfId().toString().equals(schemaProperty.getRdfId().toString())) return initialPrefix + property.getRuntimeName();
			else if (schemaProperty instanceof EventPropertyNested)
			{
				return getFullPropertyName(property, ((EventPropertyNested) schemaProperty).getEventProperties(), initialPrefix +schemaProperty.getRuntimeName() +delimiter, delimiter);
			}
		}
		return null;
	}

	public static EventProperty getEventPropertyById(DataProcessorInvocation graph,
			URI replaceFrom) {
		for(SpDataStream stream : graph.getInputStreams()) {
			for(EventProperty p : stream.getEventSchema().getEventProperties()) {
				if (p.getElementId().equals(replaceFrom.toString())) return p;
			}
		}
		return null;
	}
}
