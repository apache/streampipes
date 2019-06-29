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

import org.streampipes.manager.selector.PropertySelector;
import org.streampipes.manager.selector.PropertySelectorGenerator;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.output.PropertyRenameRule;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.sdk.helpers.Tuple2;

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

    EventSchema outputSchema = new EventSchema(generatedOutputProperties.a);

    return new Tuple2<>(outputSchema, getModifiedOutputStrategy(generatedOutputProperties.b));
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

    EventSchema outputSchema = new EventSchema(generatedOutputProperties.a);

    return new Tuple2<>(outputSchema, getModifiedOutputStrategy(generatedOutputProperties.b));
  }

  private AppendOutputStrategy getModifiedOutputStrategy(List<PropertyRenameRule> propertyRenameRules) {
    outputStrategy.setRenameRules(propertyRenameRules);
    return outputStrategy;
  }


}
