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

package org.apache.streampipes.manager.matching.output;

import org.apache.streampipes.commons.Utils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.output.ListOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

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
    // TODO SIP08
    EventPropertyList list = new EventPropertyList();
    //list.setEventProperties(schemaProperties);
    list.setRuntimeName(propertyName);
    EventSchema schema = new EventSchema();
    schema.setEventProperties(Utils.createList(list));
    return schema;
  }
}
