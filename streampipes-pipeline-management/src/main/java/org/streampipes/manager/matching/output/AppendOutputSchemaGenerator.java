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
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.helpers.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class AppendOutputSchemaGenerator extends OutputSchemaGenerator<AppendOutputStrategy> {

  private List<EventProperty> appendProperties;
  private List<EventProperty> properties;
  private List<EventProperty> renamedProperties;

  public static AppendOutputSchemaGenerator from(OutputStrategy strategy) {
    return new AppendOutputSchemaGenerator((AppendOutputStrategy) strategy);
  }

  public AppendOutputSchemaGenerator(AppendOutputStrategy strategy) {
    super(strategy);
    this.appendProperties = strategy.getEventProperties();
    this.properties = new ArrayList<>();
    this.renamedProperties = new ArrayList<>();
  }

  @Override
  public Tuple2<EventSchema, AppendOutputStrategy> buildFromOneStream(SpDataStream stream) {
    properties.addAll(stream.getEventSchema().getEventProperties());
    properties.addAll(renameDuplicates(stream.getEventSchema().getEventProperties()));
    return new Tuple2<>(new EventSchema(properties), getModifiedOutputStrategy());
  }

  @Override
  public Tuple2<EventSchema, AppendOutputStrategy> buildFromTwoStreams(SpDataStream stream1,
                                         SpDataStream stream2) {
    properties.addAll(renameDuplicates(stream1.getEventSchema().getEventProperties()));
    properties.addAll(renameDuplicates(stream2.getEventSchema().getEventProperties()));
    return new Tuple2<>(new EventSchema(properties), getModifiedOutputStrategy());
  }

  private List<EventProperty> renameDuplicates(List<EventProperty> oldProperties) {
    List<EventProperty> renamed = new PropertyDuplicateRemover(oldProperties, appendProperties).rename();
    this.renamedProperties.addAll(renamed);
    return renamed;
  }

  private AppendOutputStrategy getModifiedOutputStrategy() {
    outputStrategy.setEventProperties(renamedProperties);
    return outputStrategy;
  }


}
