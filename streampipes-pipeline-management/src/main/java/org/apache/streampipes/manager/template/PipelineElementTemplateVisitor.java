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

import org.apache.streampipes.model.staticproperty.*;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;

import java.util.List;
import java.util.Map;

public class PipelineElementTemplateVisitor implements StaticPropertyVisitor {

  private Map<String, PipelineElementTemplateConfig> configs;

  public PipelineElementTemplateVisitor(PipelineElementTemplate template) {
    this.configs = template.getTemplateConfigs();
  }

  @Override
  public void visit(AnyStaticProperty property) {
    if (hasKey(property)) {
      List<String> value = (List<String>) getValue(property);
      property.getOptions().forEach(option -> {
        option.setSelected(value.stream().anyMatch(v -> v.equals(option.getName())));
      });
    }
  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {

  }

  @Override
  public void visit(CollectionStaticProperty collectionStaticProperty) {

  }

  @Override
  public void visit(ColorPickerStaticProperty colorPickerStaticProperty) {
    if (hasKey(colorPickerStaticProperty)) {
      colorPickerStaticProperty.setSelectedColor(getStringValue(colorPickerStaticProperty));
    }
  }

  @Override
  public void visit(DomainStaticProperty domainStaticProperty) {

  }

  @Override
  public void visit(FileStaticProperty fileStaticProperty) {
    if (hasKey(fileStaticProperty)) {
      fileStaticProperty.setLocationPath(getStringValue(fileStaticProperty));
    }
  }

  @Override
  public void visit(FreeTextStaticProperty freeTextStaticProperty) {
    if (hasKey(freeTextStaticProperty)) {
      freeTextStaticProperty.setValue(getStringValue(freeTextStaticProperty));
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
      String value = getStringValue(oneOfStaticProperty);
      oneOfStaticProperty.getOptions().forEach(option -> {
        option.setSelected(option.getName().equals(value));
      });
    }
  }

  @Override
  public void visit(SecretStaticProperty secretStaticProperty) {

  }

  @Override
  public void visit(StaticPropertyAlternative staticPropertyAlternative) {

  }

  @Override
  public void visit(StaticPropertyAlternatives staticPropertyAlternatives) {

  }

  @Override
  public void visit(StaticPropertyGroup staticPropertyGroup) {

  }

  @Override
  public void visit(RemoteOneOfStaticProperty remoteOneOfStaticProperty) {

  }

  private String getStringValue(StaticProperty sp) {
    return String.valueOf(getValue(sp));
  }

  private Object getValue(StaticProperty sp) {
    return configs.get(sp.getInternalName()).getValue();
  }

  private boolean hasKey(StaticProperty sp) {
    return configs.containsKey(sp.getInternalName());
  }
}
