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

import java.util.List;
import java.util.stream.Collectors;

public class StaticPropertyAlternatives extends StaticProperty {

  private List<StaticPropertyAlternative> alternatives;

  public StaticPropertyAlternatives() {
    super(StaticPropertyType.StaticPropertyAlternatives);
  }

  public StaticPropertyAlternatives(StaticPropertyAlternatives other) {
    super(other);
//    this.alternatives = new Cloner().staticProperties()
    this.alternatives = other
        .getAlternatives()
        .stream()
        .map(StaticPropertyAlternative::new)
        .collect(Collectors.toList());
  }

  public StaticPropertyAlternatives(String internalName, String label, String description) {
    super(StaticPropertyType.StaticPropertyAlternatives, internalName, label, description);
  }

  public List<StaticPropertyAlternative> getAlternatives() {
    return alternatives;
  }

  public void setAlternatives(List<StaticPropertyAlternative> alternatives) {
    this.alternatives = alternatives;
  }

  @Override
  public void accept(StaticPropertyVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof StaticPropertyAlternatives that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    if (!(this.alternatives.size() == that.alternatives.size())) {
      return false;
    }

    for (var i = 0; i < alternatives.size(); i++){
      if (!alternatives.get(i).equals(that.alternatives.get(i))){
        return false;
      }
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (alternatives != null ? alternatives.hashCode() : 0);
    return result;
  }
}
