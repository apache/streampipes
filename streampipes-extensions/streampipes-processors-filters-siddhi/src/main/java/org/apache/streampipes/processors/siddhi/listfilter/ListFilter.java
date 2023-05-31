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
package org.apache.streampipes.processors.siddhi.listfilter;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfigBuilder;
import org.apache.streampipes.wrapper.siddhi.SiddhiQueryBuilder;
import org.apache.streampipes.wrapper.siddhi.engine.StreamPipesSiddhiProcessor;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expression;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expressions;

public class ListFilter extends StreamPipesSiddhiProcessor {

  private static final String LIST_KEY = "list-key";
  private static final String REQUIRED_VALUE_KEY = "required-value";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.listfilter")
        .withLocales(Locales.EN)
        .category(DataProcessorType.FILTER)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.listRequirement(), Labels.withId
                (LIST_KEY), PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .requiredTextParameter(Labels.withId(REQUIRED_VALUE_KEY))
        .outputStrategy(OutputStrategies.keep())
        .build();
  }

  private Object extractFilterValue(String selector, IDataProcessorParameterExtractor extractor) {
    EventPropertyList prop = (EventPropertyList) extractor.getEventPropertyBySelector(selector);
    return extractor.singleValueParameter((EventPropertyPrimitive) prop.getEventProperty(), REQUIRED_VALUE_KEY);
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams siddhiParams,
                                        String finalInsertIntoStreamName) {
    String filteredFieldSelector = siddhiParams.getParams().extractor().mappingPropertyValue(LIST_KEY);
    Object filterValue = extractFilterValue(filteredFieldSelector, siddhiParams.getParams().extractor());
    FromClause fromClause = FromClause.create();

    Expression containsExp = Expressions.containsListItem(Expressions.property(filteredFieldSelector), filterValue);
    fromClause.add(Expressions.filter(Expressions.stream(siddhiParams.getInputStreamNames().get(0)), containsExp));

    SelectClause selectClause = SelectClause.createWildcard();

    InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName);

    return SiddhiAppConfigBuilder
        .create()
        .addQuery(SiddhiQueryBuilder
            .create(fromClause, insertIntoClause)
            .withSelectClause(selectClause)
            .build())
        .build();
  }
}
