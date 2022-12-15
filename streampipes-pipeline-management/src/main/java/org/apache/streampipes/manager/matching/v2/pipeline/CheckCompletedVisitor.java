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

package org.apache.streampipes.manager.matching.v2.pipeline;

import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.staticproperty.AnyStaticProperty;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty;
import org.apache.streampipes.model.staticproperty.DefaultStaticPropertyVisitor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CheckCompletedVisitor extends DefaultStaticPropertyVisitor {

  private List<PipelineElementValidationInfo> validationInfos;

  public CheckCompletedVisitor() {
    this.validationInfos = new ArrayList<>();
  }

  @Override
  public void visit(AnyStaticProperty property) {

  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {
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

  }

  @Override
  public void visit(MappingPropertyNary mappingPropertyNary) {
    if (existsSelection(mappingPropertyNary) && mappingPropertyNary
        .getSelectedProperties()
        .stream()
        .noneMatch((p -> mappingPropertyNary.getMapsFromOptions().contains(p)))) {
      mappingPropertyNary.setSelectedProperties(mappingPropertyNary
          .getSelectedProperties()
          .stream()
          .filter(p -> mappingPropertyNary.getMapsFromOptions().contains(p))
          .collect(Collectors.toList()));
      validationInfos.add(PipelineElementValidationInfo.info("Auto-updated invalid field selection"));
    }
  }

  @Override
  public void visit(MappingPropertyUnary mappingPropertyUnary) {
    if (existsSelection(mappingPropertyUnary)) {
      if (!(mappingPropertyUnary.getMapsFromOptions().contains(mappingPropertyUnary.getSelectedProperty()))) {
        if (mappingPropertyUnary.getMapsFromOptions().size() > 0) {
          String firstSelector = mappingPropertyUnary.getMapsFromOptions().get(0);
          mappingPropertyUnary.setSelectedProperty(firstSelector);
          validationInfos.add(PipelineElementValidationInfo.info("Auto-updated invalid field selection"));
        }
      }
    } else {
      String firstSelector = mappingPropertyUnary.getMapsFromOptions().get(0);
      mappingPropertyUnary.setSelectedProperty(firstSelector);
    }
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
  public void visit(RemoteOneOfStaticProperty remoteOneOfStaticProperty) {

  }

  @Override
  public void visit(SlideToggleStaticProperty slideToggleStaticProperty) {

  }

  @Override
  public void visit(RuntimeResolvableTreeInputStaticProperty treeInputStaticProperty) {

  }

  public List<PipelineElementValidationInfo> getValidationInfos() {
    return this.validationInfos;
  }

  private boolean existsSelection(MappingPropertyUnary mappingProperty) {
    return !(mappingProperty.getSelectedProperty() == null || mappingProperty.getSelectedProperty().equals(""));
  }

  private boolean existsSelection(MappingPropertyNary mappingProperty) {
    return !(mappingProperty.getSelectedProperties() == null || mappingProperty.getSelectedProperties().size() == 0);
  }
}
