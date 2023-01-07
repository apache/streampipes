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
package org.apache.streampipes.resource.management.secret;

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
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.model.staticproperty.StaticPropertyVisitor;

public class SecretVisitor implements StaticPropertyVisitor {

  private ISecretHandler secretHandler;

  public SecretVisitor(ISecretHandler secretHandler) {
    this.secretHandler = secretHandler;
  }

  @Override
  public void visit(AnyStaticProperty property) {

  }

  @Override
  public void visit(CodeInputStaticProperty codeInputStaticProperty) {

  }

  @Override
  public void visit(CollectionStaticProperty collectionStaticProperty) {
    collectionStaticProperty.getMembers().forEach(sp -> sp.accept(this));
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
    if (secretHandler.shouldApply(secretStaticProperty.getEncrypted())) {
      String newValue = secretHandler.apply(secretStaticProperty.getValue());
      secretStaticProperty.setValue(newValue);
      secretStaticProperty.setEncrypted(!secretStaticProperty.getEncrypted());
    }
  }

  @Override
  public void visit(StaticPropertyAlternative staticPropertyAlternative) {
    if (staticPropertyAlternative.getSelected() && staticPropertyAlternative.getStaticProperty() != null) {
      staticPropertyAlternative.getStaticProperty().accept(this);
    }
  }

  @Override
  public void visit(StaticPropertyAlternatives staticPropertyAlternatives) {
    staticPropertyAlternatives.getAlternatives().forEach(sp -> sp.accept(this));
  }

  @Override
  public void visit(StaticPropertyGroup staticPropertyGroup) {
    staticPropertyGroup.getStaticProperties().forEach(sp -> sp.accept(this));
  }

  @Override
  public void visit(RemoteOneOfStaticProperty remoteOneOfStaticProperty) {

  }

  @Override
  public void visit(SlideToggleStaticProperty slideToggleStaticProperty) {
    // Do nothing
  }

  @Override
  public void visit(RuntimeResolvableTreeInputStaticProperty treeInputStaticProperty) {
    // Do nothing
  }
}
