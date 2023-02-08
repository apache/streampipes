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

package org.apache.streampipes.model.grounding;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TransportFormat {

  private static final long serialVersionUID = -525073244975968386L;

  private List<URI> rdfType;

  public TransportFormat() {
    super();
    this.rdfType = new ArrayList<>();
  }

  public TransportFormat(String transportFormatType) {
    super();
    this.rdfType = new ArrayList<>();
    this.rdfType.add(URI.create(transportFormatType));
  }

  public TransportFormat(TransportFormat other) {
    this.rdfType = other.getRdfType();
  }

  public List<URI> getRdfType() {
    return rdfType;
  }

  public void setRdfType(List<URI> rdfType) {
    this.rdfType = rdfType;
  }

}
