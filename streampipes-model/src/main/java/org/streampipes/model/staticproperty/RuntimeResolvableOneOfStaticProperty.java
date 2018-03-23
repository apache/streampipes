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

@RdfsClass(StreamPipes.RUNTIME_RESOLVABLE_ONE_OF_STATIC_PROPERTY)
@Entity
public class RuntimeResolvableOneOfStaticProperty extends RuntimeResolvableSelectionStaticProperty {

  public RuntimeResolvableOneOfStaticProperty() {
    super(StaticPropertyType.RuntimeResolvableOneOfStaticProperty);
  }

  public RuntimeResolvableOneOfStaticProperty(org.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty other) {
    super(other);
  }

  public RuntimeResolvableOneOfStaticProperty(String internalName, String label, String description) {
    super(StaticPropertyType.RuntimeResolvableOneOfStaticProperty, internalName, label, description);
  }
}
