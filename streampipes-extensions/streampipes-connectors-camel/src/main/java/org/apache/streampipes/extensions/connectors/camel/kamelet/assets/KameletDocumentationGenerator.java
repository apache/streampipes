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

package org.apache.streampipes.extensions.connectors.camel.kamelet.assets;

import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletPropertyDefinition;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class KameletDocumentationGenerator {

  public byte[] generateDocumentation(KameletTemplate template) {
    StringBuilder builder = new StringBuilder();
    builder.append("# ").append(template.displayName()).append("\n\n");

    if (!template.description().isBlank()) {
      builder.append(template.description()).append("\n\n");
    }

    builder.append("- Kamelet name: `").append(template.name()).append("`\n");
    builder.append("- Endpoint: `").append(template.endpointBaseUri()).append("`\n");
    if (!template.group().isBlank()) {
      builder.append("- Group: `").append(template.group()).append("`\n");
    }

    if (template.properties().isEmpty()) {
      builder.append("\n## Parameters\n\n");
      builder.append("This Kamelet exposes no configuration parameters.\n");
    } else {
      appendParameterSection(builder, "Required Parameters", template.properties().stream()
          .filter(KameletPropertyDefinition::required)
          .toList());
      appendParameterSection(builder, "Optional Parameters", template.properties().stream()
          .filter(property -> !property.required())
          .toList());
    }

    builder.append("\n## YAML Spec\n\n");
    builder.append("```yaml\n");
    builder.append(template.yamlSpec());
    builder.append("\n```\n");

    return builder.toString().getBytes(StandardCharsets.UTF_8);
  }

  private void appendParameterSection(StringBuilder builder,
                                      String title,
                                      List<KameletPropertyDefinition> properties) {
    if (properties.isEmpty()) {
      return;
    }

    builder.append("\n## ").append(title).append("\n\n");
    for (KameletPropertyDefinition property : properties) {
      builder.append("- `").append(property.name()).append("`");
      builder.append(" (`").append(property.datatype().name()).append("`, ");
      builder.append(property.inputType().name()).append(")");

      if (property.defaultValue() != null && !property.defaultValue().isBlank()) {
        builder.append(", default `").append(escapeInline(property.defaultValue())).append("`");
      }

      if (!property.allowedValues().isEmpty()) {
        builder.append(", allowed: ");
        builder.append(property.allowedValues().stream()
            .map(this::escapeInline)
            .map(value -> "`" + value + "`")
            .collect(Collectors.joining(", ")));
      }

      builder.append("\n");
      builder.append("  ");
      builder.append(escapeText(property.description().isBlank() ? property.displayLabel() : property.description()));
      builder.append("\n");
    }
  }

  private String escapeInline(String value) {
    return value == null ? "" : value.replace("`", "\\`").replace("\n", " ");
  }

  private String escapeText(String value) {
    return value == null ? "" : value.replace("\n", " ").trim();
  }
}
