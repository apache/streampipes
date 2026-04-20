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

import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.List;

public class KameletSinkStaticPropertyProvider {

  public static final String KAMELET_PARAMETERS_GROUP_KEY = "camel-kamelet-parameters-group";

  public static final String MESSAGE_MAPPING_GROUP_KEY = "camel-message-mapping-group";
  public static final String PAYLOAD_GROUP_KEY = "camel-payload-group";
  public static final String PAYLOAD_ALTERNATIVES_KEY = "camel-payload-mode";
  public static final String PAYLOAD_EVENT_MAP_KEY = "camel-payload-event-map";
  public static final String PAYLOAD_EVENT_JSON_KEY = "camel-payload-event-json";
  public static final String PAYLOAD_MAPPED_FIELD_KEY = "camel-payload-mapped-field";
  public static final String PAYLOAD_FIELD_MAPPING_KEY = "camel-payload-field";
  public static final String HEADER_MAPPINGS_KEY = "camel-header-mappings";
  public static final String HEADER_MAPPING_GROUP_KEY = "camel-header-mapping-group";
  public static final String HEADER_NAME_KEY = "camel-header-name";
  public static final String HEADER_FIELD_MAPPING_KEY = "camel-header-field";

  public static final String ADVANCED_TRANSFORM_KEY = "camel-advanced-transform";
  public static final String ADVANCED_TRANSFORM_DISABLED_KEY = "camel-advanced-transform-disabled";
  public static final String ADVANCED_TRANSFORM_ENABLED_KEY = "camel-advanced-transform-enabled";
  public static final String ADVANCED_TRANSFORM_GROUP_KEY = "camel-advanced-transform-group";
  public static final String ADVANCED_TRANSFORM_STEPS_KEY = "camel-advanced-transform-steps";

  public RuntimeResolvableGroupStaticProperty createKameletParameterGroup() {
    var group = StaticProperties.runtimeResolvableGroupStaticProperty(
        Labels.from(
            KAMELET_PARAMETERS_GROUP_KEY,
            "Parameters",
            "Configure the Apache Camel Kamelet parameters."
        ),
        List.of()
    );
    group.setHorizontalRendering(false);
    return group;
  }

  public StaticPropertyGroup createMessageMappingGroup() {
    StaticPropertyGroup group = StaticProperties.group(
        Labels.from(
            MESSAGE_MAPPING_GROUP_KEY,
            "Message Mapping",
            "Control how the StreamPipes event is exposed to Camel before the Kamelet is invoked."
        ),
        false,
        createPayloadAlternatives(),
        createHeaderMappingsCollection()
    );
    group.setHorizontalRendering(false);
    return group;
  }

  public StaticPropertyAlternatives createAdvancedTransformAlternatives() {
    return StaticProperties.alternatives(
        Labels.from(
            ADVANCED_TRANSFORM_KEY,
            "Advanced Transform",
            "Optionally apply custom Camel YAML steps before the Kamelet sink endpoint."
        ),
        List.of(
            alternative(
                ADVANCED_TRANSFORM_DISABLED_KEY,
                "Disabled",
                "Use only the structured Kamelet mapping configuration.",
                true,
                emptyGroup("camel-advanced-transform-disabled-group")
            ),
            alternative(
                ADVANCED_TRANSFORM_ENABLED_KEY,
                "Custom YAML Steps",
                "Apply additional Camel YAML steps before forwarding the message to the Kamelet.",
                false,
                advancedTransformGroup()
            )
        )
    );
  }

  private StaticPropertyAlternatives createPayloadAlternatives() {
    return StaticProperties.alternatives(
        Labels.from(
            PAYLOAD_ALTERNATIVES_KEY,
            "Payload",
            "Choose how the input event is exposed as the Camel message body."
        ),
        List.of(
            alternative(
                PAYLOAD_EVENT_MAP_KEY,
                "Full Event Map",
                "Send the entire input event as a map body.",
                true,
                emptyGroup(PAYLOAD_EVENT_MAP_KEY + "-group")
            ),
            alternative(
                PAYLOAD_EVENT_JSON_KEY,
                "Full Event JSON",
                "Send the entire input event as a JSON string body.",
                false,
                emptyGroup(PAYLOAD_EVENT_JSON_KEY + "-group")
            ),
            alternative(
                PAYLOAD_MAPPED_FIELD_KEY,
                "Mapped Field",
                "Use one selected input field as the Camel message body.",
                false,
                mappedFieldPayloadGroup()
            )
        )
    );
  }

  private StaticPropertyGroup mappedFieldPayloadGroup() {
    MappingPropertyUnary bodyField = createUnaryMappingWithoutRequirement(
        PAYLOAD_FIELD_MAPPING_KEY,
        "Body Field",
        "Select the input event field that should become the Camel message body."
    );

    StaticPropertyGroup group = StaticProperties.group(
        Labels.from(PAYLOAD_GROUP_KEY, "", ""),
        false,
        bodyField
    );
    group.setHorizontalRendering(false);
    return group;
  }

  private CollectionStaticProperty createHeaderMappingsCollection() {
    StaticPropertyGroup headerMappingTemplate = StaticProperties.group(
        Labels.from(
            HEADER_MAPPING_GROUP_KEY,
            "Header Mapping",
            "Map one input field to one Camel header."
        ),
        false,
        StaticProperties.stringFreeTextProperty(
            Labels.from(
                HEADER_NAME_KEY,
                "Header Name",
                "The Camel header name to set."
            )
        ),
        createUnaryMappingWithoutRequirement(
            HEADER_FIELD_MAPPING_KEY,
            "Event Field",
            "Select the input event field to map to this Camel header."
        )
    );
    headerMappingTemplate.setHorizontalRendering(false);

    return new CollectionStaticProperty(
        HEADER_MAPPINGS_KEY,
        "Header Mappings",
        "Optionally project selected input fields into Camel headers.",
        headerMappingTemplate
    );
  }

  private StaticPropertyGroup advancedTransformGroup() {
    CodeInputStaticProperty codeBlock = StaticProperties.codeStaticProperty(
        Labels.from(
            ADVANCED_TRANSFORM_STEPS_KEY,
            "Transform Steps (YAML)",
            "Provide a Camel YAML steps fragment that runs before the message is sent to the Kamelet."
        ),
        CodeLanguage.None,
        """
            - setHeader:
                name: example
                simple: "${body[myField]}"
            """
    );
    codeBlock.setLanguage("yaml");

    StaticPropertyGroup group = StaticProperties.group(
        Labels.from(ADVANCED_TRANSFORM_GROUP_KEY, "", ""),
        false,
        codeBlock
    );
    group.setHorizontalRendering(false);
    return group;
  }

  private StaticPropertyAlternative alternative(String internalName,
                                                String label,
                                                String description,
                                                boolean selected,
                                                StaticProperty property) {
    StaticPropertyAlternative alternative = new StaticPropertyAlternative(internalName, label, description);
    alternative.setSelected(selected);
    alternative.setStaticProperty(property);
    return alternative;
  }

  private StaticPropertyGroup emptyGroup(String internalName) {
    StaticPropertyGroup group = StaticProperties.group(Labels.from(internalName, "", ""), false);
    group.setHorizontalRendering(false);
    return group;
  }

  private MappingPropertyUnary createUnaryMappingWithoutRequirement(String internalName,
                                                                    String label,
                                                                    String description) {
    MappingPropertyUnary mappingProperty = new MappingPropertyUnary(internalName, label, description);
    mappingProperty.setPropertyScope(PropertyScope.NONE.name());
    return mappingProperty;
  }
}
