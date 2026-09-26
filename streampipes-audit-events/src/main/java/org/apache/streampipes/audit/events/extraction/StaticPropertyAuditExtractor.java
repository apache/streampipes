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

package org.apache.streampipes.audit.events.extraction;

import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.MatchingStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SelectionStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

/** Extracts effective configuration values, never presentation metadata or whole property objects. */
public final class StaticPropertyAuditExtractor {
  private final UnaryOperator<String> decryptSecret;

  public StaticPropertyAuditExtractor(UnaryOperator<String> decryptSecret) {
    this.decryptSecret = Objects.requireNonNull(decryptSecret);
  }

  /** Returns comparison values, including decrypted secrets; redact secret values before recording them. */
  public Map<String, Value> extract(List<StaticProperty> properties) {
    var values = new LinkedHashMap<String, Value>();
    extractProperties(properties, "", values);
    return values;
  }

  private void extractProperties(List<StaticProperty> properties, String prefix, Map<String, Value> values) {
    if (properties != null) {
      for (int i = 0; i < properties.size(); i++) {
        var property = properties.get(i);
        String name = property.getInternalName();
        String path = prefix + (name == null || name.isBlank() ? "property[" + i + "]" : name);
        extractProperty(property, path, values);
      }
    }
  }

  private void extractProperty(StaticProperty property, String path, Map<String, Value> values) {
    switch (property) {
      case StaticPropertyGroup group -> extractProperties(group.getStaticProperties(), path + ".", values);
      case CollectionStaticProperty collection -> {
        if (collection.getMembers() != null) {
          for (int i = 0; i < collection.getMembers().size(); i++) {
            extractProperty(collection.getMembers().get(i), path + "[" + i + "]", values);
          }
        }
      }
      case StaticPropertyAlternatives alternatives -> {
        var selected = alternatives.getAlternatives() == null ? List.<StaticPropertyAlternative>of()
            : alternatives.getAlternatives().stream().filter(a -> Boolean.TRUE.equals(a.getSelected())).toList();
        put(values, path, selected.stream().map(a -> alternativeName(a)).sorted().toList(), false);
        for (var alternative : selected) {
          if (alternative.getStaticProperty() != null) {
            extractProperties(List.of(alternative.getStaticProperty()), path + "." + alternativeName(alternative) + ".",
                values);
          }
        }
      }
      case StaticPropertyAlternative alternative -> {
        put(values, path, alternative.getSelected(), false);
        if (Boolean.TRUE.equals(alternative.getSelected()) && alternative.getStaticProperty() != null) {
          extractProperties(List.of(alternative.getStaticProperty()), path + ".", values);
        }
      }
      case SecretStaticProperty secret -> {
        String value = secret.getValue();
        if (Boolean.TRUE.equals(secret.getEncrypted()) && value != null) {
          value = decryptSecret.apply(value);
        }
        put(values, path, value, true);
      }
      case OneOfStaticProperty selection -> {
        var selected = selectedOptions(selection);
        put(values, path, selected.isEmpty() ? null : selected.size() == 1 ? selected.getFirst() : selected, false);
      }
      case SelectionStaticProperty selection -> put(values, path, selectedOptions(selection), false);
      case FreeTextStaticProperty text -> put(values, path, text.getValue(), false);
      case CodeInputStaticProperty code -> put(values, path, code.getValue(), false);
      case ColorPickerStaticProperty color -> put(values, path, color.getSelectedColor(), false);
      case SlideToggleStaticProperty toggle -> put(values, path, toggle.isSelected(), false);
      case MappingPropertyUnary mapping -> put(values, path, mapping.getSelectedProperty(), false);
      case MappingPropertyNary mapping -> put(values, path, mapping.getSelectedProperties(), false);
      case RuntimeResolvableTreeInputStaticProperty tree -> put(values, path, tree.getSelectedNodesInternalNames(), false);
      case FileStaticProperty file -> put(values, path, file.getLocationPath(), false);
      case MatchingStaticProperty matching -> {
        var pair = new LinkedHashMap<String, String>();
        pair.put("left", matching.getMatchLeft() == null ? null : matching.getMatchLeft().toString());
        pair.put("right", matching.getMatchRight() == null ? null : matching.getMatchRight().toString());
        put(values, path, pair, false);
      }
      default -> throw new IllegalArgumentException("Unsupported audit static property type");
    }
  }

  private List<String> selectedOptions(SelectionStaticProperty property) {
    return property.getOptions() == null ? List.of() : property.getOptions().stream()
        .filter(Option::isSelected)
        .map(option -> option.getInternalName() == null || option.getInternalName().isBlank()
            ? option.getName() : option.getInternalName())
        .sorted().toList();
  }

  private String alternativeName(StaticPropertyAlternative alternative) {
    return alternative.getInternalName() == null || alternative.getInternalName().isBlank()
        ? alternative.getLabel() : alternative.getInternalName();
  }

  private void put(Map<String, Value> values, String path, Object value, boolean secret) {
    if (values.putIfAbsent(path, new Value(value, secret)) != null) {
      throw new IllegalArgumentException("Duplicate audit static property name");
    }
  }

  /** Comparison-only value. Never serialize this record directly into audit details. */
  public record Value(Object value, boolean secret) {
  }
}
