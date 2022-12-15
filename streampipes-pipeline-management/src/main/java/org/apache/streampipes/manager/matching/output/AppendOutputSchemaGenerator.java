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

import org.apache.streampipes.manager.selector.PropertySelector;
import org.apache.streampipes.manager.selector.PropertySelectorGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.output.AppendOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.util.List;

public class AppendOutputSchemaGenerator extends OutputSchemaGenerator<AppendOutputStrategy> {

  private List<EventProperty> appendProperties;

  public static AppendOutputSchemaGenerator from(OutputStrategy strategy) {
    return new AppendOutputSchemaGenerator((AppendOutputStrategy) strategy);
  }

  public AppendOutputSchemaGenerator(AppendOutputStrategy strategy) {
    super(strategy);
    this.appendProperties = strategy.getEventProperties();
  }

  @Override
  public Tuple2<EventSchema, AppendOutputStrategy> buildFromOneStream(SpDataStream stream) {
    List<String> selectors =
        new PropertySelectorGenerator(stream.getEventSchema(), true).generateSelectors();

    Tuple2<List<EventProperty>, List<PropertyRenameRule>> generatedOutputProperties = new
        PropertySelector(stream.getEventSchema())
        .createRenamedPropertyList(selectors, appendProperties);

    EventSchema outputSchema = new EventSchema(generatedOutputProperties.k);

    return new Tuple2<>(outputSchema, getModifiedOutputStrategy(generatedOutputProperties.v));
  }

  @Override
  public Tuple2<EventSchema, AppendOutputStrategy> buildFromTwoStreams(SpDataStream stream1,
                                                                       SpDataStream stream2) {
    List<String> selectors =
        new PropertySelectorGenerator(stream1.getEventSchema(), stream2.getEventSchema(),
            true).generateSelectors();

    Tuple2<List<EventProperty>, List<PropertyRenameRule>> generatedOutputProperties = new
        PropertySelector(stream1.getEventSchema(), stream2.getEventSchema())
        .createRenamedPropertyList(selectors, appendProperties);

    EventSchema outputSchema = new EventSchema(generatedOutputProperties.k);

    return new Tuple2<>(outputSchema, getModifiedOutputStrategy(generatedOutputProperties.v));
  }

  private AppendOutputStrategy getModifiedOutputStrategy(List<PropertyRenameRule> propertyRenameRules) {
    outputStrategy.setRenameRules(propertyRenameRules);
    return outputStrategy;
  }


}
