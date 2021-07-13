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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.SchemaTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.StreamTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ValueTransformationRuleDescription;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;

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

//    @RdfProperty("sp:couchDBId")
//    @JsonProperty("couchDBId")
//    private @SerializedName("_id") String id;
//
//    @JsonProperty("_rev")
//    private @SerializedName("_rev") String rev;

    private String adapterId;

    private String userName;

    private EventGrounding eventGrounding;

    private String adapterType;

    private String icon;

    private List<StaticProperty> config;

    private List<TransformationRuleDescription> rules;

    private List<String> category;

    private long createdAt;

    private String selectedEndpointUrl;

    private String correspondingServiceGroup;

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
        this.eventGrounding.setTransportProtocols(Arrays.asList(tpKafka,tpJms,tpMqtt));
    }

    public AdapterDescription(String uri, String name, String description) {
        super(uri, name, description);
        this.rules = new ArrayList<>();
        this.category = new ArrayList<>();
    }


    public AdapterDescription(AdapterDescription other) {
        super(other);
        this.adapterId = other.getAdapterId();
        //this.id = other.getId();
        //this.rev = other.getRev();
        this.config = new Cloner().staticProperties(other.getConfig());
        this.userName = other.getUserName();
        this.rules = other.getRules();
        this.adapterType = other.getAdapterType();
        this.icon = other.getIcon();
        this.category = new Cloner().epaTypes(other.getCategory());
        this.createdAt = other.getCreatedAt();
        this.selectedEndpointUrl = other.getSelectedEndpointUrl();
        this.correspondingServiceGroup = other.getCorrespondingServiceGroup();
        if (other.getEventGrounding() != null) this.eventGrounding = new EventGrounding(other.getEventGrounding());
    }



    public String getId() {
        return this.elementId;
    }

    public void setId(String id) {
        this.elementId = id;
    }

    public String getRev() {
        return this.rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getAdapterId() {
        return adapterId;
    }

    public void setAdapterId(String adapterId) {
        this.adapterId = adapterId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void addConfig(StaticProperty sp) {
        this.config.add(sp);
    }

    public void setConfig(List<StaticProperty> config) {
        this.config = config;
    }

    public String getAdapterType() {
        return adapterType;
    }

    public void setAdapterType(String adapterType) {
        this.adapterType = adapterType;
    }

    public List getValueRules() {
        List tmp = new ArrayList<>();
        rules.forEach(rule -> {
            if(rule instanceof ValueTransformationRuleDescription)
                tmp.add(rule);
        });
        return tmp;
    }

    public List getStreamRules() {
        List tmp = new ArrayList<>();
        rules.forEach(rule -> {
            if(rule instanceof StreamTransformationRuleDescription)
                tmp.add(rule);
        });
        return tmp;
    }

    public List getSchemaRules() {
        List tmp = new ArrayList<>();
        rules.forEach(rule -> {
            if(rule instanceof SchemaTransformationRuleDescription)
                tmp.add(rule);
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
        return "AdapterDescription{" +
                //"id='" + id + '\'' +
                //", rev='" + rev + '\'' +
                ", elementId='" + elementId + '\'' +
                ", DOM='" + DOM + '\'' +
                '}';
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

    public String getCorrespondingServiceGroup() {
        return correspondingServiceGroup;
    }

    public void setCorrespondingServiceGroup(String correspondingServiceGroup) {
        this.correspondingServiceGroup = correspondingServiceGroup;
    }
}
