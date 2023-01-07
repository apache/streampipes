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
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.output.CustomOutputStrategy;
import org.apache.streampipes.model.output.OutputStrategy;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.Tuple2;

import java.util.List;

public class CustomOutputSchemaGenerator extends OutputSchemaGenerator<CustomOutputStrategy> {

  private List<String> selectedPropertyKeys;

  public static CustomOutputSchemaGenerator from(OutputStrategy strategy) {
    return new CustomOutputSchemaGenerator((CustomOutputStrategy) strategy);
  }

  public CustomOutputSchemaGenerator(CustomOutputStrategy strategy) {
    super(strategy);
    this.selectedPropertyKeys = strategy.getSelectedPropertyKeys();
  }

  @Override
  public Tuple2<EventSchema, CustomOutputStrategy> buildFromOneStream(SpDataStream stream) {
    return new Tuple2<>(new EventSchema(new PropertySelector(stream.getEventSchema())
        .createPropertyList(selectedPropertyKeys)), outputStrategy);
  }

  @Override
  public Tuple2<EventSchema, CustomOutputStrategy> buildFromTwoStreams(SpDataStream stream1,
                                                                       SpDataStream stream2) {

    Tuple2<List<EventProperty>, List<PropertyRenameRule>> generatedOutputProperties = new
        PropertySelector(stream1.getEventSchema(),
        stream2.getEventSchema()).createRenamedPropertyList(selectedPropertyKeys);

    EventSchema outputSchema = new EventSchema(generatedOutputProperties.k);

    return new Tuple2<>(outputSchema, getModifiedOutputStrategy(generatedOutputProperties.v));
  }

  private CustomOutputStrategy getModifiedOutputStrategy(List<PropertyRenameRule> propertyRenameRules) {
    outputStrategy.setRenameRules(propertyRenameRules);
    return outputStrategy;
  }
}
