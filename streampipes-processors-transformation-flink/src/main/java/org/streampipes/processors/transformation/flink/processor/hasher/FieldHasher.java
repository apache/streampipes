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

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.processors.transformation.flink.processor.hasher.algorithm.HashAlgorithm;

import java.io.Serializable;
import java.util.Map;

public class FieldHasher implements Serializable, FlatMapFunction<Map<String, Object>, Map<String, Object>>{

	private HashAlgorithm hashAlgorithm;
	private String propertyName;
	
	public FieldHasher(String propertyName, HashAlgorithm hashAlgorithm) {
		this.propertyName = propertyName;
		this.hashAlgorithm = hashAlgorithm;
	}
	
	@Override
	public void flatMap(Map<String, Object> in,
			Collector<Map<String, Object>> out) throws Exception {
		in.put(propertyName, hashAlgorithm.toHashValue(in.get(propertyName)));
		out.collect(in);
	}

}
