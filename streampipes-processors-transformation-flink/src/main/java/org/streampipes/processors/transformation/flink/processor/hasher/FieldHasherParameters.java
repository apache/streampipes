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

package org.streampipes.processors.transformation.flink.processor.hasher;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithmType;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class FieldHasherParameters extends EventProcessorBindingParams {
	
	private String propertyName;
	private HashAlgorithmType hashAlgorithmType;
	
	public FieldHasherParameters(DataProcessorInvocation graph, String propertyName, HashAlgorithmType hashAlgorithmType) {
		super(graph);
		this.propertyName = propertyName;
		this.hashAlgorithmType = hashAlgorithmType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public HashAlgorithmType getHashAlgorithmType() {
		return hashAlgorithmType;
	}
	
}
