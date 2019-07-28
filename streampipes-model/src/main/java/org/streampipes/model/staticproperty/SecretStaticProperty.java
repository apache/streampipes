/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.SECRET_STATIC_PROPERTY)
@Entity
public class SecretStaticProperty extends StaticProperty {

  public SecretStaticProperty() {
    super(StaticPropertyType.SecretStaticProperty);
  }

  @RdfProperty(StreamPipes.HAS_VALUE)
  private String value;

  @RdfProperty(StreamPipes.IS_ENCRYPTED)
  private Boolean encrypted;

  public SecretStaticProperty(SecretStaticProperty other) {
    super(other);
    this.value = other.getValue();
    this.encrypted = other.getEncrypted();
  }

  public SecretStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.SecretStaticProperty, internalName, label, description);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Boolean getEncrypted() {
    return encrypted;
  }

  public void setEncrypted(Boolean encrypted) {
    this.encrypted = encrypted;
  }
}
