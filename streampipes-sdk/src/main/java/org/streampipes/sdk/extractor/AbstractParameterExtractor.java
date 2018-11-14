/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sdk.extractor;

import com.github.drapostolos.typeparser.TypeParser;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.staticproperty.*;

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

  private <V, T extends SelectionStaticProperty> V selectedSingleValue(String internalName, Class<V> targetClass, Class<T> oneOfStaticProperty) {
    return typeParser.parse(getStaticPropertyByName(internalName, oneOfStaticProperty)
            .getOptions()
            .stream()
            .filter(Option::isSelected)
            .findFirst()
            .get()
            .getName(), targetClass);
  }


  public <V> V selectedSingleValueFromRemote(String internalName, Class<V> targetClass) {
    return selectedSingleValue(internalName, targetClass, RuntimeResolvableOneOfStaticProperty.class);
  }

  public <V> V selectedSingleValue(String internalName, Class<V> targetClass) {
    return selectedSingleValue(internalName, targetClass, OneOfStaticProperty.class);
  }

  public <V> V selectedSingleValueInternalName(String internalName, Class<V> targetClass) {
    return typeParser.parse(getStaticPropertyByName(internalName, OneOfStaticProperty.class)
            .getOptions()
            .stream()
            .filter(Option::isSelected)
            .findFirst()
            .get()
            .getInternalName(), targetClass);
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
            .map(Option::getName)
            .map(o -> typeParser.parse(o, targetClass))
            .collect(Collectors.toList());
  }

  private <S extends StaticProperty> S getStaticPropertyByName(String internalName, Class<S>
          spType) {
    return spType.cast(getStaticPropertyByName(internalName));
  }

  private StaticProperty getStaticPropertyByName(String name) {
    for (StaticProperty p : sepaElement.getStaticProperties()) {
      if (p.getInternalName().equals(name)) {
        return p;
      }
    }
    return null;
  }

  public String mappingPropertyValue(String staticPropertyName) {
    URI propertyURI = getURIFromStaticProperty(staticPropertyName);
    return mappingPropertyValues(staticPropertyName, false, propertyURI).get(0);
  }

  public List<String> mappingPropertyValues(String staticPropertyName) {
    Optional<MappingPropertyNary> mappingPropertyOpt = sepaElement.getStaticProperties().stream()
            .filter(p -> p instanceof MappingPropertyNary)
            .map((p -> (MappingPropertyNary) p))
            .filter(p -> p.getInternalName().equals(staticPropertyName))
            .findFirst();

    if (mappingPropertyOpt.isPresent()) {
      MappingPropertyNary mappingProperty = mappingPropertyOpt.get();
      List<String> result = new ArrayList<>();

      // TODO fix empire which is sometimes not returning a URI
      for (Object obj : mappingProperty.getMapsTo()) {
        if (obj.getClass().isAssignableFrom(URI.class)) {
          result.addAll(mappingPropertyValues(staticPropertyName, false, (URI) obj));
        } else {
          if (obj instanceof EventProperty) {
            EventProperty property = (EventProperty) obj;
            result.add(property.getRuntimeName());
          }
        }
      }
      return result;
    } else {
      return new ArrayList<>();
    }
  }

  public String propertyDatatype(String runtimeName) {
    List<EventProperty> eventProperties = new ArrayList<>();
    for (SpDataStream is : sepaElement.getInputStreams()) {
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
                                            boolean completeNames, URI propertyURI) {
    for (SpDataStream stream : sepaElement.getInputStreams()) {
      List<String> matchedProperties = getMappingPropertyName(stream.getEventSchema().getEventProperties(), propertyURI, completeNames, "");
      if (matchedProperties.size() > 0) {
        return matchedProperties;
      }
    }
    return null;
    //TODO: exceptions
  }

  public <V> V supportedOntologyPropertyValue(String domainPropertyInternalId, String
          propertyId, Class<V> targetClass) {
    DomainStaticProperty dsp = getStaticPropertyByName(domainPropertyInternalId,
            DomainStaticProperty.class);

    return typeParser.parse(dsp
            .getSupportedProperties()
            .stream()
            .filter(sp -> sp.getPropertyId().equals(propertyId))
            .findFirst()
            .map(SupportedProperty::getValue)
            .get(), targetClass);

  }

  // TODO copied from SepaUtils, refactor code
  private List<String> getMappingPropertyName(List<EventProperty> eventProperties, URI propertyURI, boolean completeNames, String prefix) {
    List<String> result = new ArrayList<>();
    for (EventProperty p : eventProperties) {
      if (p instanceof EventPropertyPrimitive || p instanceof EventPropertyList) {
        if (p.getElementId().equals(propertyURI.toString())) {
          if (!completeNames) {
            result.add(p.getRuntimeName());
          } else {
            result.add(prefix + p.getRuntimeName());
          }
        }
        if (p instanceof EventPropertyList) {
          for (EventProperty sp : ((EventPropertyList) p).getEventProperties()) {
            if (sp.getElementId().equals(propertyURI.toString())) {
              result.add(p.getRuntimeName() + "," + sp.getRuntimeName());
            }
          }
        }
      } else if (p instanceof EventPropertyNested) {
        result.addAll(getMappingPropertyName(((EventPropertyNested) p).getEventProperties(), propertyURI, completeNames, prefix + p.getRuntimeName() + "."));
      }
    }
    return result;
  }

  private URI getURIFromStaticProperty(String staticPropertyName) {
    Optional<MappingPropertyUnary> property = sepaElement.getStaticProperties().stream()
            .filter(p -> p instanceof MappingPropertyUnary)
            .map((p -> (MappingPropertyUnary) p))
            .filter(p -> p.getInternalName().equals(staticPropertyName))
            .findFirst();

    return property.map(MappingPropertyUnary::getMapsTo).orElse(null);
    //TODO: exceptions
  }
}
