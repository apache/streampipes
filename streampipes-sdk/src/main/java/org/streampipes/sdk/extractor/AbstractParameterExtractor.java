package org.streampipes.sdk.extractor;

import com.github.drapostolos.typeparser.TypeParser;
import org.streampipes.model.InvocableSEPAElement;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.eventproperty.EventPropertyNested;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.staticproperty.DomainStaticProperty;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.OneOfStaticProperty;
import org.streampipes.model.impl.staticproperty.Option;
import org.streampipes.model.impl.staticproperty.StaticProperty;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by riemer on 20.03.2017.
 */
public abstract class AbstractParameterExtractor<T extends InvocableSEPAElement> {

  protected T sepaElement;
  private TypeParser typeParser;

  public AbstractParameterExtractor(T sepaElement) {
    this.sepaElement = sepaElement;
    this.typeParser = TypeParser.newBuilder().build();
  }

  public String inputTopic(Integer streamIndex) {
    return sepaElement
            .getInputStreams()
            .get(streamIndex)
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicName();
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
    return mappingPropertyValue(staticPropertyName, false);
  }

  public String mappingPropertyValue(String staticPropertyName,
                                       boolean completeNames)
  {
    URI propertyURI = getURIFromStaticProperty(staticPropertyName);
    for(EventStream stream : sepaElement.getInputStreams())
    {
      List<String> matchedProperties = getMappingPropertyName(stream.getEventSchema().getEventProperties(), propertyURI, completeNames, "");
      if (matchedProperties.size() > 0) return matchedProperties.get(0);
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
