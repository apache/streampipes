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

package org.apache.streampipes.connect.shared.preprocessing.convert;

import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaConverter {

  public EventSchema toOriginalSchema(EventSchema transformedSchema,
                                      List<TransformationRuleDescription> transformationRules) {

    var converter = new ToOriginalSchemaConverter(
        transformedSchema.getEventProperties()
    );

    return transform(transformationRules, converter, true);
  }

  public EventSchema toTransformedSchema(EventSchema originalSchema,
                                         List<TransformationRuleDescription> transformationRules) {

    var converter = new ToTransformedSchemaConverter(
        originalSchema.getEventProperties()
    );

    return transform(transformationRules, converter, false);
  }

  private EventSchema transform(List<TransformationRuleDescription> transformationRules,
                                ProvidesConversionResult converter,
                                boolean reverseRules) {
    var rules = transformationRules
        .stream()
        .sorted(Comparator.comparingInt(TransformationRuleDescription::getRulePriority))
        .collect(Collectors.toCollection(ArrayList::new));

    if (reverseRules) {
      Collections.reverse(rules);
    }

    rules.forEach(rule -> rule.accept(converter));
    return new EventSchema(converter.getTransformedProperties());
  }
}
