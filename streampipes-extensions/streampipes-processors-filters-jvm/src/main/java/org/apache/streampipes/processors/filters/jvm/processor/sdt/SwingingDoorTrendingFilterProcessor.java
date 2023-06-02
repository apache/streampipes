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

package org.apache.streampipes.processors.filters.jvm.processor.sdt;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class SwingingDoorTrendingFilterProcessor extends StreamPipesDataProcessor {

  private static final String SDT_TIMESTAMP_FIELD_KEY = "sdt-timestamp-field";
  private static final String SDT_VALUE_FIELD_KEY = "sdt-value-field";
  private static final String SDT_COMPRESSION_DEVIATION_KEY = "sdt-compression-deviation";
  private static final String SDT_COMPRESSION_MIN_INTERVAL_KEY = "sdt-compression-min-interval";
  private static final String SDT_COMPRESSION_MAX_INTERVAL_KEY = "sdt-compression-max-interval";

  private String sdtTimestampField;
  private String sdtValueField;

  private float sdtCompressionDeviation = Float.MAX_VALUE;
  private long sdtCompressionMinTimeInterval = 0L;
  private long sdtCompressionMaxTimeInterval = Long.MAX_VALUE;

  private SwingingDoorTrendingFilter sdtFilter;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.sdt")
        .category(DataProcessorType.FILTER)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                Labels.withId(SDT_TIMESTAMP_FIELD_KEY), PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                Labels.withId(SDT_VALUE_FIELD_KEY), PropertyScope.NONE)
            .build())
        .requiredFloatParameter(Labels.withId(SDT_COMPRESSION_DEVIATION_KEY))
        .requiredLongParameter(Labels.withId(SDT_COMPRESSION_MIN_INTERVAL_KEY), 0L)
        .requiredLongParameter(Labels.withId(SDT_COMPRESSION_MAX_INTERVAL_KEY), Long.MAX_VALUE)
        .outputStrategy(OutputStrategies.keep())
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    // extract field names on stream
    sdtTimestampField = parameters.extractor().mappingPropertyValue(SDT_TIMESTAMP_FIELD_KEY);
    sdtValueField = parameters.extractor().mappingPropertyValue(SDT_VALUE_FIELD_KEY);

    // extract & check sdt compression params
    sdtCompressionDeviation = parameters.extractor().singleValueParameter(SDT_COMPRESSION_DEVIATION_KEY, Float.class);
    sdtCompressionMinTimeInterval =
        parameters.extractor().singleValueParameter(SDT_COMPRESSION_MIN_INTERVAL_KEY, Long.class);
    sdtCompressionMaxTimeInterval =
        parameters.extractor().singleValueParameter(SDT_COMPRESSION_MAX_INTERVAL_KEY, Long.class);
    checkSdtCompressionParams();

    sdtFilter = new SwingingDoorTrendingFilter(sdtCompressionDeviation, sdtCompressionMinTimeInterval,
        sdtCompressionMaxTimeInterval);
  }

  /**
   * @throws SpRuntimeException throw if the followings are not satisfied:
   *                            0 < sdtCompressionDeviation
   *                            0 <= sdtCompressionMinTimeInterval < sdtCompressionMaxTimeInterval <= Long.MAX_VALUE
   */
  private void checkSdtCompressionParams() {
    if (sdtCompressionDeviation <= 0) {
      throw new SpRuntimeException(
          String.format("Compression Deviation should be positive! Actual value: %f. ", sdtCompressionDeviation));
    }

    if (sdtCompressionMinTimeInterval < 0) {
      throw new SpRuntimeException(
          String.format("Compression Minimum Time Interval should be >= 0! Actual value: %d. ",
              sdtCompressionMinTimeInterval));
    }

    if (sdtCompressionMaxTimeInterval <= sdtCompressionMinTimeInterval) {
      throw new SpRuntimeException(
          String.format(
              "Compression Minimum Time Interval should be < Compression Maximum Time Interval! "
                  + "Actual: Compression Minimum Time Interval(%d), Compression Maximum Time Interval(%d). ",
              sdtCompressionMinTimeInterval, sdtCompressionMaxTimeInterval));
    }
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    final long timestamp = event.getFieldBySelector(sdtTimestampField).getAsPrimitive().getAsLong();
    final double value = event.getFieldBySelector(sdtValueField).getAsPrimitive().getAsDouble();

    if (sdtFilter.filter(timestamp, value, event)) {
      sdtFilter.forward(collector);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    // nothing to do
  }
}
