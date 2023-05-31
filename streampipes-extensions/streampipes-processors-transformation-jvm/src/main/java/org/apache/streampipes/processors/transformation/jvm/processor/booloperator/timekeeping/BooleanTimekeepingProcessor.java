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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.LinkedList;

public class BooleanTimekeepingProcessor extends StreamPipesDataProcessor {
  // Measures time and returns count

  public static final String LEFT_FIELD_ID = "left-field";
  public static final String RIGHT_FIELD_ID = "right-field";

  public static final String TIME_FIELD_ID = "time-field";
  public static final String COUNT_FIELD_ID = "count-field";

  public static final String OUTPUT_UNIT_ID = "outputUnit";
  private static final String MILLISECONDS = "Milliseconds";
  private static final String SECONDS = "Seconds";
  private static final String MINUTES = "Minutes";

  private String leftFieldName;
  private String rightFieldName;

  private boolean leftFieldLast;
  private boolean rightFieldLast;

  private LinkedList<Long> allPending;
  private Long counter;

  private double outputDivisor;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.jvm.booloperator.timekeeping")
        .category(DataProcessorType.BOOLEAN_OPERATOR, DataProcessorType.TIME)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON, "time_measure_example.png")
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.booleanReq(),
                Labels.withId(LEFT_FIELD_ID),
                PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(
                EpRequirements.booleanReq(),
                Labels.withId(RIGHT_FIELD_ID),
                PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(OUTPUT_UNIT_ID), Options.from(MILLISECONDS, SECONDS, MINUTES))
        .outputStrategy(OutputStrategies.append(
            EpProperties.numberEp(Labels.withId(TIME_FIELD_ID),
                "measured_time",
                "http://schema.org/Number"),
            EpProperties.numberEp(Labels.withId(COUNT_FIELD_ID),
                "counter",
                "http://schema.org/Number")

        ))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    leftFieldName = extractor.mappingPropertyValue(LEFT_FIELD_ID);
    rightFieldName = extractor.mappingPropertyValue(LEFT_FIELD_ID);
    String outputUnit = extractor.selectedSingleValue(OUTPUT_UNIT_ID, String.class);
    leftFieldLast = false;
    rightFieldLast = false;
    allPending = new LinkedList<>();
    counter = 0L;

    outputDivisor = 1.0;
    if (outputUnit.equals(SECONDS)) {
      outputDivisor = 1000.0;
    } else if (outputUnit.equals(MINUTES)) {
      outputDivisor = 60000.0;
    }
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector collector) throws SpRuntimeException {
    boolean leftField = inputEvent.getFieldBySelector(leftFieldName).getAsPrimitive().getAsBoolean();
    boolean rightField = inputEvent.getFieldBySelector(rightFieldName).getAsPrimitive().getAsBoolean();


    if (!rightFieldLast && rightField) {
      if (this.allPending.size() > 0) {
        Long startTime = this.allPending.removeLast();

        Long timeDifference = System.currentTimeMillis() - startTime;

        double result = timeDifference / this.outputDivisor;

        this.counter++;

        if (this.counter == Long.MAX_VALUE) {
          this.counter = 0L;
        }

        inputEvent.addField("measured_time", result);
        inputEvent.addField("counter", this.counter);

        collector.collect(inputEvent);
      }
    }


    if (!leftFieldLast && leftField) {
      this.allPending.push(System.currentTimeMillis());
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
