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

package org.apache.streampipes.processors.filters.jvm.processor.movingaverage;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.filters.jvm.processor.movingaverage.util.MovingAverageFilter;
import org.apache.streampipes.processors.filters.jvm.processor.movingaverage.util.MovingFilter;
import org.apache.streampipes.processors.filters.jvm.processor.movingaverage.util.MovingMedianFilter;
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
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

;

public class MovingAverageProcessor extends StreamPipesDataProcessor {

  private static final String RESULT_FIELD = "filterResult";
  private static final String NUMBER_VALUE = "number";
  private static final String N_VALUE = "n";
  private static final String METHOD_KEY = "method";
  private static final String MEAN_INTERNAL_NAME = "MEAN";
  private static final String MEDIAN_INTERNAL_NAME = "MEDIAN";


  private String numberName;
  private MovingFilter filter;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.movingaverage")
        .category(DataProcessorType.FILTER)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                Labels.withId(NUMBER_VALUE),
                PropertyScope.NONE)
            .build())
        .requiredIntegerParameter(Labels.withId(N_VALUE))
        .requiredSingleValueSelection(Labels.withId(METHOD_KEY),
            Options.from(new Tuple2<>("mean", MEAN_INTERNAL_NAME),
                new Tuple2<>("median", MEDIAN_INTERNAL_NAME)))
        .outputStrategy(
            OutputStrategies.append(
                EpProperties.numberEp(Labels.empty(), RESULT_FIELD, SO.NUMBER)))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.numberName = parameters.extractor().mappingPropertyValue(NUMBER_VALUE);
    int n = parameters.extractor().singleValueParameter(N_VALUE, Integer.class);
    String methode = parameters.extractor().selectedSingleValueInternalName(METHOD_KEY, String.class);
    if (methode.equals(MEDIAN_INTERNAL_NAME)) {
      filter = new MovingMedianFilter(n);
    } else {
      filter = new MovingAverageFilter(n);
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    Double result = filter.update(event.getFieldBySelector(numberName).getAsPrimitive().getAsDouble());
    event.addField(RESULT_FIELD, result);
    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
