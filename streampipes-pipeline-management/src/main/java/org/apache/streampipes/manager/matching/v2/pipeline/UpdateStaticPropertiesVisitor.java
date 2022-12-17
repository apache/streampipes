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

import org.apache.streampipes.manager.matching.mapping.AbstractRequirementsSelectorGenerator;
import org.apache.streampipes.manager.matching.mapping.RequirementsSelectorGeneratorFactory;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.staticproperty.AnyStaticProperty;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty;
import org.apache.streampipes.model.staticproperty.DefaultStaticPropertyVisitor;
import org.apache.streampipes.model.staticproperty.DomainStaticProperty;
import org.apache.streampipes.model.staticproperty.FileStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.MappingProperty;
import org.apache.streampipes.model.staticproperty.MappingPropertyNary;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.model.staticproperty.MatchingStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.RemoteOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableTreeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;

import java.util.List;

public class UpdateStaticPropertiesVisitor extends DefaultStaticPropertyVisitor {

  private final List<SpDataStream> inputStreams;
  private final InvocableStreamPipesEntity pipelineElement;

  public UpdateStaticPropertiesVisitor(InvocableStreamPipesEntity pipelineElement) {
    this.inputStreams = pipelineElement.getInputStreams();
    this.pipelineElement = pipelineElement;
  }

  @Override
  public void visit(AnyStaticProperty property) {
    // Do nothing
  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {
    // Do nothing
  }

  @Override
  public void visit(ColorPickerStaticProperty colorPickerStaticProperty) {
    // Do nothing
  }

  @Override
  public void visit(DomainStaticProperty domainStaticProperty) {
    // Do nothing
  }

  @Override
  public void visit(FileStaticProperty fileStaticProperty) {
    // Do nothing
  }

  @Override
  public void visit(FreeTextStaticProperty freeTextStaticProperty) {
    // Do nothing
  }

  @Override
  public void visit(MappingPropertyNary mappingPropertyNary) {
    updateMappingProperty(mappingPropertyNary);
  }

  @Override
  public void visit(MappingPropertyUnary mappingPropertyUnary) {
    updateMappingProperty(mappingPropertyUnary);
  }

  @Override
  public void visit(MatchingStaticProperty matchingStaticProperty) {
    // compute
  }

  @Override
  public void visit(OneOfStaticProperty oneOfStaticProperty) {
    // do nothing
  }

  @Override
  public void visit(SecretStaticProperty secretStaticProperty) {
    // do nothing
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

  private void updateMappingProperty(MappingProperty mappingProperty) {
    AbstractRequirementsSelectorGenerator generator = RequirementsSelectorGeneratorFactory
        .getRequirementsSelector(
            mappingProperty,
            inputStreams,
            pipelineElement
        );
    mappingProperty.setMapsFromOptions(generator.generateSelectors());
  }
}
