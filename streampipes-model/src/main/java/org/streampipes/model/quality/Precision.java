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

@RdfsClass(SSN.PRECISION)
@Entity
public class Precision extends EventPropertyQualityDefinition {

	private static final long serialVersionUID = -1090184880089982077L;
	
	@RdfProperty(StreamPipes.HAS_QUANTITY_VALUE)
	private float quantityValue;

	public Precision() {
		super();
	}
	
	public Precision(float quantityValue) {
		this.quantityValue = quantityValue;
	}
	
	public Precision(Precision other) {
		super(other);
		this.quantityValue = other.getQuantityValue();
	}
	
	public float getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(float quantityValue) {
		this.quantityValue = quantityValue;
	}

}
