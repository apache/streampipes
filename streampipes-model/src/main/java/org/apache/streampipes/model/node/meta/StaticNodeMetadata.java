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
package org.apache.streampipes.model.node.meta;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@RdfsClass(StreamPipes.STATIC_NODE_METADATA)
@Entity
@TsModel
public class StaticNodeMetadata extends UnnamedStreamPipesEntity {

    @RdfProperty(StreamPipes.NODE_TYPE)
    private String type;

    @RdfProperty(StreamPipes.NODE_MODEL)
    private String model;

    @OneToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_GEO_LOCATION)
    private GeoLocation geoLocation;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty(StreamPipes.HAS_NODE_TAGS)
    private List<String> locationTags;

    public StaticNodeMetadata() {
        super();
        this.locationTags = new ArrayList<>();
    }

    public StaticNodeMetadata(String type, String model, GeoLocation geoLocation, List<String> locationTags) {
        super();
        this.type = type;
        this.model = model;
        this.geoLocation = geoLocation;
        this.locationTags = locationTags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public List<String> getLocationTags() {
        return locationTags;
    }

    public void setLocationTags(List<String> locationTags) {
        this.locationTags = locationTags;
    }
}
