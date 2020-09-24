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

package org.apache.streampipes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DataProcessorType {

	FILTER("Filter", "b"),
	AGGREGATE("Aggregation", ""),
	ENRICH_TEXT("Text Functions", ""),
	ENRICH("Enrichment", ""),
	GEO("Geospatial Operations", ""),
	PATTERN_DETECT("Pattern Detection", ""),
	ALGORITHM("Algorithm", ""),
	TRANSFORM("Transformation", ""),
	UNCATEGORIZED("Uncategorized", "");
	
	private String label;
	private String description;
	
	DataProcessorType(String label, String description) {
		this.label = label;
		this.description = description;
	}

	public String getCode() {
		return this.name();
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	@JsonCreator
	public static AdapterType fromString(JsonNode json) {
		return AdapterType.valueOf(json.get("code").asText());
	}
}
