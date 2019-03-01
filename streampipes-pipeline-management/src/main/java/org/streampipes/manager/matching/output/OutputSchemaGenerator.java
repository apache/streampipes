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
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.helpers.Tuple2;

public abstract class OutputSchemaGenerator<T extends OutputStrategy> {

  protected T outputStrategy;

  public OutputSchemaGenerator(T outputStrategy) {
    this.outputStrategy = outputStrategy;
  }

  public abstract Tuple2<EventSchema, T> buildFromOneStream(SpDataStream stream);

  public abstract Tuple2<EventSchema, T> buildFromTwoStreams(SpDataStream stream1,
                                                                          SpDataStream stream2);

  public Tuple2<EventSchema, T> makeTuple(EventSchema eventSchema) {
    return new Tuple2<>(eventSchema, outputStrategy);
  }
}
