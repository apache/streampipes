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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.output.KeepOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class RenameOutputSchemaGenerator extends OutputSchemaGenerator<KeepOutputStrategy> {


  public static RenameOutputSchemaGenerator from(OutputStrategy strategy) {
    return new RenameOutputSchemaGenerator((KeepOutputStrategy) strategy);
  }

  public RenameOutputSchemaGenerator(KeepOutputStrategy strategy) {
    super(strategy);
  }

  @Override
  public Tuple2<EventSchema, KeepOutputStrategy> buildFromOneStream(SpDataStream stream) {
    return makeTuple(stream.getEventSchema());
  }

  @Override
  public Tuple2<EventSchema, KeepOutputStrategy> buildFromTwoStreams(SpDataStream stream1,
                                                                     SpDataStream stream2) {
    EventSchema resultSchema = new EventSchema();
    List<EventProperty> properties = new ArrayList<>();
    properties.addAll(stream1.getEventSchema().getEventProperties());
    if (outputStrategy.isKeepBoth()) {
      properties.addAll(new PropertyDuplicateRemover(properties,
          stream2.getEventSchema().getEventProperties()).rename());
    }

    resultSchema.setEventProperties(properties);
    return makeTuple(resultSchema);
  }

}
