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

import org.apache.streampipes.model.util.Cloner;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventGrounding {

  private static final long serialVersionUID = 3149070517282698799L;

  private List<TransportProtocol> transportProtocols;

  private List<TransportFormat> transportFormats;

  public EventGrounding() {
    super();
    this.transportFormats = new ArrayList<>();
    this.transportProtocols = new ArrayList<>();
  }

  public EventGrounding(TransportProtocol transportProtocol, TransportFormat transportFormat) {
    this();
    this.transportFormats = new ArrayList<>();
    this.transportFormats.add(transportFormat);
    this.transportProtocols = Arrays.asList(transportProtocol);
  }

  public EventGrounding(EventGrounding other) {
    this.transportProtocols = new Cloner().protocols(other.getTransportProtocols());
    this.transportFormats = new Cloner().transportFormats(other.getTransportFormats());
  }

  public List<TransportProtocol> getTransportProtocols() {
    return transportProtocols;
  }

  public void setTransportProtocols(List<TransportProtocol> transportProtocols) {
    this.transportProtocols = transportProtocols;
  }

  @JsonIgnore
  public TransportProtocol getTransportProtocol() {
    if (transportProtocols.size() == 0) {
      return null;
    } else {
      return transportProtocols.get(0);
    }
  }

  public void setTransportProtocol(TransportProtocol transportProtocol) {
    this.transportProtocols = Collections.singletonList(transportProtocol);
  }

  public List<TransportFormat> getTransportFormats() {
    return transportFormats;
  }

  public void setTransportFormats(List<TransportFormat> transportFormats) {
    this.transportFormats = transportFormats;
  }

}
