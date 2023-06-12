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

package org.apache.streampipes.pe.flink.processor.stat.summary;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOutputStrategy;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventSchema;
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
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.Statistics;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class StatisticsSummaryController extends FlinkDataProcessorDeclarer<StatisticsSummaryParameters> implements
    ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor> {

  private static final String listPropertyMappingName = "list-property";

  public static final String MIN = "min";
  public static final String MAX = "max";
  public static final String SUM = "sum";
  public static final String STDDEV = "stddev";
  public static final String VARIANCE = "variance";
  public static final String MEAN = "mean";
  public static final String N = "n";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.statistics.flink.statistics-summary")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithNaryMapping(EpRequirements.listRequirement(Datatypes
                .Number), Labels.withId(listPropertyMappingName), PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .outputStrategy(OutputStrategies.customTransformation())
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<StatisticsSummaryParameters> getProgram(
      DataProcessorInvocation graph,
      ProcessingElementParameterExtractor extractor) {
    List<String> listPropertyMappings = extractor.mappingPropertyValues(listPropertyMappingName);

    StatisticsSummaryParameters params = new StatisticsSummaryParameters(graph, listPropertyMappings);

    return new StatisticsSummaryProgram(params);

  }

  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
                                           ProcessingElementParameterExtractor extractor) throws SpRuntimeException {

    EventSchema eventSchema = processingElement.getInputStreams().get(0).getEventSchema();
    List<String> listPropertyMappings = extractor.mappingPropertyValues(listPropertyMappingName);

    for (String property : listPropertyMappings) {
      String propertyPrefix = StringUtils.substringAfterLast(property, ":");

      eventSchema.addEventProperty(EpProperties.doubleEp(Labels.empty(), propertyPrefix + "_" + MIN, Statistics.MIN));
      eventSchema.addEventProperty(EpProperties.doubleEp(Labels.empty(), propertyPrefix + "_" + MAX, Statistics.MAX));
      eventSchema.addEventProperty(EpProperties.doubleEp(Labels.empty(), propertyPrefix + "_" + SUM, Statistics.SUM));
      eventSchema.addEventProperty(
          EpProperties.doubleEp(Labels.empty(), propertyPrefix + "_" + STDDEV, Statistics.STDDEV));
      eventSchema.addEventProperty(
          EpProperties.doubleEp(Labels.empty(), propertyPrefix + "_" + VARIANCE, Statistics.VARIANCE));
      eventSchema.addEventProperty(EpProperties.doubleEp(Labels.empty(), propertyPrefix + "_" + MEAN, Statistics.MEAN));
      eventSchema.addEventProperty(EpProperties.doubleEp(Labels.empty(), propertyPrefix + "_" + N, Statistics.N));
    }
    return eventSchema;
  }
}
