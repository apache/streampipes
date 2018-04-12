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

package org.streampipes.model.schema;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.EVENT_PROPERTY_LIST)
@Entity
public class EventPropertyList extends EventProperty {
	
	private static final long serialVersionUID = -2636018143426727534L;

	// TODO : change list<eventproperty> to eventproperty!
	@Deprecated
	@RdfProperty(StreamPipes.HAS_EVENT_PROPERTY)
	@OneToOne (fetch = FetchType.EAGER,
	   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<EventProperty> eventProperties;

	@RdfProperty(StreamPipes.HAS_EVENT_PROPERTY)
	private EventProperty eventProperty;

	public EventPropertyList()
	{
		super();
		eventProperties = new ArrayList<>();
	}
	
	public EventPropertyList(EventPropertyList other)
	{
		super(other);
		this.eventProperty = eventProperty;
		this.eventProperties = new Cloner().properties(other.getEventProperties());
	}
	
	public EventPropertyList(String propertyName, EventProperty eventProperty) {
		super(propertyName);
		this.eventProperty = eventProperty;
		eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(eventProperty);
	}

	public EventPropertyList(String propertyName, EventProperty eventProperty, List<URI> domainProperties) {
		super(propertyName);
		this.eventProperty = eventProperty;
		this.setDomainProperties(domainProperties);
	}

	public EventProperty getEventProperty() {
		return eventProperty;
	}

	public void setEventProperty(EventProperty eventProperty) {
		this.eventProperty = eventProperty;
	}

	@Deprecated
	public List<EventProperty> getEventProperties() {
		return eventProperties;
	}

	@Deprecated
	public void setEventProperties(List<EventProperty> eventProperties) {
		this.eventProperties = eventProperties;
	}
	
}
