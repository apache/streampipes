/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.manager.selector;

import static org.streampipes.model.constants.PropertySelectorConstants.*;

import org.streampipes.model.output.PropertyRenameRule;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.util.Cloner;
import org.streampipes.sdk.helpers.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PropertySelector {

  private EventSchema firstSchema;
  private EventSchema secondSchema;
  private List<EventProperty> outputProperties;
  private List<PropertyRenameRule> propertyRenameRules;

  public PropertySelector() {
    this.outputProperties = new ArrayList<>();
    this.propertyRenameRules = new ArrayList<>();
  }

  public PropertySelector(EventSchema firstSchema) {
    this();
    this.firstSchema = firstSchema;
  }

  public PropertySelector(EventSchema firstSchema, EventSchema secondSchema) {
    this();
    this.firstSchema = firstSchema;
    this.secondSchema = secondSchema;
  }

  private List<EventProperty> extractProperties(List<EventProperty> inputProperties, List<String>
          propertySelectors, String currentPropertyPointer, List<EventProperty> appendProperties) {
    List<EventProperty> outputProperties = new ArrayList<>();

    for (EventProperty inputProperty : inputProperties) {
      for (String propertySelector : propertySelectors) {
        if (isInSelection(inputProperty, propertySelector, currentPropertyPointer)) {
          EventProperty outputProperty = new Cloner().property(inputProperty);
          if (outputProperty instanceof EventPropertyNested) {
            ((EventPropertyNested) outputProperty).setEventProperties(extractProperties
                    (((EventPropertyNested) outputProperty).getEventProperties
                            (), propertySelectors, makeSelector(currentPropertyPointer,
                            inputProperty.getRuntimeName()), appendProperties));
          }
          if (isPresent(outputProperty.getRuntimeName(), outputProperties, appendProperties)) {
            String newRuntimeName = createRuntimeName(outputProperty
                    .getRuntimeName(), outputProperties, appendProperties);
            propertyRenameRules.add(new PropertyRenameRule(makeSelector(currentPropertyPointer,
                    outputProperty.getRuntimeName()), newRuntimeName));
            outputProperty.setRuntimeName(newRuntimeName);
          }
          outputProperties.add(new Cloner().property(outputProperty));
        }
      }
    }

    return outputProperties;
  }

  private String createRuntimeName(String runtimeName,
                                   List<EventProperty> outputProperties,
                                   List<EventProperty> appendProperties) {
    int i = 0;
    String newRuntimeName;
    for (;;) {
      if (!isPresent(runtimeName + "_" + i, outputProperties, appendProperties)) {
        newRuntimeName = runtimeName + "_" + i;
        break;
      }
      i++;
    }

    return newRuntimeName;
  }

  private Boolean isPresent(String runtimeName,
                            List<EventProperty> outputProperties,
                            List<EventProperty> appendProperties) {
    return outputProperties.stream().anyMatch(p -> p.getRuntimeName().equals(runtimeName)) ||
            this.outputProperties.stream().anyMatch(p -> p.getRuntimeName().equals(runtimeName)) ||
            appendProperties.stream().anyMatch(ap -> ap.getRuntimeName().equals(runtimeName));
  }


  private boolean isInSelection(EventProperty inputProperty, String propertySelector, String currentPropertyPointer) {
    return (currentPropertyPointer
            + PROPERTY_DELIMITER
            + inputProperty.getRuntimeName()).equals(propertySelector);
  }

  public List<EventProperty> createPropertyList(final List<String> propertySelectors) {
    return createPropertyList(propertySelectors, new ArrayList<>());
  }

  public List<EventProperty> createPropertyList(final List<String> propertySelectors,
                                                List<EventProperty> appendProperties) {

    outputProperties.addAll(extractProperties(PropertySelectorUtils.getProperties(firstSchema),
            getPropertySelectors
            (propertySelectors, FIRST_STREAM_ID_PREFIX), FIRST_STREAM_ID_PREFIX, appendProperties));
    outputProperties.addAll(extractProperties(PropertySelectorUtils.getProperties(secondSchema),
            getPropertySelectors
            (propertySelectors, SECOND_STREAM_ID_PREFIX), SECOND_STREAM_ID_PREFIX, appendProperties));

    outputProperties.addAll(appendProperties.stream().map(ep -> new Cloner().property(ep)).collect(Collectors.toList()));

    return outputProperties;
  }

  public Tuple2<List<EventProperty>, List<PropertyRenameRule>> createRenamedPropertyList(final List<String> propertySelectors) {
    return new Tuple2<>(createPropertyList(propertySelectors, new ArrayList<>()), propertyRenameRules);
  }

  public Tuple2<List<EventProperty>, List<PropertyRenameRule>> createRenamedPropertyList(final List<String> propertySelectors, List<EventProperty> appendProperties) {
    return new Tuple2<>(createPropertyList(propertySelectors, appendProperties), propertyRenameRules);
  }

  private String makeSelector(String prefix, String current) {
    return prefix + PROPERTY_DELIMITER + current;
  }

  private List<String> getPropertySelectors(List<String> propertySelectors, String prefix) {
    return propertySelectors
            .stream()
            .filter(s -> s.startsWith(prefix))
            .collect(Collectors.toList());
  }
}
