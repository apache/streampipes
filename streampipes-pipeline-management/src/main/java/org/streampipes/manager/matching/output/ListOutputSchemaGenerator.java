/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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
 *
 */

package org.streampipes.manager.matching.output;

import org.streampipes.commons.Utils;
import org.streampipes.empire.core.empire.SupportsRdfId;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.output.ListOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.helpers.Tuple2;

import java.net.URI;
import java.util.List;

public class ListOutputSchemaGenerator extends OutputSchemaGenerator<ListOutputStrategy> {

  private String propertyName;

  public static ListOutputSchemaGenerator from(OutputStrategy strategy) {
    return new ListOutputSchemaGenerator((ListOutputStrategy) strategy);
  }

  public ListOutputSchemaGenerator(ListOutputStrategy strategy) {
    super(strategy);
    this.propertyName = strategy.getPropertyName();
  }

  @Override
  public Tuple2<EventSchema, ListOutputStrategy> buildFromOneStream(SpDataStream stream) {
    return makeTuple(makeList(stream.getEventSchema().getEventProperties()));
  }

  @Override
  public Tuple2<EventSchema, ListOutputStrategy> buildFromTwoStreams(SpDataStream stream1,
                                         SpDataStream stream2) {
    return buildFromOneStream(stream1);
  }

  private EventSchema makeList(List<EventProperty> schemaProperties) {
    EventPropertyList list = new EventPropertyList();
    list.setEventProperties(schemaProperties);
    list.setRuntimeName(propertyName);
    list.setRdfId(new SupportsRdfId.URIKey(URI.create(schemaProperties.get(0).getRdfId() + "-list")));
    EventSchema schema = new EventSchema();
    schema.setEventProperties(Utils.createList(list));
    return schema;
  }
}
