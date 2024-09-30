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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CheckCompletedVisitor extends DefaultStaticPropertyVisitor {

  private final List<PipelineElementValidationInfo> validationInfos;

  public CheckCompletedVisitor() {
    this.validationInfos = new ArrayList<>();
  }

  @Override
  public void visit(AnyStaticProperty property) {

  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {
    if (!codeInputStaticProperty.isOptional() && Objects.isNull(codeInputStaticProperty.getValue())) {
      addMissingConfiguration(codeInputStaticProperty);
    }
  }

  @Override
  public void visit(ColorPickerStaticProperty colorPickerStaticProperty) {
    if (!colorPickerStaticProperty.isOptional() && Objects.isNull(colorPickerStaticProperty.getSelectedColor())) {
      addMissingConfiguration(colorPickerStaticProperty);
    }
  }

  @Override
  public void visit(FileStaticProperty fileStaticProperty) {
    if (!fileStaticProperty.isOptional() && Objects.isNull(fileStaticProperty.getLocationPath())) {
      addMissingConfiguration(fileStaticProperty);
    }
  }

  @Override
  public void visit(FreeTextStaticProperty freeTextStaticProperty) {
    if (!freeTextStaticProperty.isOptional() && Objects.isNull(freeTextStaticProperty.getValue())) {
      addMissingConfiguration(freeTextStaticProperty);
    }
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
      var info = PipelineElementValidationInfo.info(
          String.format(
              "Auto-updated invalid field selection: Fields updated to %s",
              mappingPropertyNary.getSelectedProperties().toString()
          )
      );
      validationInfos.add(info);
    }
  }

  @Override
  public void visit(MappingPropertyUnary mappingPropertyUnary) {
    if (existsSelection(mappingPropertyUnary)) {
      if (!(mappingPropertyUnary.getMapsFromOptions().contains(mappingPropertyUnary.getSelectedProperty()))) {
        if (!mappingPropertyUnary.getMapsFromOptions().isEmpty()) {
          String existingSelector = mappingPropertyUnary.getSelectedProperty();
          String firstSelector = mappingPropertyUnary.getMapsFromOptions().get(0);
          mappingPropertyUnary.setSelectedProperty(firstSelector);
          var info = PipelineElementValidationInfo.info(
              String.format(
                  "Auto-updated invalid field selection: Selected field %s was changed to %s",
                  existingSelector,
                  firstSelector
              )
          );
          validationInfos.add(info);
        }
      }
    } else {
      if (!mappingPropertyUnary.getMapsFromOptions().isEmpty()) {
        String firstSelector = mappingPropertyUnary.getMapsFromOptions().get(0);
        mappingPropertyUnary.setSelectedProperty(firstSelector);
      }
    }
  }

  @Override
  public void visit(MatchingStaticProperty matchingStaticProperty) {

  }

  @Override
  public void visit(OneOfStaticProperty oneOfStaticProperty) {
    if (oneOfStaticProperty.getOptions().stream().noneMatch(Option::isSelected)) {
      validationInfos.add(PipelineElementValidationInfo.error(
          String.format(
              "Configuration \"%s\" must have one selected option, but no option was selected.",
              oneOfStaticProperty.getInternalName()
          )
      ));
    }
  }

  @Override
  public void visit(SecretStaticProperty secretStaticProperty) {
    if (!secretStaticProperty.isOptional() && Objects.isNull(secretStaticProperty.getValue())) {
      addMissingConfiguration(secretStaticProperty);
    }
  }

  @Override
  public void visit(SlideToggleStaticProperty slideToggleStaticProperty) {

  }

  @Override
  public void visit(RuntimeResolvableTreeInputStaticProperty treeInputStaticProperty) {
    if (!treeInputStaticProperty.isOptional() && treeInputStaticProperty.getSelectedNodesInternalNames().isEmpty()) {
      addMissingConfiguration(treeInputStaticProperty);
    }
  }

  @Override
  public void visit(RuntimeResolvableGroupStaticProperty groupStaticProperty) {
  }

  @Override
  public void visit(StaticPropertyAlternatives staticPropertyAlternatives) {
    if (!staticPropertyAlternatives.isOptional()
        && staticPropertyAlternatives.getAlternatives().stream().noneMatch(StaticPropertyAlternative::getSelected)) {
      validationInfos.add(PipelineElementValidationInfo.error(
          String.format(
              "No alternative of configuration \"%s\" was selected, but at least one alternative must be chosen",
              staticPropertyAlternatives.getInternalName()
          )
      ));
    }
    super.visit(staticPropertyAlternatives);
  }

  public List<PipelineElementValidationInfo> getValidationInfos() {
    return this.validationInfos;
  }

  private boolean existsSelection(MappingPropertyUnary mappingProperty) {
    return !(mappingProperty.getSelectedProperty() == null || mappingProperty.getSelectedProperty().isEmpty());
  }

  private boolean existsSelection(MappingPropertyNary mappingProperty) {
    return !(mappingProperty.getSelectedProperties() == null || mappingProperty.getSelectedProperties().isEmpty());
  }

  private void addMissingConfiguration(StaticProperty sp) {
    validationInfos.add(
        PipelineElementValidationInfo.error(
            String.format(
                "Configuration option \"%s\" as no value although it is marked as required",
                sp.getInternalName()
            )
        )
    );
  }
}
