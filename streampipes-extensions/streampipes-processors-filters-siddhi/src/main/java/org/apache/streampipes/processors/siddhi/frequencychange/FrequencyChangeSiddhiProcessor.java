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
package org.apache.streampipes.processors.siddhi.frequencychange;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfigBuilder;
import org.apache.streampipes.wrapper.siddhi.SiddhiQueryBuilder;
import org.apache.streampipes.wrapper.siddhi.engine.StreamPipesSiddhiProcessor;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;

public class FrequencyChangeSiddhiProcessor extends StreamPipesSiddhiProcessor {

  private static final String DURATION = "duration";
  private static final String TIME_UNIT = "timeUnit";
  private static final String INCREASE = "increase";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.frequencychange")
        .category(DataProcessorType.FILTER)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredSingleValueSelection(Labels.withId(TIME_UNIT), Options.from("sec", "min",
            "hrs"))
        .requiredIntegerParameter(Labels.withId(INCREASE), 0, 500, 1)
        .outputStrategy(OutputStrategies.custom(true))
        .requiredIntegerParameter(Labels.withId(DURATION))
        .build();
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams siddhiParams,
                                        String finalInsertIntoStreamName) {

    var extractor = siddhiParams.getParams().extractor();
    int duration = extractor.singleValueParameter(DURATION, Integer.class);
    String timeUnit = extractor.selectedSingleValue(TIME_UNIT, String.class);
    int increase = extractor.selectedSingleValue(INCREASE, Integer.class);

    InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName);
    return SiddhiAppConfigBuilder
        .create()
        .addQuery(SiddhiQueryBuilder.create(fromStatement(siddhiParams, duration), insertIntoClause)
            .withSelectClause(selectStatement(siddhiParams))
            .build())
        .build();
  }

  private String fromStatement(SiddhiProcessorParams siddhiParams,
                               int duration) {
    return "from every not "
        + siddhiParams.getInputStreamNames().get(0)
        + " for "
        + duration
        + " sec";
  }

  private String selectStatement(SiddhiProcessorParams siddhiParams) {
    return SelectClause.createWildcard().toSiddhiEpl();
  }
}
