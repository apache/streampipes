/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.connect.adapter;

import com.google.gson.annotations.SerializedName;
import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.connect.grounding.FormatDescription;
import org.streampipes.model.connect.grounding.ProtocolDescription;
import org.streampipes.model.connect.rules.TransformationRuleDescription;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.grounding.KafkaTransportProtocol;
import org.streampipes.model.grounding.SimpleTopicDefinition;
import org.streampipes.model.grounding.TransportProtocol;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:rules")
    private List<TransformationRuleDescription> rules;

    public AdapterDescription() {
        super();
        this.rules = new ArrayList<>();
        this.eventGrounding = new EventGrounding();

        // TODO move to another place
        TransportProtocol tp = new KafkaTransportProtocol();
        tp.setTopicDefinition(new SimpleTopicDefinition("bb"));
        this.eventGrounding.setTransportProtocol(tp);
//        this.eventGrounding.setTransportFormats(Arrays.asList(Formats.jsonFormat()));


    }

    public AdapterDescription(String uri, String name, String description) {
        super(uri, name, description);
    }


    public AdapterDescription(AdapterDescription other) {
        super(other);
        this.adapterId = other.getAdapterId();
        this.userName = other.getUserName();
        this.rules = other.getRules();
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
