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

package org.apache.streampipes.manager.verification.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.verification.ElementVerifier;
import org.apache.streampipes.manager.verification.SecVerifier;
import org.apache.streampipes.manager.verification.SepVerifier;
import org.apache.streampipes.manager.verification.SepaVerifier;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

public class TypeExtractor {
	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TypeExtractor.class.getCanonicalName());

	private static final String DATA_PROCESSOR_PREFIX = "sepa";
	private static final String DATA_SINK_PREFIX = "sec";
	private static final String DATA_SOURCE_PREFIX = "sep";

	private final String pipelineElementDescription;
	
	public TypeExtractor(String pipelineElementDescription) {
		this.pipelineElementDescription = pipelineElementDescription;
	}
	
	public ElementVerifier<?> getTypeVerifier() throws SepaParseException {
		try {
			ObjectNode jsonNode = JacksonSerializer
					.getObjectMapper()
					.readValue(this.pipelineElementDescription, ObjectNode.class);
			String jsonClassName = jsonNode.get("@class").asText();
			return getTypeDef(jsonClassName);
		} catch (JsonProcessingException e) {
			throw new SepaParseException();
		}
	}

	private ElementVerifier<?> getTypeDef(String jsonClassName) throws SepaParseException {
		if (jsonClassName == null) {
			throw new SepaParseException();
		} else {
			if (jsonClassName.equals(ep())) {
				LOG.debug("Detected active pipeline element type {}", DATA_SOURCE_PREFIX);
				return new SepVerifier(pipelineElementDescription);
			} else if (jsonClassName.equals(epa())) {
				LOG.info("Detected active pipeline element type {}", DATA_PROCESSOR_PREFIX);
				return new SepaVerifier(pipelineElementDescription);
			} else if (jsonClassName.equals(ec())) {
				LOG.info("Detected active pipeline element type {}", DATA_SINK_PREFIX);
				return new SecVerifier(pipelineElementDescription);
			} else throw new SepaParseException();
		}
	}
	
	private static String ep()
	{
		return DataSourceDescription.class.getCanonicalName();
	}
	
	private static String epa()
	{
		return DataProcessorDescription.class.getCanonicalName();
	}
	
	private static String ec()
	{
		return DataSinkDescription.class.getCanonicalName();
	}
	
}
