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

package org.apache.streampipes.model.schema;

import java.util.List;
import java.util.Objects;

public class Enumeration extends ValueSpecification {

  private static final long serialVersionUID = 1L;

  private String label;

  private String description;

  private List<String> runtimeValues;

  public Enumeration() {
    super();
  }

  public Enumeration(String label, String description, List<String> runtimeValues) {
    super();
    this.label = label;
    this.description = description;
    this.runtimeValues = runtimeValues;
  }

  public Enumeration(Enumeration other) {
    this.label = other.getLabel();
    this.description = other.getDescription();
    this.runtimeValues = other.getRuntimeValues();
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getRuntimeValues() {
    return runtimeValues;
  }

  public void setRuntimeValues(List<String> runtimeValues) {
    this.runtimeValues = runtimeValues;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Enumeration that = (Enumeration) o;
    return Objects.equals(label, that.label) && Objects.equals(description, that.description)
           && Objects.equals(runtimeValues, that.runtimeValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, description, runtimeValues);
  }
}
