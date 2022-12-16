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

public abstract class OntologyElement {

  private ElementHeader elementHeader;

  private String rdfsLabel;
  private String rdfsDescription;

  public OntologyElement(ElementHeader elementHeader, String rdfsLabel, String rdfsDescription) {
    this.elementHeader = elementHeader;
    this.rdfsLabel = rdfsLabel;
    this.rdfsDescription = rdfsDescription;
  }

  public OntologyElement() {

  }

  public OntologyElement(ElementHeader header) {
    this.elementHeader = header;
  }

  public String getRdfsLabel() {
    return rdfsLabel;
  }

  public void setRdfsLabel(String rdfsLabel) {
    this.rdfsLabel = rdfsLabel;
  }

  public String getRdfsDescription() {
    return rdfsDescription;
  }

  public void setRdfsDescription(String rdfsDescription) {
    this.rdfsDescription = rdfsDescription;
  }

  public ElementHeader getElementHeader() {
    return elementHeader;
  }

  public void setElementHeader(ElementHeader elementHeader) {
    this.elementHeader = elementHeader;
  }

}
