/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.processors.pattern.detection.flink.processor.increase;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class IncreaseParameters extends EventProcessorBindingParams {

	private Operation operation;
	private int increase;
	private int duration;
	
	private String mapping;
	private String groupBy;
	private String timestampField;

	public IncreaseParameters(DataProcessorInvocation invocationGraph,
			Operation operation, int increase, int duration,
			String mapping, String groupBy, String timestampField) {
		super(invocationGraph);
		this.operation = operation;
		this.increase = increase;
		this.duration = duration;
		this.mapping = mapping;
		this.groupBy = groupBy;
		this.timestampField = timestampField;
	}


	public Operation getOperation() {
		return operation;
	}

	public int getIncrease() {
		return increase;
	}

	public int getDuration() {
		return duration;
	}

	public String getMapping() {
		return mapping;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public String getTimestampField() {
		return timestampField;
	}
}
