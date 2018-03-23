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

package org.streampipes.model.quality;

import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

@RdfsClass(StreamPipes.EVENT_PROPERTY_QUALITY_DEFINITION)
@MappedSuperclass
@Entity
public abstract class EventPropertyQualityDefinition extends MeasurementProperty /*implements Comparable<EventPropertyQualityDefinition>*/{

	private static final long serialVersionUID = -3849772043514528797L;
	
	public EventPropertyQualityDefinition() {
		super();
	}
	
	public EventPropertyQualityDefinition(EventPropertyQualityDefinition other) {
		super(other);
	}
}
