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

package org.apache.streampipes.sdk.stream;

import org.apache.streampipes.commons.Utils;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class SchemaBuilder {

  private EventSchema schema;
  private List<EventProperty> properties;

  private SchemaBuilder() {
    this.schema = new EventSchema();
    this.properties = new ArrayList<>();
  }

  public static SchemaBuilder create() {
    return new SchemaBuilder();
  }

  public SchemaBuilder simpleProperty(String label, String description, String runtimeName, String subPropertyOf,
                                      String dataType) {
    EventPropertyPrimitive primitive =
        new EventPropertyPrimitive(dataType, runtimeName, "", Utils.createURI(subPropertyOf));
    primitive.setDescription(description);
    primitive.setLabel(label);
    properties.add(primitive);
    return this;
  }

  public SchemaBuilder properties(List<EventProperty> properties) {
    this.properties.addAll(properties);
    return this;
  }

  public EventSchema build() {
    schema.setEventProperties(properties);
    return schema;
  }


}
