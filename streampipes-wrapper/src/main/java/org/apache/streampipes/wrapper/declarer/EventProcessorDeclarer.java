/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.wrapper.declarer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.apache.streampipes.model.Response;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.runtime.PipelineElementRuntime;

public abstract class EventProcessorDeclarer<B extends EventProcessorBindingParams, EPR extends
				PipelineElementRuntime> extends PipelineElementDeclarer<B, EPR, DataProcessorInvocation,
				ProcessingElementParameterExtractor> implements
				SemanticEventProcessingAgentDeclarer {

	public static final Logger logger = LoggerFactory.getLogger(EventProcessorDeclarer.class.getCanonicalName());

	@Override
	protected ProcessingElementParameterExtractor getExtractor(DataProcessorInvocation graph) {
		return ProcessingElementParameterExtractor.from(graph);
	}

	@Override
	public Response invokeRuntime(DataProcessorInvocation graph) {
		return invokeEPRuntime(graph);
	}

	//If not overwritten elements are regarded as stateless
	@Override
	public void setState(String state) {
	}

	@Override
	public String getState() {
		return null;
	}
}
