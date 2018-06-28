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

package org.streampipes.processors.enricher.flink.processor.timestamp;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class TimestampEnricher implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

	private String appendTimePropertyName;
	
	public TimestampEnricher(String appendTimePropertyName) {
		this.appendTimePropertyName = appendTimePropertyName;
	}

	@Override
	public void flatMap(Map<String, Object> in,
			Collector<Map<String, Object>> out) throws Exception {
		in.put(appendTimePropertyName, System.currentTimeMillis());
		out.collect(in);
	}
	
	

}
