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

package org.apache.streampipes.model.connect.guess;

import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class GuessSchema extends UnnamedStreamPipesEntity {

    public EventSchema eventSchema;

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
