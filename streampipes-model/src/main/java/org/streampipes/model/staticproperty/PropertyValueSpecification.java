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
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.SO;

import javax.persistence.Entity;

@RdfsClass(SO.PropertyValueSpecification)
@Entity
public class PropertyValueSpecification extends UnnamedStreamPipesEntity {

	private static final long serialVersionUID = 1L;
	
	@RdfProperty(SO.MinValue)
	private double minValue;
	
	@RdfProperty(SO.MaxValue)
	private double maxValue;
	
	@RdfProperty(SO.Step)
	private double step;

	public PropertyValueSpecification(double minValue, double maxValue,
			double step) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
	}
	
	public PropertyValueSpecification(PropertyValueSpecification other)
	{
		super();
		this.minValue = other.getMinValue();
		this.maxValue = other.getMaxValue();
		this.step = other.getStep();
	}
	
	public PropertyValueSpecification()
	{
		super();
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}
	
}
