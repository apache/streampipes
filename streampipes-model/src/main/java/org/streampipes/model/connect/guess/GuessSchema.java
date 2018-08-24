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

package org.streampipes.model.connect.guess;

import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.connect.guess.DomainPropertyProbabilityList;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass(StreamPipes.GUESS_SCHEMA)
@Entity
public class GuessSchema extends UnnamedStreamPipesEntity {

    @RdfProperty("sp:hasEventSchema")
    public EventSchema eventSchema;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:propertyProbabilityList")
    public List<DomainPropertyProbabilityList> propertyProbabilityList;

    public GuessSchema() {
        super();
        this.propertyProbabilityList = new ArrayList<>();


    }

    public GuessSchema(EventSchema schema, List<DomainPropertyProbabilityList> propertyProbabilityList) {
        super();
        this.eventSchema = schema;
        this.propertyProbabilityList = propertyProbabilityList;
    }

	public GuessSchema(GuessSchema other) {
		super(other);
		this.eventSchema = other.getEventSchema() != null ? new EventSchema(other.getEventSchema()) : null;
	}



    public EventSchema getEventSchema() {
        return eventSchema;
    }

    public void setEventSchema(EventSchema eventSchema) {
        this.eventSchema = eventSchema;
    }

    public List<DomainPropertyProbabilityList> getPropertyProbabilityList() {
        return propertyProbabilityList;
    }

    public void setPropertyProbabilityList(List<DomainPropertyProbabilityList> propertyProbabilityList) {
        this.propertyProbabilityList = propertyProbabilityList;
    }
}
