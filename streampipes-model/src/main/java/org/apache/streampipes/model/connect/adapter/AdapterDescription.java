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

import com.google.gson.annotations.SerializedName;
import io.fogsy.empire.annotations.Namespaces;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.connect.rules.Schema.SchemaTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.Stream.StreamTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ValueTransformationRuleDescription;
import org.apache.streampipes.model.grounding.*;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:AdapterDescription")
@Entity
public abstract class AdapterDescription extends NamedStreamPipesEntity {

    @RdfProperty("sp:couchDBId")
    private @SerializedName("_id") String id;

    private @SerializedName("_rev") String rev;

    @RdfProperty("sp:adapterId")
    private String adapterId;

    @RdfProperty("sp:userName")
    private String userName;

    @RdfProperty("sp:grounding")
    private EventGrounding eventGrounding;

    @RdfProperty("sp:adapterType")
    private String adapterType;

    @RdfProperty("sp:icon")
    private String icon;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:config")
    private List<StaticProperty> config;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:rules")
    private List<TransformationRuleDescription> rules;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_ADAPTER_TYPE)
    private List<String> category;

    public AdapterDescription() {
        super();
        this.rules = new ArrayList<>();
        this.eventGrounding = new EventGrounding();
        this.config = new ArrayList<>();
        this.category = new ArrayList<>();

        // TODO move to another place
        TransportProtocol tpKafka = new KafkaTransportProtocol();
        TransportProtocol tpJms = new JmsTransportProtocol();
        tpKafka.setTopicDefinition(new SimpleTopicDefinition("bb"));
        tpJms.setTopicDefinition(new SimpleTopicDefinition("cc"));
        this.eventGrounding.setTransportProtocols(Arrays.asList(tpKafka,tpJms));
//        this.eventGrounding.setTransportFormats(Arrays.asList(Formats.jsonFormat()));


    }

    public AdapterDescription(String uri, String name, String description) {
        super(uri, name, description);
        this.rules = new ArrayList<>();
        this.category = new ArrayList<>();
    }


    public AdapterDescription(AdapterDescription other) {
        super(other);
        this.adapterId = other.getAdapterId();
        this.config = new Cloner().staticProperties(other.getConfig());
        this.userName = other.getUserName();
        this.rules = other.getRules();
        this.adapterType = other.getAdapterType();
        this.icon = other.getIcon();
        this.category = new Cloner().epaTypes(other.getCategory());
        if (other.getEventGrounding() != null) this.eventGrounding = new EventGrounding(other.getEventGrounding());
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
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
                "id='" + id + '\'' +
                ", rev='" + rev + '\'' +
                ", elementId='" + elementId + '\'' +
                ", DOM='" + DOM + '\'' +
                '}';
    }
}
