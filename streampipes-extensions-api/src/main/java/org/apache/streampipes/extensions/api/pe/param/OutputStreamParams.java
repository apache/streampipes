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
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.EventSchema;

import java.io.Serializable;
import java.util.List;

public class OutputStreamParams implements Serializable {

  private final SourceInfo sourceInfo;
  private final SchemaInfo schemaInfo;

  public OutputStreamParams(SpDataStream outputStream, List<PropertyRenameRule>
      propertyRenameRules) {
    this.schemaInfo = makeSchemaInfo(outputStream.getEventSchema(), propertyRenameRules);
    this.sourceInfo = makeSourceInfo(outputStream.getEventGrounding());

  }

  private SchemaInfo makeSchemaInfo(EventSchema eventSchema, List<PropertyRenameRule> renameRules) {
    return new SchemaInfo(eventSchema, renameRules);
  }

  private SourceInfo makeSourceInfo(EventGrounding eventGrounding) {
    return new SourceInfo(eventGrounding.getTransportProtocol().getTopicDefinition()
        .getActualTopicName(), "o");
  }

  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  public SchemaInfo getSchemaInfo() {
    return schemaInfo;
  }
}
