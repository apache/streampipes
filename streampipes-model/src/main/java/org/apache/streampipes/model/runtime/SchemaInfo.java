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
package org.apache.streampipes.model.runtime;

import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.schema.EventSchema;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SchemaInfo implements Serializable {

  private EventSchema eventSchema;
  private List<PropertyRenameRule> renameRules;

  public SchemaInfo(EventSchema eventSchema, List<PropertyRenameRule> renameRules) {
    this.eventSchema = eventSchema;
    this.renameRules = renameRules;
  }

  public EventSchema getEventSchema() {
    return eventSchema;
  }

  public List<PropertyRenameRule> getRenameRules() {
    return renameRules;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SchemaInfo that = (SchemaInfo) o;
    return Objects.equals(eventSchema, that.eventSchema);
  }
}
