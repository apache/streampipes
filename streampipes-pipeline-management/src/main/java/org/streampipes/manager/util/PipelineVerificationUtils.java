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

package org.streampipes.manager.util;

import org.streampipes.commons.exceptions.NoSepaInPipelineException;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineVerificationUtils {

	/**
	 * returns the root node of a partial pipeline (a pipeline without an action)
	 * @param pipeline
	 * @return {@link org.streampipes.model.base.InvocableStreamPipesEntity}
	 */

	public static InvocableStreamPipesEntity getRootNode(Pipeline pipeline) throws NoSepaInPipelineException
	{
		List<InvocableStreamPipesEntity> elements = new ArrayList<>();
		elements.addAll(pipeline.getSepas());
		elements.addAll(pipeline.getActions());

		List<InvocableStreamPipesEntity> unconfiguredElements = elements
				.stream()
				.filter(e -> !e.isConfigured())
				.collect(Collectors.toList());


		if (unconfiguredElements.size() != 1) throw new NoSepaInPipelineException();
		else return unconfiguredElements.get(0);

	}
}
