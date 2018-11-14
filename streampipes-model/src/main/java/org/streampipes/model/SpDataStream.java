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

package org.streampipes.model;

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.quality.EventStreamQualityDefinition;
import org.streampipes.model.quality.EventStreamQualityRequirement;
import org.streampipes.model.quality.MeasurementCapability;
import org.streampipes.model.quality.MeasurementObject;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RdfsClass(StreamPipes.DATA_STREAM)
@Entity
public class SpDataStream extends NamedStreamPipesEntity {

	private static final long serialVersionUID = -5732549347563182863L;
	
	private static final String prefix = "urn:fzi.de:eventstream:";

	@OneToMany(fetch = FetchType.EAGER,
					cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty(StreamPipes.HAS_EVENT_STREAM_QUALITY_DEFINITION)
	protected transient List<EventStreamQualityDefinition> hasEventStreamQualities;

	@OneToMany(fetch = FetchType.EAGER,
					cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty(StreamPipes.HAS_EVENT_STREAM_QUALITY_REQUIREMENT)
	protected transient List<EventStreamQualityRequirement> requiresEventStreamQualities;

	@OneToOne(fetch = FetchType.EAGER,
					cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty(StreamPipes.HAS_GROUNDING)
	protected EventGrounding eventGrounding;

	@OneToOne(fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_SCHEMA)
	protected EventSchema eventSchema;

	@RdfProperty(StreamPipes.HAS_MEASUREMENT_CAPABILTIY)
	@OneToMany(fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
	protected List<MeasurementCapability> measurementCapability;

	@RdfProperty(StreamPipes.HAS_MEASUREMENT_OBJECT)
	@OneToMany(fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
	protected List<MeasurementObject> measurementObject;

	protected List<String> category;

	public SpDataStream(String uri, String name, String description, String iconUrl, List<EventStreamQualityDefinition> hasEventStreamQualities,
											EventGrounding eventGrounding,
											EventSchema eventSchema) {
		super(uri, name, description, iconUrl);
		this.hasEventStreamQualities = hasEventStreamQualities;
		this.eventGrounding = eventGrounding;
		this.eventSchema = eventSchema;
	}
	
	public SpDataStream(String uri, String name, String description, EventSchema eventSchema)
	{
		super(uri, name, description);
		this.eventSchema = eventSchema;
	}

	public SpDataStream() {
		super(prefix +RandomStringUtils.randomAlphabetic(6));
	}


	public SpDataStream(SpDataStream other) {
		super(other);
		if (other.getEventGrounding() != null) this.eventGrounding = new EventGrounding(other.getEventGrounding());
		if (other.getEventSchema() != null) this.eventSchema = new EventSchema(other.getEventSchema());
		if (other.getHasEventStreamQualities() != null) this.hasEventStreamQualities = other.getHasEventStreamQualities().stream().map(EventStreamQualityDefinition::new).collect(Collectors.toCollection(ArrayList::new));
		if (other.getRequiresEventStreamQualities() != null) this.requiresEventStreamQualities = other.getRequiresEventStreamQualities().stream().map(EventStreamQualityRequirement::new).collect(Collectors.toCollection(ArrayList::new));
		if (other.getMeasurementCapability() != null) this.measurementCapability =  new Cloner().mc(other.getMeasurementCapability());
		if (other.getMeasurementObject() != null) this.measurementObject = new Cloner().mo(other.getMeasurementObject());

	}

	public List<EventStreamQualityDefinition> getHasEventStreamQualities() {
		return hasEventStreamQualities;
	}

	public void setHasEventStreamQualities(
					List<EventStreamQualityDefinition> hasEventStreamQualities) {
		this.hasEventStreamQualities = hasEventStreamQualities;
	}


	public List<EventStreamQualityRequirement> getRequiresEventStreamQualities() {
		return requiresEventStreamQualities;
	}

	public void setRequiresEventStreamQualities(
					List<EventStreamQualityRequirement> requiresEventStreamQualities) {
		this.requiresEventStreamQualities = requiresEventStreamQualities;
	}

	public EventSchema getEventSchema() {
		return eventSchema;
	}

	public void setEventSchema(EventSchema eventSchema) {
		this.eventSchema = eventSchema;
	}

	public EventGrounding getEventGrounding() {
		return eventGrounding;
	}

	public void setEventGrounding(EventGrounding eventGrounding) {
		this.eventGrounding = eventGrounding;
	}

	public List<MeasurementCapability> getMeasurementCapability() {
		return measurementCapability;
	}

	public void setMeasurementCapability(
					List<MeasurementCapability> measurementCapability) {
		this.measurementCapability = measurementCapability;
	}

	public List<MeasurementObject> getMeasurementObject() {
		return measurementObject;
	}

	public void setMeasurementObject(List<MeasurementObject> measurementObject) {
		this.measurementObject = measurementObject;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

}
