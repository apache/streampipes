/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.wrapper.params.binding;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.output.PropertyRenameRule;
import org.streampipes.model.runtime.SchemaInfo;
import org.streampipes.model.runtime.SourceInfo;
import org.streampipes.model.schema.EventSchema;

import java.io.Serializable;
import java.util.List;

public class OutputStreamParams implements Serializable {

  private SourceInfo sourceInfo;
  private SchemaInfo schemaInfo;

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
