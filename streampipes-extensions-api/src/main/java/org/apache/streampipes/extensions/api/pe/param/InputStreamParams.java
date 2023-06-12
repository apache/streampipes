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

package org.apache.streampipes.extensions.api.pe.param;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.util.SchemaUtils;

import java.io.Serializable;
import java.util.List;

public class InputStreamParams implements Serializable {

  private static final long serialVersionUID = -240772928651344246L;

  private final EventGrounding eventGrounding;
  private final EventSchema eventSchema;
  private final String inName;
  private final SourceInfo sourceInfo;
  private final SchemaInfo schemaInfo;

  public InputStreamParams(Integer streamId,
                           SpDataStream inputStream,
                           List<PropertyRenameRule> propertyRenameRules) {
    super();
    this.eventGrounding = inputStream.getEventGrounding();
    this.inName = eventGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();
    this.eventSchema = inputStream.getEventSchema();
    this.sourceInfo = makeSourceInfo(streamId);
    this.schemaInfo = makeSchemaInfo(propertyRenameRules);
  }

  private SchemaInfo makeSchemaInfo(List<PropertyRenameRule> renameRules) {
    return new SchemaInfo(eventSchema, renameRules);
  }

  private SourceInfo makeSourceInfo(Integer streamId) {
    return new SourceInfo(eventGrounding.getTransportProtocol().getTopicDefinition()
        .getActualTopicName(), makeStreamPrefix(streamId));
  }

  private String makeStreamPrefix(Integer streamId) {
    return streamId == 0 ? PropertySelectorConstants.FIRST_STREAM_ID_PREFIX :
        PropertySelectorConstants.SECOND_STREAM_ID_PREFIX;
  }

  public EventGrounding getEventGrounding() {
    return eventGrounding;
  }

  public String getInName() {
    return inName;
  }

  public List<String> getAllProperties() {
    return SchemaUtils.toPropertyList(eventSchema.getEventProperties());
  }

  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  public SchemaInfo getSchemaInfo() {
    return schemaInfo;
  }
}
