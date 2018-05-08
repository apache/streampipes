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

package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.*;
import java.net.URI;
import java.util.List;

@RdfsClass(StreamPipes.MAPPING_PROPERTY)
@MappedSuperclass
@Entity
public abstract class MappingProperty extends StaticProperty {

	private static final long serialVersionUID = -7849999126274124847L;
	
	@RdfProperty(StreamPipes.MAPS_FROM)
	protected URI mapsFrom;

	@RdfProperty(StreamPipes.MAPS_FROM_OPTIONS)
	@OneToMany(fetch = FetchType.EAGER,
					cascade = {CascadeType.ALL})
	private List<EventProperty> mapsFromOptions;

	@OneToOne(fetch = FetchType.EAGER,
					cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_PROPERTY_SCOPE)
	private String propertyScope;
	
	public MappingProperty()
	{
		super();
	}

	public MappingProperty(StaticPropertyType type) {
		super(type);
	}
	
	public MappingProperty(MappingProperty other)
	{
		super(other);
		this.mapsFrom = other.getMapsFrom();
		this.propertyScope = other.getPropertyScope();
		if (other.getMapsFromOptions() != null) {
			this.mapsFromOptions = new Cloner().properties(other.getMapsFromOptions());
		}
	}
	
	protected MappingProperty(StaticPropertyType type, URI mapsFrom, String internalName, String label, String description)
	{
		super(type, internalName, label, description);
		this.mapsFrom = mapsFrom;
	}
	
	protected MappingProperty(StaticPropertyType type, String internalName, String label, String description)
	{
		super(type, internalName, label, description);
	}

	public URI getMapsFrom() {
		return mapsFrom;
	}

	public void setMapsFrom(URI mapsFrom) {
		this.mapsFrom = mapsFrom;
	}

	public List<EventProperty> getMapsFromOptions() {
		return mapsFromOptions;
	}

	public void setMapsFromOptions(List<EventProperty> mapsFromOptions) {
		this.mapsFromOptions = mapsFromOptions;
	}

	public String getPropertyScope() {
		return propertyScope;
	}

	public void setPropertyScope(String propertyScope) {
		this.propertyScope = propertyScope;
	}
}
