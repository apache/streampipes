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
package org.apache.streampipes.connect.iiot.adapters.oi4.model;

import org.apache.streampipes.connect.iiot.adapters.oi4.serializer.PayloadDeserializer;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Implementation of data set message as defined by the Open Industry 4.0 Alliance.
 *
 * @see <a href="https://openindustry4.com/fileadmin/Dateien/Downloads/OEC_Development_Guideline_V1.1.1.pdf" >Open
 *      Insdustry 4.0 Alliance Development Guideline, p.82</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DataSetMessage(@JsonProperty("DataSetWriterId") String datasetWriterId,
        @JsonProperty("Timestamp") String timestamp, @JsonProperty("Filter") String filter,
        @JsonProperty("Source") String source,
        @JsonProperty("Payload") @JsonDeserialize(using = PayloadDeserializer.class) Map<String, Object> payload) {

}
