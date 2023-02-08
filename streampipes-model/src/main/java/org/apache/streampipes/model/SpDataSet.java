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
package org.apache.streampipes.model;

import org.apache.streampipes.model.api.EndpointSelectable;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.schema.EventSchema;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SpDataSet extends SpDataStream implements EndpointSelectable {

  private EventGrounding supportedGrounding;

  private String datasetInvocationId;

  private String correspondingPipeline;
  private String selectedEndpointUrl;

  public SpDataSet(String uri,
                   String name,
                   String description,
                   String iconUrl,
                   EventGrounding eventGrounding,
                   EventSchema eventSchema) {
    super(uri, name, description, iconUrl, eventGrounding, eventSchema);
  }

  public SpDataSet(String uri, String name, String description, EventSchema eventSchema) {
    super(uri, name, description, eventSchema);
  }

  public SpDataSet() {
    super();
    this.eventSchema = new EventSchema();
  }

  public SpDataSet(SpDataSet other) {
    super(other);
    this.datasetInvocationId = other.getDatasetInvocationId();
    if (other.getSupportedGrounding() != null) {
      this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());
    }
  }

  public String getBrokerHostname() {
    if (getEventGrounding() == null || getEventGrounding().getTransportProtocol() == null
        || getEventGrounding().getTransportProtocol().getBrokerHostname() == null) {
      return "";
    } else {
      return getEventGrounding().getTransportProtocol().getBrokerHostname();
    }
  }

  public String getActualTopicName() {
    if (getEventGrounding() == null || getEventGrounding().getTransportProtocol() == null
        || getEventGrounding().getTransportProtocol().getTopicDefinition() == null) {
      return "";
    } else {
      return getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
    }
  }


  public EventGrounding getSupportedGrounding() {
    return supportedGrounding;
  }

  public void setSupportedGrounding(EventGrounding supportedGrounding) {
    this.supportedGrounding = supportedGrounding;
  }

  public String getDatasetInvocationId() {
    return datasetInvocationId;
  }

  public void setDatasetInvocationId(String datasetInvocationId) {
    this.datasetInvocationId = datasetInvocationId;
  }

  @Override
  public String getCorrespondingPipeline() {
    return correspondingPipeline;
  }

  @Override
  public void setCorrespondingPipeline(String correspondingPipeline) {
    this.correspondingPipeline = correspondingPipeline;
  }

  @Override
  @JsonIgnore
  public String getDetachPath() {
    return "/" + getCorrespondingAdapterId() + "/" + getDatasetInvocationId();
  }

  @Override
  public String getSelectedEndpointUrl() {
    return selectedEndpointUrl;
  }

  @Override
  public void setSelectedEndpointUrl(String selectedEndpointUrl) {
    this.selectedEndpointUrl = selectedEndpointUrl;
  }
}
