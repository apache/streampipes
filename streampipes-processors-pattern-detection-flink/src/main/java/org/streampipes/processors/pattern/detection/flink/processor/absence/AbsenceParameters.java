/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
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

package org.streampipes.processors.pattern.detection.flink.processor.absence;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.pattern.detection.flink.processor.and.TimeUnit;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.ArrayList;
import java.util.List;

public class AbsenceParameters extends EventProcessorBindingParams {

	private static final long serialVersionUID = 4319341875274736697L;
	
	private List<String> selectProperties = new ArrayList<>();
	private Integer timeWindowSize;
	private TimeUnit timeUnit;
	
	public AbsenceParameters(DataProcessorInvocation graph, List<String> selectProperties, Integer timeWindowSize, TimeUnit timeUnit) {
		super(graph);
		this.selectProperties = selectProperties;
		this.timeWindowSize = timeWindowSize;
		this.timeUnit = timeUnit;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}

	public Integer getTimeWindowSize() {
		return timeWindowSize;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
}
