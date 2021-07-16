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
package org.apache.streampipes.manager.secret;

import org.apache.streampipes.model.staticproperty.*;

public class SecretVisitor implements StaticPropertyVisitor {

  private String username;
  private ISecretHandler secretHandler;

  public SecretVisitor(String username,
                       ISecretHandler secretHandler) {
    this.username = username;
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
      String newValue = secretHandler.apply(username, secretStaticProperty.getValue());
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
}
