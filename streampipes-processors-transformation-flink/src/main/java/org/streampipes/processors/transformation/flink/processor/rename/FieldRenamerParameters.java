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

package org.streampipes.processors.transformation.flink.processor.rename;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class FieldRenamerParameters extends EventProcessorBindingParams {

	private String oldPropertyName;
	private String newPropertyName;
	
	public FieldRenamerParameters(DataProcessorInvocation graph, String oldPropertyName, String newPropertyName) {
		super(graph);
		this.oldPropertyName = oldPropertyName;
		this.newPropertyName = newPropertyName;
	}

	public String getOldPropertyName() {
		return oldPropertyName;
	}

	public void setOldPropertyName(String oldPropertyName) {
		this.oldPropertyName = oldPropertyName;
	}

	public String getNewPropertyName() {
		return newPropertyName;
	}

	public void setNewPropertyName(String newPropertyName) {
		this.newPropertyName = newPropertyName;
	}

	
}
