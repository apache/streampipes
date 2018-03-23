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
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.MEASUREMENT_OBJECT)
@Entity
public class MeasurementObject extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 4391097898611686930L;
	
	@RdfProperty(StreamPipes.MEASURES_OBJECT)
	private URI measuresObject;
	
	public MeasurementObject() {
		super();
	}
	
	public MeasurementObject(MeasurementObject other) {
		super(other);
		this.measuresObject = other.getMeasuresObject();
	}
	
	public MeasurementObject(URI measurementObject) {
		super();
		this.measuresObject = measurementObject;
	}

	public URI getMeasuresObject() {
		return measuresObject;
	}

	public void setMeasuresObject(URI measurementObject) {
		this.measuresObject = measurementObject;
	}
	
	
}
