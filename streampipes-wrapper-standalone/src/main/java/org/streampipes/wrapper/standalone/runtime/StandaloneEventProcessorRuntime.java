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

package org.streampipes.wrapper.standalone.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.standalone.param.StandaloneEventProcessorRuntimeParams;

public class StandaloneEventProcessorRuntime extends StandalonePipelineElementRuntime<EventProcessorRuntimeParams<?>> {

	public StandaloneEventProcessorRuntime(StandaloneEventProcessorRuntimeParams<?> params) {
		super(params);
	}

	@Override
	public void discardRuntime() throws SpRuntimeException {
		params.getInputCollectors().forEach(is -> is.unregisterConsumer(instanceId));
		params.discardEngine();
		postDiscard();
	}

	@Override
	public void bindRuntime() throws SpRuntimeException {
		params.bindEngine();
		params.getInputCollectors().forEach(is -> is.registerConsumer(instanceId, params.getEngine()));
		prepareRuntime();
	}

	@Override
	public void prepareRuntime() throws SpRuntimeException {
		for (SpInputCollector spInputCollector : params.getInputCollectors()) {
			spInputCollector.connect();
		}

		params.getOutputCollector().connect();
	}

	@Override
	public void postDiscard() throws SpRuntimeException {
		for(SpInputCollector spInputCollector : params.getInputCollectors()) {
			spInputCollector.disconnect();
		}

		params.getOutputCollector().disconnect();
	}

}
