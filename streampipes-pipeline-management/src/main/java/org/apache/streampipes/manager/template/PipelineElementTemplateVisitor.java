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

import java.util.Map;

public class PipelineElementTemplateVisitor implements StaticPropertyVisitor {

  private Map<String, PipelineElementTemplateConfig> configs;

  public PipelineElementTemplateVisitor(PipelineElementTemplate template) {
    this.configs = template.getTemplateConfigs();
  }

  @Override
  public void visit(AnyStaticProperty property) {

  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {

  }

  @Override
  public void visit(CollectionStaticProperty collectionStaticProperty) {

  }

  @Override
  public void visit(ColorPickerStaticProperty colorPickerStaticProperty) {

  }

  @Override
  public void visit(DomainStaticProperty domainStaticProperty) {

  }

  @Override
  public void visit(FileStaticProperty fileStaticProperty) {

  }

  @Override
  public void visit(FreeTextStaticProperty freeTextStaticProperty) {
    if (hasKey(freeTextStaticProperty.getInternalName())) {
      freeTextStaticProperty.setValue(String.valueOf(getValue(freeTextStaticProperty.getInternalName())));
    }
  }

  @Override
  public void visit(MappingPropertyNary mappingPropertyNary) {

  }

  @Override
  public void visit(MappingPropertyUnary mappingPropertyUnary) {

  }

  @Override
  public void visit(MatchingStaticProperty matchingStaticProperty) {

  }

  @Override
  public void visit(OneOfStaticProperty oneOfStaticProperty) {

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

  private Object getValue(String internalName) {
    return configs.get(internalName).getValue();
  }

  private boolean hasKey(String internalName) {
    return configs.containsKey(internalName);
  }
}
