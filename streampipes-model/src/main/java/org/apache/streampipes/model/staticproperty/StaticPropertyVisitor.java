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
package org.apache.streampipes.model.staticproperty;

public interface StaticPropertyVisitor {

  void visit(AnyStaticProperty property);

  void visit(CodeInputStaticProperty codeInputStaticProperty);

  void visit(CollectionStaticProperty collectionStaticProperty);

  void visit(ColorPickerStaticProperty colorPickerStaticProperty);

  void visit(DomainStaticProperty domainStaticProperty);

  void visit(FileStaticProperty fileStaticProperty);

  void visit(FreeTextStaticProperty freeTextStaticProperty);

  void visit(MappingPropertyNary mappingPropertyNary);

  void visit(MappingPropertyUnary mappingPropertyUnary);

  void visit(MatchingStaticProperty matchingStaticProperty);

  void visit(OneOfStaticProperty oneOfStaticProperty);

  void visit(SecretStaticProperty secretStaticProperty);

  void visit(StaticPropertyAlternative staticPropertyAlternative);

  void visit(StaticPropertyAlternatives staticPropertyAlternatives);

  void visit(StaticPropertyGroup staticPropertyGroup);

  void visit(RemoteOneOfStaticProperty remoteOneOfStaticProperty);

  void visit(SlideToggleStaticProperty slideToggleStaticProperty);

  void visit(RuntimeResolvableTreeInputStaticProperty treeInputStaticProperty);
}
