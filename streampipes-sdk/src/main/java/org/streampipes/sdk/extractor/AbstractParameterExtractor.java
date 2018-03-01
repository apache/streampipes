package org.streampipes.sdk.extractor;

import com.github.drapostolos.typeparser.TypeParser;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.staticproperty.CollectionStaticProperty;
import org.streampipes.model.staticproperty.DomainStaticProperty;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.OneOfStaticProperty;
import org.streampipes.model.staticproperty.Option;
import org.streampipes.model.staticproperty.StaticProperty;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractParameterExtractor<T extends InvocableStreamPipesEntity> {

  protected T sepaElement;
  private TypeParser typeParser;

  public AbstractParameterExtractor(T sepaElement) {
    this.sepaElement = sepaElement;
    this.typeParser = TypeParser.newBuilder().build();
  }

  public String measurementUnit(String runtimeName, Integer streamIndex) {
   return sepaElement
           .getInputStreams()
           .get(streamIndex)
           .getEventSchema()
           .getEventProperties()
           .stream()
           .filter(ep -> ep.getRuntimeName().equals(runtimeName))
           .map(ep -> (EventPropertyPrimitive) ep)
           .findFirst()
           .get()
           .getMeasurementUnit()
           .toString();
  }

  public String inputTopic(Integer streamIndex) {
    return sepaElement
            .getInputStreams()
            .get(streamIndex)
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  public <V> V singleValueParameter(String internalName, Class<V> targetClass) {
    return typeParser.parse(getStaticPropertyByName(internalName, FreeTextStaticProperty.class)
            .getValue(), targetClass);
  }

  public <V> V selectedSingleValue(String internalName, Class<V> targetClass) {
      return typeParser.parse(getStaticPropertyByName(internalName, OneOfStaticProperty.class)
              .getOptions()
              .stream()
              .filter(Option::isSelected)
              .findFirst()
              .get()
              .getName(), targetClass);
  }

  public <V> List<V> singleValueParameterFromCollection(String internalName, Class<V> targetClass) {
    CollectionStaticProperty collection = getStaticPropertyByName(internalName, CollectionStaticProperty.class);
    return collection
            .getMembers()
            .stream()
            .map(sp -> (FreeTextStaticProperty) sp)
            .map(FreeTextStaticProperty::getValue)
            .map(v -> typeParser.parse(v, targetClass))
            .collect(Collectors.toList());
  }

  public <V> List<V> selectedMultiValues(String internalName, Class<V> targetClass) {
    return getStaticPropertyByName(internalName, OneOfStaticProperty.class)
            .getOptions()
            .stream()
            .filter(Option::isSelected)
            .map(o -> o.getName())
            .map(o -> typeParser.parse(o, targetClass))
            .collect(Collectors.toList());
  }

  private <S extends StaticProperty> S getStaticPropertyByName(String internalName, Class<S>
          spType) {
    return spType.cast(getStaticPropertyByName(internalName));
  }

  private StaticProperty getStaticPropertyByName(String name)
  {
    for(StaticProperty p : sepaElement.getStaticProperties())
    {
      if (p.getInternalName().equals(name)) return p;
    }
    return null;
  }

  public String mappingPropertyValue(String staticPropertyName)
  {
    return mappingPropertyValues(staticPropertyName, false).get(0);
  }

  public String propertyDatatype(String runtimeName) {
    List<EventProperty> eventProperties = new ArrayList<>();
    for(SpDataStream is : sepaElement.getInputStreams()) {
      eventProperties.addAll(is.getEventSchema().getEventProperties());
    }

    Optional<EventProperty> matchedProperty = eventProperties
            .stream()
            .filter(ep -> ep.getRuntimeName().equals
            (runtimeName))
            .findFirst();

    if (matchedProperty.isPresent()) {
      EventProperty p = matchedProperty.get();
      if (p instanceof EventPropertyPrimitive) {
        return ((EventPropertyPrimitive) p).getRuntimeType();
      } else if (p instanceof EventPropertyList) {
        EventProperty listProperty = ((EventPropertyList) p).getEventProperties().get(0);
        if (listProperty instanceof EventPropertyPrimitive) {
          return ((EventPropertyPrimitive) listProperty).getRuntimeType();
        }
      }
    }
    // TODO exceptions
    return null;
  }

  public List<String> mappingPropertyValues(String staticPropertyName,
                                       boolean completeNames)
  {
    URI propertyURI = getURIFromStaticProperty(staticPropertyName);
    for(SpDataStream stream : sepaElement.getInputStreams())
    {
      List<String> matchedProperties = getMappingPropertyName(stream.getEventSchema().getEventProperties(), propertyURI, completeNames, "");
      if (matchedProperties.size() > 0) return matchedProperties;
    }
    return null;
    //TODO: exceptions
  }

  public <V> V supportedOntologyPropertyValue(String domainPropertyInternalId, String
          propertyId, Class<V> targetClass)
  {
    DomainStaticProperty dsp = getStaticPropertyByName(domainPropertyInternalId,
            DomainStaticProperty.class);

    return typeParser.parse(dsp
            .getSupportedProperties()
            .stream()
            .filter(sp -> sp.getPropertyId().equals(propertyId))
            .findFirst()
            .map(m -> m.getValue())
            .get(), targetClass);

  }

  // TODO copied from SepaUtils, refactor code
  private List<String> getMappingPropertyName(List<EventProperty> eventProperties, URI propertyURI, boolean completeNames, String prefix)
  {
    List<String> result = new ArrayList<String>();
    for(EventProperty p : eventProperties)
    {
      if (p instanceof EventPropertyPrimitive || p instanceof EventPropertyList)
      {
        if (p.getElementId().toString().equals(propertyURI.toString()))
        {
          if (!completeNames) result.add(p.getRuntimeName());
          else
            result.add(prefix + p.getRuntimeName());
        }
        if (p instanceof EventPropertyList)
        {
          for(EventProperty sp : ((EventPropertyList) p).getEventProperties())
          {
            if (sp.getElementId().toString().equals(propertyURI.toString()))
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

  private URI getURIFromStaticProperty(String staticPropertyName)
  {
    Optional<MappingPropertyUnary> property = sepaElement.getStaticProperties().stream()
            .filter(p -> p instanceof MappingPropertyUnary)
            .map((p -> (MappingPropertyUnary) p))
            .filter(p -> p.getInternalName().equals(staticPropertyName))
            .findFirst();

    if (property.isPresent()) {
      return property.get().getMapsTo();
    } else {
      return null;
    }
    //TODO: exceptions
  }
}
