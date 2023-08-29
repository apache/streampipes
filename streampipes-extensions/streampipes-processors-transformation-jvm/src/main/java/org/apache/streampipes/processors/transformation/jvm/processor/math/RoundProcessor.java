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

package org.apache.streampipes.processors.transformation.jvm.processor.math;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RoundProcessor extends StreamPipesDataProcessor {

    private List<String> fieldsToBeRounded;
    private int numDigits;

    private static final String FIELDS = "fields";
    private static final String NUM_DIGITS = "num-digits";

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder
            .create("package.org.apache.streampipes.processors.transformation.jvm.processor.math", "Round", "Round numeric values")
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithNaryMapping(EpRequirements.numberReq(), Labels.from(FIELDS, "Fields to Be Rounded", "fields to be rounded"), PropertyScope.NONE)
                .build())
            .requiredIntegerParameter(Labels.from(NUM_DIGITS, "Number of Digits", "How many digits after decimal point to round/keep?"))
            .outputStrategy(OutputStrategies.keep())
            .build();
    }

    @Override
    public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
        fieldsToBeRounded = parameters.extractor().mappingPropertyValues(FIELDS);
        numDigits = parameters.extractor().singleValueParameter(NUM_DIGITS, Integer.class);
    }

    @Override
    public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
        for (String fieldToBeRounded : fieldsToBeRounded) {
            double value = event.getFieldBySelector(fieldToBeRounded).getAsPrimitive().getAsDouble();
            double roundedValue = BigDecimal.valueOf(value).setScale(numDigits, RoundingMode.HALF_UP).doubleValue();
            event.removeFieldBySelector(fieldToBeRounded);
            // Needs to remove "s0::“ in the beginning since addField uses runtime name
            event.addField(fieldToBeRounded.substring(4), roundedValue);
        }
        collector.collect(event);
    }

    @Override
    public void onDetach() {
    }
}