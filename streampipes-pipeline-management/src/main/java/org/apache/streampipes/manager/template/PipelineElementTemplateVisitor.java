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
import org.apache.streampipes.model.staticproperty.DomainStaticProperty;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.MatchingStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.RemoteOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.model.staticproperty.StaticPropertyVisitor;
import org.apache.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipelineElementTemplateVisitor implements StaticPropertyVisitor {

  private final Map<String, Object> configs;

  public PipelineElementTemplateVisitor(Map<String, Object> configs) {
    this.configs = configs;
  }

  @Override
  public void visit(AnyStaticProperty property) {
    if (hasKey(property)) {
      List<String> value = getValueAsList(property);
      property.getOptions().forEach(option -> {
        option.setSelected(value.stream().anyMatch(v -> v.equals(option.getName())));
      });
    }
  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {
    if (hasKey(codeInputStaticProperty)) {
      codeInputStaticProperty.setValue(getAsString(codeInputStaticProperty));
    }
  }

  @Override
  public void visit(CollectionStaticProperty collectionStaticProperty) {
    if (hasKey(collectionStaticProperty)) {
      List<Map<String, Object>> values = getAsList(collectionStaticProperty, "members");
      collectionStaticProperty.setMembers(new ArrayList<>());
      values.forEach(v -> {
        StaticProperty sp = new Cloner().staticProperty(collectionStaticProperty.getStaticPropertyTemplate());
        PipelineElementTemplateVisitor visitor = new PipelineElementTemplateVisitor(v);
        sp.accept(visitor);
        collectionStaticProperty.getMembers().add(sp);
      });
    }
  }

  @Override
  public void visit(ColorPickerStaticProperty colorPickerStaticProperty) {
    if (hasKey(colorPickerStaticProperty)) {
      colorPickerStaticProperty.setSelectedColor(getAsString(colorPickerStaticProperty));
    }
  }

  @Override
  public void visit(DomainStaticProperty domainStaticProperty) {
    // TODO - not used anywhere anymore
  }

  @Override
  public void visit(FileStaticProperty fileStaticProperty) {
    if (hasKey(fileStaticProperty)) {
      fileStaticProperty.setLocationPath(getAsString(fileStaticProperty));
    }
  }

  @Override
  public void visit(FreeTextStaticProperty freeTextStaticProperty) {
    if (hasKey(freeTextStaticProperty)) {
      freeTextStaticProperty.setValue(getAsString(freeTextStaticProperty));
    }
  }

  @Override
  public void visit(MappingPropertyNary mappingPropertyNary) {
    // Do nothing, not supported by pipeline element templates
  }

  @Override
  public void visit(MappingPropertyUnary mappingPropertyUnary) {
    // Do nothing, not supported by pipeline element templates
  }

  @Override
  public void visit(MatchingStaticProperty matchingStaticProperty) {
    // Do nothing, not supported by pipeline element templates
  }

  @Override
  public void visit(OneOfStaticProperty oneOfStaticProperty) {
    if (hasKey(oneOfStaticProperty)) {
      String value = getAsString(oneOfStaticProperty);
      oneOfStaticProperty.getOptions().forEach(option ->
          option.setSelected(option.getName().equals(value)));
    }
  }

  @Override
  public void visit(SecretStaticProperty secretStaticProperty) {
    if (hasKey(secretStaticProperty)) {
      secretStaticProperty.setEncrypted(true);
      secretStaticProperty.setValue(getAsString(secretStaticProperty));
    }
  }

  @Override
  public void visit(StaticPropertyAlternative staticPropertyAlternative) {
    if (hasKey(staticPropertyAlternative)) {
      Map<String, Object> values = getAsMap(staticPropertyAlternative);
      StaticProperty property = staticPropertyAlternative.getStaticProperty();
      staticPropertyAlternative.setSelected(Boolean.parseBoolean(String.valueOf(values.get("selected"))));
      if (property != null) {
        PipelineElementTemplateVisitor visitor = new PipelineElementTemplateVisitor(getAsMap(values, "staticProperty"));
        property.accept(visitor);
      }
    }
  }

  @Override
  public void visit(StaticPropertyAlternatives staticPropertyAlternatives) {
    if (hasKey(staticPropertyAlternatives)) {
      Map<String, Object> values = getAsMap(staticPropertyAlternatives, "alternatives");
      staticPropertyAlternatives.getAlternatives().forEach((alternative) -> {
        PipelineElementTemplateVisitor visitor = new PipelineElementTemplateVisitor(values);
        alternative.accept(visitor);
      });
    }
  }

  @Override
  public void visit(StaticPropertyGroup staticPropertyGroup) {
    if (hasKey(staticPropertyGroup)) {
      Map<String, Object> values = getAsMap(staticPropertyGroup);
      staticPropertyGroup.getStaticProperties().forEach((group) -> {
        PipelineElementTemplateVisitor visitor =
            new PipelineElementTemplateVisitor(getAsMap(values, "staticProperties"));
        group.accept(visitor);
      });
    }
  }

  @Override
  public void visit(RemoteOneOfStaticProperty remoteOneOfStaticProperty) {

  }

  @Override
  public void visit(SlideToggleStaticProperty slideToggleStaticProperty) {
    if (hasKey(slideToggleStaticProperty)) {
      slideToggleStaticProperty.setSelected(getAsBoolean(slideToggleStaticProperty));
    }
  }

  @Override
  public void visit(RuntimeResolvableTreeInputStaticProperty treeInputStaticProperty) {
    // TODO support templates for tree input
  }

  private Object getValue(StaticProperty sp) {
    return ((Map<String, Object>) configs.get(sp.getInternalName())).get("value");
  }

  private List<String> getValueAsList(StaticProperty sp) {
    return (List<String>) configs.get(sp.getInternalName());
  }

  private boolean hasKey(StaticProperty sp) {
    return configs.containsKey(sp.getInternalName());
  }

  private String getAsString(StaticProperty sp) {
    return configs.get(sp.getInternalName()).toString();
  }

  private boolean getAsBoolean(StaticProperty sp) {
    return Boolean.parseBoolean(configs.get(sp.getInternalName()).toString());
  }

  private Map<String, Object> getAsMap(StaticProperty sp) {
    return (Map<String, Object>) configs.get(sp.getInternalName());
  }

  private Map<String, Object> getAsMap(StaticProperty sp, String subkey) {
    return (Map<String, Object>) getAsMap(sp).get(subkey);
  }

  private Map<String, Object> getAsMap(Map<String, Object> map, String key) {
    return (Map<String, Object>) map.get(key);
  }

  private List<Map<String, Object>> getAsList(StaticProperty sp, String key) {
    return (List<Map<String, Object>>) getAsMap(sp).get(key);
  }
}
