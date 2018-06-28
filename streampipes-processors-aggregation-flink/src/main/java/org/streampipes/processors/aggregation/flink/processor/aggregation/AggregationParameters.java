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

package org.streampipes.processors.aggregation.flink.processor.aggregation;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class AggregationParameters extends EventProcessorBindingParams {

	private AggregationType aggregationType;
	private int outputEvery;
	private int timeWindowSize;
	private String aggregate;
	private List<String> groupBy;
	private List<String> selectProperties;
	
	public AggregationParameters(DataProcessorInvocation graph, AggregationType aggregationType, int outputEvery, List<String> groupBy, String aggregate, int timeWindowSize, List<String> selectProperties) {
		super(graph);
		this.aggregationType = aggregationType;
		this.outputEvery = outputEvery;
		this.groupBy = groupBy;
		this.timeWindowSize = timeWindowSize;
		this.aggregate = aggregate;
		this.selectProperties = selectProperties;
	}

	public AggregationType getAggregationType() {
		return aggregationType;
	}

	public int getOutputEvery() {
		return outputEvery;
	}

	public List<String> getGroupBy() {
		return groupBy;
	}

	public int getTimeWindowSize() {
		return timeWindowSize;
	}

	public String getAggregate() {
		return aggregate;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}

}
