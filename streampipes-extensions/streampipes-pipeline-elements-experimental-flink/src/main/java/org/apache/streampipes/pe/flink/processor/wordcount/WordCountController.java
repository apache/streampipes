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

package org.apache.streampipes.pe.flink.processor.wordcount;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

public class WordCountController extends FlinkDataProcessorDeclarer<WordCountParameters> {

  private static final String WORD_COUNT_FIELD_KEY = "wordcountField";
  private static final String TIME_WINDOW_KEY = "timeWindow";
  private static final String WORD_KEY = "word";
  private static final String COUNT_KEY = "count";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.textmining.flink.wordcount")
        .withAssets(Assets.DOCUMENTATION)
        .withLocales(Locales.EN)
        .category(DataProcessorType.AGGREGATE)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.stringReq(),
                Labels.withId(WORD_COUNT_FIELD_KEY),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.fixed(EpProperties.stringEp(
                Labels.withId(WORD_KEY),
                "word",
                "http://schema.org/text"),
            EpProperties.integerEp(Labels.withId(COUNT_KEY),
                "count", "http://schema.org/number")))
        .requiredIntegerParameter(Labels.withId(TIME_WINDOW_KEY))
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<WordCountParameters> getProgram(DataProcessorInvocation graph,
                                                                   ProcessingElementParameterExtractor extractor) {

    String fieldName = extractor.mappingPropertyValue(WORD_COUNT_FIELD_KEY);
    Integer timeWindowValue = extractor.singleValueParameter(TIME_WINDOW_KEY, Integer.class);

    return new WordCountProgram(new WordCountParameters(graph, fieldName, timeWindowValue));

  }
}
