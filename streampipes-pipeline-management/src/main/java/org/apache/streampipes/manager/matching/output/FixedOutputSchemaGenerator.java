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
import org.apache.streampipes.model.output.FixedOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.util.List;

public class FixedOutputSchemaGenerator extends OutputSchemaGenerator<FixedOutputStrategy> {

  private List<EventProperty> fixedProperties;

  public static FixedOutputSchemaGenerator from(OutputStrategy strategy) {
    return new FixedOutputSchemaGenerator((FixedOutputStrategy) strategy);
  }

  public FixedOutputSchemaGenerator(FixedOutputStrategy strategy) {
    super(strategy);
    this.fixedProperties = strategy.getEventProperties();
  }

  @Override
  public Tuple2<EventSchema, FixedOutputStrategy> buildFromOneStream(SpDataStream stream) {
    return makeTuple(new EventSchema(fixedProperties));
  }

  @Override
  public Tuple2<EventSchema, FixedOutputStrategy> buildFromTwoStreams(SpDataStream stream1,
                                                                      SpDataStream stream2) {
    return buildFromOneStream(stream1);
  }
}
