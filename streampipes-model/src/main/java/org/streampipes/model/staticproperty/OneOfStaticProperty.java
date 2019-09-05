/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.ONE_OF_STATIC_PROPERTY)
@Entity
public class OneOfStaticProperty extends SelectionStaticProperty {

  private static final long serialVersionUID = 3483290363677184344L;

  public OneOfStaticProperty() {
    super(StaticPropertyType.OneOfStaticProperty);
  }

  public OneOfStaticProperty(StaticPropertyType staticPropertyType) {
    super(staticPropertyType);
  }

  public OneOfStaticProperty(StaticPropertyType staticPropertyType, String internalName,
                             String label, String description) {
    super(staticPropertyType, internalName, label, description);
  }

  public OneOfStaticProperty(OneOfStaticProperty other) {
    super(other);
  }

  public OneOfStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.OneOfStaticProperty, internalName, label, description);
  }


}
