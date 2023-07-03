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
package org.apache.streampipes.processors.siddhi.topk;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfigBuilder;
import org.apache.streampipes.wrapper.siddhi.SiddhiQueryBuilder;
import org.apache.streampipes.wrapper.siddhi.engine.StreamPipesSiddhiProcessor;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.output.SiddhiListOutputConfig;
import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.GroupByClause;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.LimitClause;
import org.apache.streampipes.wrapper.siddhi.query.OrderByClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expressions;
import org.apache.streampipes.wrapper.siddhi.query.expression.SiddhiTimeUnit;

import io.siddhi.query.api.execution.query.selection.OrderByAttribute;

import java.util.Arrays;

public class TopK extends StreamPipesSiddhiProcessor {

  private static final String VALUE_KEY = "value-key";
  private static final String COUNT_KEY = "count-key";
  private static final String WINDOW_SIZE = "window-size";
  private static final String SCALE_KEY = "scale";
  private static final String LIMIT_KEY = "limit";
  private static final String ORDER_KEY = "order";

  private static final String HOURS_INTERNAL_NAME = "HOURS";
  private static final String MINUTES_INTERNAL_NAME = "MINUTES";
  private static final String SECONDS_INTERNAL_NAME = "SECONDS";

  private static final String ORDER_ASCENDING_NAME = "ASC";
  private static final String ORDER_DESCENDING_NAME = "DESC";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.topk")
        .withLocales(Locales.EN)
        .category(DataProcessorType.TRANSFORM)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(), Labels.withId
                (VALUE_KEY), PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.integerReq(), Labels.withId
                (COUNT_KEY), PropertyScope.NONE)
            .build())
        .requiredIntegerParameter(Labels.withId(LIMIT_KEY))
        .requiredIntegerParameter(Labels.withId(WINDOW_SIZE))
        .requiredSingleValueSelection(Labels.withId(SCALE_KEY),
            Options.from(new Tuple2<>("Hours", HOURS_INTERNAL_NAME),
                new Tuple2<>("Minutes", MINUTES_INTERNAL_NAME),
                new Tuple2<>("Seconds", SECONDS_INTERNAL_NAME)))
        .requiredSingleValueSelection(Labels.withId(ORDER_KEY),
            Options.from(new Tuple2<>("Ascending", ORDER_ASCENDING_NAME),
                new Tuple2<>("Descending", ORDER_DESCENDING_NAME)))
        .outputStrategy(OutputStrategies.fixed(EpProperties.listNestedEp(Labels.withId("top"), "top",
            Arrays.asList(EpProperties.integerEp(Labels.withId("count"), "count", "http://schema.org/count"),
                EpProperties.stringEp(Labels.withId("value"), "value", SO.TEXT)))))
        .build();
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams siddhiParams,
                                        String finalInsertIntoStreamName) {
    IDataProcessorParameterExtractor extractor = siddhiParams.getParams().extractor();
    Integer windowSize = extractor.singleValueParameter(WINDOW_SIZE, Integer.class);
    String valueSelector = extractor.mappingPropertyValue(VALUE_KEY);
    String countSelector = extractor.mappingPropertyValue(COUNT_KEY);
    Integer limit = extractor.singleValueParameter(LIMIT_KEY, Integer.class);
    SiddhiTimeUnit scale = toTimeUnit(extractor.selectedSingleValueInternalName(SCALE_KEY, String.class));
    OrderByAttribute.Order order =
        OrderByAttribute.Order.valueOf(extractor.selectedSingleValueInternalName(ORDER_KEY, String.class));

    FromClause fromClause = FromClause.create();
    fromClause.add(
        Expressions.stream(siddhiParams.getInputStreamNames().get(0), Expressions.timeBatch(windowSize, scale)));

    SelectClause selectClause = SelectClause.create(Expressions.as(Expressions.property(valueSelector), "value"),
        Expressions.as(Expressions.property(countSelector), "count"));
    GroupByClause groupByClause = GroupByClause.create(Expressions.property(valueSelector));
    OrderByClause orderByClause = OrderByClause.create(Expressions.orderBy(Expressions.property("count"), order));

    InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName, true);
    LimitClause limitClause = LimitClause.create(limit);

    return SiddhiAppConfigBuilder.create(new SiddhiListOutputConfig("top", true))
        .addQuery(SiddhiQueryBuilder
            .create(fromClause, insertIntoClause)
            .withSelectClause(selectClause)
            .withGroupByClause(groupByClause)
            .withOrderByClause(orderByClause)
            .withLimitClause(limitClause)
            .build())
        .build();
  }

  private SiddhiTimeUnit toTimeUnit(String scale) {
    return SiddhiTimeUnit.valueOf(scale);
  }
}
