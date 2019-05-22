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

package org.streampipes.model.connect.grounding;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:ProtocolDescription")
@Entity
public class ProtocolDescription extends NamedStreamPipesEntity {


    //Remove for new classes
    @Deprecated
    @RdfProperty("sp:sourceType")
    String sourceType;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:config")
    List<StaticProperty> config;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_ADAPTER_TYPE)
    private List<String> category;

    public ProtocolDescription() {
    }

    public ProtocolDescription(String uri, String name, String description) {
        super(uri, name, description);
        this.config = new ArrayList<>();
        this.category = new ArrayList<>();
    }

    public ProtocolDescription(String uri, String name, String description, List<StaticProperty> config) {
        super(uri, name, description);
        this.config = config;
        this.category = new ArrayList<>();
    }

    public ProtocolDescription(ProtocolDescription other) {
        super(other);

        this.config = new Cloner().staticProperties(other.getConfig());
        if (other.getCategory() != null) {
            this.category = new Cloner().epaTypes(other.getCategory());
        }
    }

    public void addConfig(StaticProperty sp) {
        this.config.add(sp);
    }

    public List<StaticProperty> getConfig() {
        return config;
    }

    public void setConfig(List<StaticProperty> config) {
        this.config = config;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }
}
