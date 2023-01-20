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

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.SchemaTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.StreamTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ValueTransformationRuleDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.SimpleTopicDefinition;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonSubTypes({
    @JsonSubTypes.Type(GenericAdapterSetDescription.class),
    @JsonSubTypes.Type(GenericAdapterStreamDescription.class),
    @JsonSubTypes.Type(SpecificAdapterStreamDescription.class),
    @JsonSubTypes.Type(SpecificAdapterSetDescription.class)
})
@TsModel
public abstract class AdapterDescription extends NamedStreamPipesEntity {

  private EventGrounding eventGrounding;

  private String adapterType;

  private String icon;

  private List<StaticProperty> config;

  private List<TransformationRuleDescription> rules;

  private List<String> category;

  private long createdAt;

  //  Is used to store where the adapter is running to stop it
  private String selectedEndpointUrl;

  /**
   * This is used to identify all the service within the service group the adapter can be invoked in
   */
  private String correspondingServiceGroup;

  /**
   * This is the identifier of the data stream that is associated with the adapter
   */
  private String correspondingDataStreamElementId;

  public AdapterDescription() {
    super();
    this.rules = new ArrayList<>();
    this.eventGrounding = new EventGrounding();
    this.config = new ArrayList<>();
    this.category = new ArrayList<>();

    // TODO move to another place
    TransportProtocol tpKafka = new KafkaTransportProtocol();
    TransportProtocol tpJms = new JmsTransportProtocol();
    TransportProtocol tpMqtt = new MqttTransportProtocol();
    tpKafka.setTopicDefinition(new SimpleTopicDefinition("bb"));
    tpJms.setTopicDefinition(new SimpleTopicDefinition("cc"));
    tpMqtt.setTopicDefinition(new SimpleTopicDefinition("dd"));
    this.eventGrounding.setTransportProtocols(Arrays.asList(tpKafka, tpJms, tpMqtt));
  }

  public AdapterDescription(String elementId, String name, String description) {
    super(elementId, name, description);
    this.rules = new ArrayList<>();
    this.category = new ArrayList<>();
  }


  public AdapterDescription(AdapterDescription other) {
    super(other);
    this.config = new Cloner().staticProperties(other.getConfig());
    this.rules = other.getRules();
    this.adapterType = other.getAdapterType();
    this.icon = other.getIcon();
    this.category = new Cloner().epaTypes(other.getCategory());
    this.createdAt = other.getCreatedAt();
    this.selectedEndpointUrl = other.getSelectedEndpointUrl();
    this.correspondingServiceGroup = other.getCorrespondingServiceGroup();
    this.correspondingDataStreamElementId = other.getCorrespondingDataStreamElementId();
    if (other.getEventGrounding() != null) {
      this.eventGrounding = new EventGrounding(other.getEventGrounding());
    }
  }

  public String getRev() {
    return this.rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public List<TransformationRuleDescription> getRules() {
    return rules;
  }

  public void setRules(List<TransformationRuleDescription> rules) {
    this.rules = rules;
  }

  public EventGrounding getEventGrounding() {
    return eventGrounding;
  }

  public void setEventGrounding(EventGrounding eventGrounding) {
    this.eventGrounding = eventGrounding;
  }

  public List<StaticProperty> getConfig() {
    return config;
  }

  public void setConfig(List<StaticProperty> config) {
    this.config = config;
  }

  public void addConfig(StaticProperty sp) {
    this.config.add(sp);
  }

  public String getAdapterType() {
    return adapterType;
  }

  public void setAdapterType(String adapterType) {
    this.adapterType = adapterType;
  }

  public List<TransformationRuleDescription> getValueRules() {
    var tmp = new ArrayList<TransformationRuleDescription>();
    rules.forEach(rule -> {
      if (rule instanceof ValueTransformationRuleDescription && !(rule instanceof AddTimestampRuleDescription)) {
        tmp.add(rule);
      }
    });
    return tmp;
  }

  public List<TransformationRuleDescription> getStreamRules() {
    var tmp = new ArrayList<TransformationRuleDescription>();
    rules.forEach(rule -> {
      if (rule instanceof StreamTransformationRuleDescription) {
        tmp.add(rule);
      }
    });
    return tmp;
  }

  public List<TransformationRuleDescription> getSchemaRules() {
    var tmp = new ArrayList<TransformationRuleDescription>();
    rules.forEach(rule -> {
      if (rule instanceof SchemaTransformationRuleDescription) {
        tmp.add(rule);
      }
    });
    return tmp;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public List<String> getCategory() {
    return category;
  }

  public void setCategory(List<String> category) {
    this.category = category;
  }

  @Override
  public String toString() {
    return String.format("AdapterDescription{elementId= '%s', DOM= '%s'}", elementId, dom);
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public String getSelectedEndpointUrl() {
    return selectedEndpointUrl;
  }

  public void setSelectedEndpointUrl(String selectedEndpointUrl) {
    this.selectedEndpointUrl = selectedEndpointUrl;
  }

  /**
   * @deprecated check if the service group can be removed as a single pipeline element
   * can correspond to different service groups
   */
  @Deprecated
  public String getCorrespondingServiceGroup() {
    return correspondingServiceGroup;
  }

  /**
   * @deprecated check if the service group can be removed as a single pipeline element
   * can correspond to different service groups
   */
  @Deprecated
  public void setCorrespondingServiceGroup(String correspondingServiceGroup) {
    this.correspondingServiceGroup = correspondingServiceGroup;
  }

  public String getCorrespondingDataStreamElementId() {
    return correspondingDataStreamElementId;
  }

  public void setCorrespondingDataStreamElementId(String correspondingDataStreamElementId) {
    this.correspondingDataStreamElementId = correspondingDataStreamElementId;
  }

  public abstract EventSchema getEventSchema();
}
