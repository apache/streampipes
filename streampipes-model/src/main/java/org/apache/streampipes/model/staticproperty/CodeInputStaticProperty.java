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

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.CODE_INPUT_STATIC_PROPERTY)
@Entity
public class CodeInputStaticProperty extends StaticProperty {

  @RdfProperty(StreamPipes.HAS_LANGUAGE)
  private String language;

  @RdfProperty(StreamPipes.HAS_CODE_TEMPLATE)
  private String codeTemplate;

  @RdfProperty(StreamPipes.HAS_CODE_INPUT)
  private String value;

  @RdfProperty(StreamPipes.IS_RECONFIGURABLE)
  private boolean reconfigurable;

  public CodeInputStaticProperty() {
    super(StaticPropertyType.CodeInputStaticProperty);
  }

  public CodeInputStaticProperty(CodeInputStaticProperty other) {
    super(other);
    this.language = other.getLanguage();
    this.value = other.getValue();
    this.codeTemplate = other.getCodeTemplate();
    this.reconfigurable = other.isReconfigurable();
  }

  public CodeInputStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.CodeInputStaticProperty, internalName, label, description);
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getCodeTemplate() {
    return codeTemplate;
  }

  public void setCodeTemplate(String codeTemplate) {
    this.codeTemplate = codeTemplate;
  }

  public boolean isReconfigurable() {
    return reconfigurable;
  }

  public void setReconfigurable(boolean reconfigurable) {
    this.reconfigurable = reconfigurable;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }
}
