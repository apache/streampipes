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

package org.streampipes.wrapper.flink;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

import java.util.Map;


public abstract class FlinkDataSinkRuntime<B extends EventSinkBindingParams> extends FlinkRuntime<B, DataSinkInvocation>{

	private static final long serialVersionUID = 1L;

	public FlinkDataSinkRuntime(B params)
	{
		super(params);
	}

	public FlinkDataSinkRuntime(B params, FlinkDeploymentConfig config)
	{
		super(params, config);
	}

	@Override
	public void appendExecutionConfig(DataStream<Map<String, Object>>... convertedStream) {
		getSink(convertedStream);

	}

	public abstract void getSink(DataStream<Map<String, Object>>... convertedStream1);

}
