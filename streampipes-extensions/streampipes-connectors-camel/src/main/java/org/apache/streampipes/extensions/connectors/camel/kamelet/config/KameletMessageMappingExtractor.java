/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.extensions.connectors.camel.kamelet.config;

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.connectors.camel.kamelet.message.KameletHeaderMapping;
import org.apache.streampipes.extensions.connectors.camel.kamelet.message.KameletMessageMapping;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KameletMessageMappingExtractor {

  public KameletMessageMapping extract(IParameterExtractor extractor) {
    KameletMessageMapping.PayloadMode payloadMode = KameletMessageMapping.PayloadMode.EVENT_MAP;
    String bodyFieldSelector = null;

    StaticPropertyAlternatives payloadAlternatives = extractor.getStaticPropertyByName(
        KameletSinkStaticPropertyProvider.PAYLOAD_ALTERNATIVES_KEY,
        StaticPropertyAlternatives.class
    );

    if (payloadAlternatives != null) {
      Optional<StaticPropertyAlternative> selectedAlternative = getSelectedAlternative(payloadAlternatives);
      if (selectedAlternative.isPresent()) {
        String internalName = selectedAlternative.get().getInternalName();
        if (KameletSinkStaticPropertyProvider.PAYLOAD_EVENT_JSON_KEY.equals(internalName)) {
          payloadMode = KameletMessageMapping.PayloadMode.EVENT_JSON;
        } else if (KameletSinkStaticPropertyProvider.PAYLOAD_MAPPED_FIELD_KEY.equals(internalName)) {
          payloadMode = KameletMessageMapping.PayloadMode.MAPPED_FIELD;
          bodyFieldSelector = extractMappedBodyField(selectedAlternative.get().getStaticProperty());
        }
      }
    }

    return new KameletMessageMapping(
        payloadMode,
        bodyFieldSelector,
        extractHeaderMappings(extractor),
        extractTransformSteps(extractor)
    );
  }

  private String extractMappedBodyField(StaticProperty payloadProperty) {
    if (!(payloadProperty instanceof StaticPropertyGroup group)) {
      return null;
    }

    StaticProperty property = findGroupMember(group, KameletSinkStaticPropertyProvider.PAYLOAD_FIELD_MAPPING_KEY);
    if (property instanceof MappingPropertyUnary mappingProperty) {
      return mappingProperty.getSelectedProperty();
    }

    return null;
  }

  private List<KameletHeaderMapping> extractHeaderMappings(IParameterExtractor extractor) {
    CollectionStaticProperty collection = extractor.getStaticPropertyByName(
        KameletSinkStaticPropertyProvider.HEADER_MAPPINGS_KEY,
        CollectionStaticProperty.class
    );

    if (collection == null || collection.getMembers() == null) {
      return List.of();
    }

    List<KameletHeaderMapping> mappings = new ArrayList<>();

    for (StaticProperty member : collection.getMembers()) {
      if (!(member instanceof StaticPropertyGroup group)) {
        continue;
      }

      String headerName = extractHeaderName(group);
      String eventFieldSelector = extractHeaderFieldSelector(group);

      if (headerName != null && !headerName.isBlank() && eventFieldSelector != null && !eventFieldSelector.isBlank()) {
        mappings.add(new KameletHeaderMapping(headerName, eventFieldSelector));
      }
    }

    return mappings;
  }

  private String extractHeaderName(StaticPropertyGroup group) {
    StaticProperty property = findGroupMember(group, KameletSinkStaticPropertyProvider.HEADER_NAME_KEY);
    if (property instanceof FreeTextStaticProperty freeTextStaticProperty) {
      return freeTextStaticProperty.getValue();
    }

    return null;
  }

  private String extractHeaderFieldSelector(StaticPropertyGroup group) {
    StaticProperty property = findGroupMember(group, KameletSinkStaticPropertyProvider.HEADER_FIELD_MAPPING_KEY);
    if (property instanceof MappingPropertyUnary mappingProperty) {
      return mappingProperty.getSelectedProperty();
    }

    return null;
  }

  private String extractTransformSteps(IParameterExtractor extractor) {
    StaticPropertyAlternatives alternatives = extractor.getStaticPropertyByName(
        KameletSinkStaticPropertyProvider.ADVANCED_TRANSFORM_KEY,
        StaticPropertyAlternatives.class
    );

    if (alternatives == null) {
      return null;
    }

    Optional<StaticPropertyAlternative> selectedAlternative = getSelectedAlternative(alternatives);
    if (selectedAlternative.isEmpty()
        || !KameletSinkStaticPropertyProvider.ADVANCED_TRANSFORM_ENABLED_KEY.equals(
        selectedAlternative.get().getInternalName())) {
      return null;
    }

    if (!(selectedAlternative.get().getStaticProperty() instanceof StaticPropertyGroup group)) {
      return null;
    }

    StaticProperty property = findGroupMember(group, KameletSinkStaticPropertyProvider.ADVANCED_TRANSFORM_STEPS_KEY);
    if (property instanceof CodeInputStaticProperty codeInputStaticProperty) {
      return codeInputStaticProperty.getValue();
    }

    return null;
  }

  private Optional<StaticPropertyAlternative> getSelectedAlternative(StaticPropertyAlternatives alternatives) {
    return alternatives.getAlternatives()
        .stream()
        .filter(StaticPropertyAlternative::getSelected)
        .findFirst();
  }

  private StaticProperty findGroupMember(StaticPropertyGroup group,
                                         String internalName) {
    return group.getStaticProperties()
        .stream()
        .filter(property -> internalName.equals(property.getInternalName()))
        .findFirst()
        .orElse(null);
  }
}
