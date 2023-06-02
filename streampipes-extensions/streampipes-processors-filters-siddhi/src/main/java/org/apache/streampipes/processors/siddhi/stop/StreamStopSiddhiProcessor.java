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
package org.apache.streampipes.processors.siddhi.stop;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
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
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;

import java.util.Arrays;

public class StreamStopSiddhiProcessor extends StreamPipesSiddhiProcessor {

  private static final String Duration = "duration";
  private static final String Message = "message";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.stop")
        .withLocales(Locales.EN)
        .category(DataProcessorType.FILTER)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .outputStrategy(OutputStrategies.fixed(
            Arrays.asList(
                EpProperties.timestampProperty("timestamp"),
                EpProperties.stringEp(Labels.withId(Message),
                    "message", "http://schema.org/text")
            )
        ))
        .requiredIntegerParameter(Labels.withId(Duration))
        .build();
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams siddhiParams,
                                        String finalInsertIntoStreamName) {
    var extractor = siddhiParams.getParams().extractor();
    int duration = extractor.singleValueParameter(Duration, Integer.class);

    InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName);

    return SiddhiAppConfigBuilder
        .create()
        .addQuery(SiddhiQueryBuilder
            .create(fromStatement(siddhiParams, duration), insertIntoClause)
            .withSelectClause(selectStatement(siddhiParams))
            .build())
        .build();
  }

  private String fromStatement(SiddhiProcessorParams siddhiParams,
                               int duration) {
    return "define stream Test(timestamp LONG,message STRING);\n"
        + "from every not "
        + siddhiParams.getInputStreamNames().get(0)
        + " for " + duration
        + " sec";
  }

  private String selectStatement(SiddhiProcessorParams siddhiParams) {
    return "select currentTimeMillis() as timestamp, 'Event stream has stopped' as message";
  }
}
