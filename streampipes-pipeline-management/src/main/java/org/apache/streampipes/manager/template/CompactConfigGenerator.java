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
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.model.staticproperty.StaticPropertyVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompactConfigGenerator implements StaticPropertyVisitor {

  private final Map<String, Object> config;
  private final StaticProperty staticProperty;

  public CompactConfigGenerator(StaticProperty staticProperty) {
    this.config = new HashMap<>();
    this.staticProperty = staticProperty;
  }

  public Map<String, Object> toTemplateValue() {
    staticProperty.accept(this);
    return config;
  }

  @Override
  public void visit(AnyStaticProperty property) {
    addConfig(
        property,
        property.getOptions().stream().filter(Option::isSelected).map(Option::getName).toList()
    );
  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {
    addConfig(
        codeInputStaticProperty,
        codeInputStaticProperty.getValue()
    );
  }

  @Override
  public void visit(CollectionStaticProperty collectionStaticProperty) {
    config.put(
        collectionStaticProperty.getInternalName(),
        addListEntry(collectionStaticProperty.getMembers())
    );
  }

  @Override
  public void visit(ColorPickerStaticProperty colorPickerStaticProperty) {
    addConfig(
        colorPickerStaticProperty,
        colorPickerStaticProperty.getSelectedColor()
    );
  }

  @Override
  public void visit(FileStaticProperty fileStaticProperty) {
    addConfig(
        fileStaticProperty,
        fileStaticProperty.getLocationPath()
    );
  }

  @Override
  public void visit(FreeTextStaticProperty freeTextStaticProperty) {
    addConfig(
        freeTextStaticProperty,
        freeTextStaticProperty.getValue()
    );
  }

  @Override
  public void visit(MappingPropertyNary mappingPropertyNary) {
    addConfig(
        mappingPropertyNary,
        mappingPropertyNary.getSelectedProperties()
    );
  }

  @Override
  public void visit(MappingPropertyUnary mappingPropertyUnary) {
    addConfig(
        mappingPropertyUnary,
        mappingPropertyUnary.getSelectedProperty()
    );
  }

  @Override
  public void visit(MatchingStaticProperty matchingStaticProperty) {
    // not supported
  }

  @Override
  public void visit(OneOfStaticProperty oneOfStaticProperty) {
    addConfig(
        oneOfStaticProperty,
        oneOfStaticProperty.getOptions().stream().filter(Option::isSelected).findFirst().map(Option::getName)
    );
  }

  @Override
  public void visit(SecretStaticProperty secretStaticProperty) {
    config.put(secretStaticProperty.getInternalName(), secretStaticProperty.getValue());
    config.put("encrypted", secretStaticProperty.getEncrypted());
  }

  @Override
  public void visit(StaticPropertyAlternative staticPropertyAlternative) {

  }

  @Override
  public void visit(StaticPropertyAlternatives staticPropertyAlternatives) {
    var selectedAlternativeOpt = staticPropertyAlternatives.getAlternatives()
        .stream()
        .filter(StaticPropertyAlternative::getSelected)
        .findFirst();
    if (selectedAlternativeOpt.isPresent()) {
      var selectedAlternative = selectedAlternativeOpt.get();
      config.put(staticPropertyAlternatives.getInternalName(), selectedAlternative.getInternalName());
      if (selectedAlternative.getStaticProperty() != null) {
        var alternative = new CompactConfigGenerator(selectedAlternative.getStaticProperty()).toTemplateValue();
        config.putAll(alternative);
      }
    }
  }

  @Override
  public void visit(StaticPropertyGroup staticPropertyGroup) {
    config.putAll(addNestedEntry(staticPropertyGroup.getStaticProperties()));
  }

  @Override
  public void visit(SlideToggleStaticProperty slideToggleStaticProperty) {
    addConfig(
        slideToggleStaticProperty,
        slideToggleStaticProperty.isSelected()
    );
  }

  @Override
  public void visit(RuntimeResolvableTreeInputStaticProperty treeInputStaticProperty) {
    addConfig(
        treeInputStaticProperty,
        treeInputStaticProperty.getSelectedNodesInternalNames()
    );
  }

  @Override
  public void visit(RuntimeResolvableGroupStaticProperty groupStaticProperty) {

  }

  private void addConfig(StaticProperty staticProperty, Object value) {
    config.put(staticProperty.getInternalName(), value);
  }

  public List<Map<String, Object>> addListEntry(List<StaticProperty> staticProperties) {
    return staticProperties.stream()
        .map(sp -> new CompactConfigGenerator(sp).toTemplateValue())
        .toList();
  }

  public Map<String, Object> addNestedEntry(List<StaticProperty> staticProperties) {
    Map<String, Object> entry = new HashMap<>();

    staticProperties.forEach(sp -> {
      Map<String, Object> groupEntries = new CompactConfigGenerator(sp).toTemplateValue();
      entry.putAll(groupEntries);
    });

    return entry;
  }
}
