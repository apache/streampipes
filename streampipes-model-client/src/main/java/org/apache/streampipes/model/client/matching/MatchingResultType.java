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

package org.apache.streampipes.model.client.matching;

public enum MatchingResultType {

  DATATYPE_MATCH("Datatype Match", "A required datatype is not present in the input event property."),
  STREAM_MATCH("Stream Match", ""),
  DOMAIN_PROPERTY_MATCH("Domain Property Match",
      "A required domain property is not present in the input event property."),
  FORMAT_MATCH("Format Match", "No supported transport format found."),
  GROUNDING_MATCH("Grounding Match", ""),
  PROTOCOL_MATCH("Protocol Match", "No supported communication protocol found."),
  PROPERTY_MATCH("Property Match", "A required property is not present in the input event schema."),
  SCHEMA_MATCH("Schema Match", "A required schema is not present in the input event stream."),
  MEASUREMENT_UNIT_MATCH("Measurement Unit Match", ""),
  STREAM_QUALITY("Stream Quality Match", "");

  private String title;
  private String description;

  MatchingResultType(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

}
