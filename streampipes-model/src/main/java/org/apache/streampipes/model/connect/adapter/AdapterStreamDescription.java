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

package org.apache.streampipes.model.connect.adapter;

import org.apache.streampipes.model.SpDataStream;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
    @JsonSubTypes.Type(SpecificAdapterStreamDescription.class),
    @JsonSubTypes.Type(SpecificAdapterStreamDescription.class)
})
public abstract class AdapterStreamDescription extends AdapterDescription {

  private SpDataStream dataStream;

  private boolean running;

  public AdapterStreamDescription() {
    super();
    this.dataStream = new SpDataStream();
  }

  public AdapterStreamDescription(String elementId, String name, String description) {
    super(elementId, name, description);
    this.dataStream = new SpDataStream();
  }

  public AdapterStreamDescription(AdapterStreamDescription other) {
    super(other);
    this.running = other.isRunning();
    if (other.getDataStream() != null) {
      this.dataStream = new SpDataStream(other.getDataStream());
    }
  }

  public SpDataStream getDataStream() {
    return dataStream;
  }

  public void setDataStream(SpDataStream dataStream) {
    this.dataStream = dataStream;
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }
}
