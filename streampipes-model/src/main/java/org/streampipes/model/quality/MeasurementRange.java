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

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.SSN;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(SSN.MEASUREMENT_RANGE)
@Entity
public class MeasurementRange extends EventPropertyQualityDefinition {

	private static final long serialVersionUID = 4853190183770515968L;

	@RdfProperty(StreamPipes.HAS_MEASUREMENT_PROPERTY_MIN_VALUE)
	private float minValue;

	@RdfProperty(StreamPipes.HAS_MEASUREMENT_PROPERTY_MAX_VALUE)
	private float maxValue;
	
	public MeasurementRange() {
		super();
	}

	public MeasurementRange(float minValue, float maxValue) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public MeasurementRange(MeasurementRange other) {
		super(other);
		this.minValue = other.getMinValue();
		this.maxValue = other.getMaxValue();
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

}
