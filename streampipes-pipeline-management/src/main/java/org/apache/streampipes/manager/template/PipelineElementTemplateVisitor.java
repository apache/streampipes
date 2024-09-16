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
package org.apache.streampipes.manager.template;

import org.apache.streampipes.model.staticproperty.AnyStaticProperty;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.MatchingStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.model.staticproperty.StaticPropertyVisitor;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PipelineElementTemplateVisitor implements StaticPropertyVisitor {

  private final List<Map<String, Object>> configs;

  public PipelineElementTemplateVisitor(List<Map<String, Object>> configs) {
    this.configs = configs;
  }

  @Override
  public void visit(AnyStaticProperty property) {
    if (hasConfig(property)) {
      List<String> value = getConfigValueAsStringList(property);
      property.getOptions().forEach(option -> {
        option.setSelected(value.stream().anyMatch(v -> v.equals(option.getName())));
      });
    }
  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {
    if (hasConfig(codeInputStaticProperty)) {
      codeInputStaticProperty.setValue(getConfigValueAsString(codeInputStaticProperty));
    }
  }

  @Override
  public void visit(CollectionStaticProperty collectionStaticProperty) {
    if (hasConfig(collectionStaticProperty)) {
      List<Map<String, Object>> values = getConfigValueAsList(collectionStaticProperty);
      collectionStaticProperty.setMembers(new ArrayList<>());
      values.forEach(v -> {
        StaticProperty sp = new Cloner().staticProperty(collectionStaticProperty.getStaticPropertyTemplate());
        PipelineElementTemplateVisitor visitor = new PipelineElementTemplateVisitor(List.of(v));
        sp.accept(visitor);
        collectionStaticProperty.getMembers().add(sp);
      });
    }
  }

  @Override
  public void visit(ColorPickerStaticProperty colorPickerStaticProperty) {
    if (hasConfig(colorPickerStaticProperty)) {
      colorPickerStaticProperty.setSelectedColor(getConfigValueAsString(colorPickerStaticProperty));
    }
  }

  @Override
  public void visit(FileStaticProperty fileStaticProperty) {
    if (hasConfig(fileStaticProperty)) {
      fileStaticProperty.setLocationPath(getConfigValueAsString(fileStaticProperty));
    }
  }

  @Override
  public void visit(FreeTextStaticProperty freeTextStaticProperty) {
    if (hasConfig(freeTextStaticProperty)) {
      freeTextStaticProperty.setValue(getConfigValueAsString(freeTextStaticProperty));
    }
  }

  @Override
  public void visit(MappingPropertyNary mappingPropertyNary) {
    if (hasConfig(mappingPropertyNary)) {
      var selectedProperties = getConfigValueAsStringList(mappingPropertyNary);
      mappingPropertyNary.setSelectedProperties(selectedProperties);
    }
  }

  @Override
  public void visit(MappingPropertyUnary mappingPropertyUnary) {
    if (hasConfig(mappingPropertyUnary)) {
      var selectedProperty = getConfigValueAsString(mappingPropertyUnary);
      mappingPropertyUnary.setSelectedProperty(selectedProperty);
    }
  }

  @Override
  public void visit(MatchingStaticProperty matchingStaticProperty) {
    // Do nothing, not supported by pipeline element templates
  }

  @Override
  public void visit(OneOfStaticProperty oneOfStaticProperty) {
    if (hasConfig(oneOfStaticProperty)) {
      String value = getConfigValueAsString(oneOfStaticProperty);
      oneOfStaticProperty.getOptions().forEach(option ->
          option.setSelected(option.getName().equals(value)));
    }
  }

  @Override
  public void visit(SecretStaticProperty secretStaticProperty) {
    if (hasConfig(secretStaticProperty)) {
      Map<String, Object> values = getConfig(secretStaticProperty);
      boolean encrypted = values.containsKey("encrypted")
          && Boolean.parseBoolean(String.valueOf(values.get("encrypted")));
      String value = getConfigValueAsString(secretStaticProperty);
      if (encrypted) {
        secretStaticProperty.setValue(value);
      } else {
        String newValue = SecretEncryptionManager.encrypt(value);
        secretStaticProperty.setValue(newValue);
      }
      secretStaticProperty.setEncrypted(true);
    }
  }

  @Override
  public void visit(StaticPropertyAlternative staticPropertyAlternative) {
    StaticProperty property = staticPropertyAlternative.getStaticProperty();
    if (property != null) {
      PipelineElementTemplateVisitor visitor = new PipelineElementTemplateVisitor(configs);
      property.accept(visitor);
    }
  }

  @Override
  public void visit(StaticPropertyAlternatives staticPropertyAlternatives) {
    if (hasConfig(staticPropertyAlternatives)) {
      Map<String, Object> values = getConfig(staticPropertyAlternatives);
      var selectedId = getConfigValueAsString(staticPropertyAlternatives);
      staticPropertyAlternatives
          .getAlternatives()
          .stream()
          .filter(a -> a.getInternalName().equalsIgnoreCase(selectedId))
          .forEach(a -> {
            a.setSelected(true);
            PipelineElementTemplateVisitor visitor = new PipelineElementTemplateVisitor(List.of(values));
            a.accept(visitor);
          });
    }
  }

  @Override
  public void visit(StaticPropertyGroup staticPropertyGroup) {
    staticPropertyGroup.getStaticProperties().forEach(group -> {
      PipelineElementTemplateVisitor visitor =
          new PipelineElementTemplateVisitor(configs);
      group.accept(visitor);
    });
  }

  @Override
  public void visit(SlideToggleStaticProperty slideToggleStaticProperty) {
    if (hasConfig(slideToggleStaticProperty)) {
      slideToggleStaticProperty.setSelected(getConfigValueAsBoolean(slideToggleStaticProperty));
    }
  }

  @Override
  public void visit(RuntimeResolvableTreeInputStaticProperty property) {
    if (hasConfig(property)) {
      List<String> values = getConfigValueAsStringList(property);
      property.setSelectedNodesInternalNames(values);
    }
  }

  @Override
  public void visit(RuntimeResolvableGroupStaticProperty groupStaticProperty) {
    // TODO not yet supported
  }


  private Map<String, Object> getConfig(StaticProperty sp) {
    return getConfig(sp.getInternalName());
  }

  private Map<String, Object> getConfig(String key) {
    return configs
        .stream()
        .filter(f -> hasKeyCaseInsensitive(key, f))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(String.format("No key found: %s", key)));
  }

//  private List<Map<String, Object>> getConfigAsList(StaticProperty sp) {
//    return getConfig(sp).
//  }

  private boolean hasKeyCaseInsensitive(String internalName,
                                        Map<String, Object> templateConfig) {
    return templateConfig
        .entrySet()
        .stream()
        .anyMatch(entry -> entry.getKey().equalsIgnoreCase(internalName));
  }

  private boolean hasConfig(StaticProperty sp) {
    return configs.stream().anyMatch(c -> hasKeyCaseInsensitive(sp.getInternalName(), c));
  }

  private String getConfigValueAsString(StaticProperty sp) {
    var config = getConfig(sp);
    if (config.isEmpty()) {
      throw new IllegalArgumentException(String.format("Could not find config for %s", sp.getInternalName()));
    } else {
      var caseInsensitiveKey = getCaseInsensitiveKey(config, sp.getInternalName());
      return String.valueOf(config.get(caseInsensitiveKey));
    }
  }

  private boolean getConfigValueAsBoolean(StaticProperty sp) {
    return Boolean.parseBoolean(getConfigValueAsString(sp));
  }

  private List<String> getConfigValueAsStringList(StaticProperty sp) {
    return getValueAsStringList(getConfig(sp), sp.getInternalName());
  }

  private String getCaseInsensitiveKey(Map<String, Object> config,
                                       String key) {
    return config.keySet().stream()
        .filter(k -> k.equalsIgnoreCase(key))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Key not found: " + key));
  }

  private List<String> getValueAsStringList(Map<String, Object> config, String key) {
    String caseInsensitiveKey = getCaseInsensitiveKey(config, key);

    return Optional.ofNullable(config.get(caseInsensitiveKey))
        .filter(value -> value instanceof List<?>)
        .map(value -> ((List<?>) value).stream())
        .map(s -> s.map(String.class::cast).collect(Collectors.toList()))
        .orElseThrow(() -> new IllegalArgumentException("Value is not a List<String>"));
  }

  private List<Map<String, Object>> getConfigValueAsList(StaticProperty sp) {
    return configs
        .stream()
        .filter(f -> hasKeyCaseInsensitive(sp.getInternalName(), f))
        .findFirst()
        .map(f -> getCaseInsensitiveList(f, sp.getInternalName()))
        .orElseThrow(IllegalArgumentException::new);
  }

  private List<Map<String, Object>> getCaseInsensitiveList(Map<String, Object> map, String key) {
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(key)) {
        return (List<Map<String, Object>>) entry.getValue();
      }
    }
    throw new IllegalArgumentException("Key '" + key + "' not found");
  }


//  private String getAsString(StaticProperty sp) {
//    return configs.get(sp.getInternalName()).toString();
//  }
//
//  private boolean getAsBoolean(StaticProperty sp) {
//    return Boolean.parseBoolean(configs.get(sp.getInternalName()).toString());
//  }
//
//  private Map<String, Object> getAsMap(StaticProperty sp) {
//    return (Map<String, Object>) configs.get(sp.getInternalName());
//  }
//
//  private Map<String, Object> getAsMap(StaticProperty sp, String subkey) {
//    return (Map<String, Object>) getAsMap(sp).get(subkey);
//  }
//
//  private Map<String, Object> getAsMap(Map<String, Object> map, String key) {
//    return (Map<String, Object>) map.get(key);
//  }
//
//  private List<Map<String, Object>> getAsList(StaticProperty sp, String key) {
//    return (List<Map<String, Object>>) getAsMap(sp).get(key);
//  }
}
