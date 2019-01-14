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

import org.streampipes.model.SpDataStream;
import org.streampipes.model.output.FixedOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.helpers.Tuple2;

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
