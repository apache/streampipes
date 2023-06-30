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
package org.apache.streampipes.processors.siddhi.count;

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
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfigBuilder;
import org.apache.streampipes.wrapper.siddhi.SiddhiQueryBuilder;
import org.apache.streampipes.wrapper.siddhi.engine.StreamPipesSiddhiProcessor;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.GroupByClause;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expressions;
import org.apache.streampipes.wrapper.siddhi.query.expression.SiddhiTimeUnit;

public class CountAggregation extends StreamPipesSiddhiProcessor {

  private static final String TIME_WINDOW_KEY = "time-window";
  private static final String TIMESTAMP_MAPPING_KEY = "timestamp-mapping";
  private static final String SCALE_KEY = "scale";
  private static final String COUNT_MAPPING = "count-mapping";

  static final String HOURS_INTERNAL_NAME = "HOURS";
  static final String MINUTES_INTERNAL_NAME = "MINUTES";
  static final String SECONDS_INTERNAL_NAME = "SECONDS";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.count")
        .category(DataProcessorType.COUNT_OPERATOR)
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.timestampReq(),
                Labels.withId(TIMESTAMP_MAPPING_KEY),
                PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(
                EpRequirements.anyProperty(),
                Labels.withId(COUNT_MAPPING),
                PropertyScope.DIMENSION_PROPERTY)
            .build())
        .outputStrategy(OutputStrategies.fixed(
            EpProperties.timestampProperty("timestamp"),
            EpProperties.stringEp(Labels.empty(), "value", "http://schema.org/Text"),
            EpProperties.integerEp(Labels.empty(), "count", "http://schema.org/Number")))
        .requiredIntegerParameter(Labels.withId(TIME_WINDOW_KEY))
        .requiredSingleValueSelection(Labels.withId(SCALE_KEY),
            Options.from(new Tuple2<>("Hours", HOURS_INTERNAL_NAME),
                new Tuple2<>("Minutes", MINUTES_INTERNAL_NAME),
                new Tuple2<>("Seconds", SECONDS_INTERNAL_NAME)))
        .build();
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams siddhiParams,
                                        String finalInsertIntoStreamName) {
    Integer timeWindowSize = siddhiParams.getParams().extractor().singleValueParameter(TIME_WINDOW_KEY, Integer.class);
    String scale = siddhiParams.getParams().extractor().selectedSingleValueInternalName(SCALE_KEY, String.class);
    String fieldToCount = siddhiParams.getParams().extractor().mappingPropertyValue(COUNT_MAPPING);
    String timestampField = siddhiParams.getParams().extractor().mappingPropertyValue(TIMESTAMP_MAPPING_KEY);


    FromClause fromClause = FromClause.create();
    fromClause.add(Expressions.stream(siddhiParams.getInputStreamNames().get(0),
        Expressions.timeWindow(timeWindowSize, toTimeUnit(scale))));

    SelectClause selectClause = SelectClause.create(
        Expressions.as(Expressions.property(timestampField), "timestamp"),
        Expressions.as(Expressions.property(fieldToCount), "value"),
        Expressions.as(Expressions.count(Expressions.property(fieldToCount)), "count"));

    GroupByClause groupByClause = GroupByClause.create(Expressions.property(fieldToCount));
    InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName);

    return SiddhiAppConfigBuilder.create()
        .addQuery(SiddhiQueryBuilder
            .create(fromClause, insertIntoClause)
            .withSelectClause(selectClause)
            .withGroupByClause(groupByClause)
            .build())
        .build();

  }

  private SiddhiTimeUnit toTimeUnit(String scale) {
    return SiddhiTimeUnit.valueOf(scale);
  }

}
