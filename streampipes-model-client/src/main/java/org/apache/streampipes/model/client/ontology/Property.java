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

package org.apache.streampipes.model.client.ontology;

public class Property extends OntologyElement {

  private Range range;

  private boolean rangeDefined;
  private boolean labelDefined;

  public Property(ElementHeader elementHeader, String rdfsLabel, String rdfsDescription, Range range) {
    super(elementHeader, rdfsLabel, rdfsDescription);
    this.range = range;
    this.labelDefined = true;
    this.rangeDefined = true;
  }

  public Property() {
    // TODO Auto-generated constructor stub
  }

  public Property(ElementHeader header) {
    super(header);
    this.rangeDefined = false;
    this.labelDefined = false;
  }

  public Range getRange() {
    return range;
  }

  public void setRange(Range range) {
    this.range = range;
  }

  public boolean isRangeDefined() {
    return rangeDefined;
  }

  public void setRangeDefined(boolean rangeDefined) {
    this.rangeDefined = rangeDefined;
  }

  public boolean isLabelDefined() {
    return labelDefined;
  }

  public void setLabelDefined(boolean labelDefined) {
    this.labelDefined = labelDefined;
  }

  @Override
  public int hashCode() {
    return this.getElementHeader().getId().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Property)) {
      return false;
    } else {
      return this.getElementHeader().getId().equals(((Property) other).getElementHeader().getId());
    }
  }


}
