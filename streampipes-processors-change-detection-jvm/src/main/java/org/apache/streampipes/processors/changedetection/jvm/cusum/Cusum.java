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

package org.apache.streampipes.processors.changedetection.jvm.cusum;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

public class Cusum implements EventProcessor<CusumParameters> {

    private String selectedNumberMapping;
    private Double k;
    private Double h;
    private Double cusumLow;
    private Double cusumHigh;
    private WelfordAggregate welfordAggregate;

    @Override
    public void onInvocation(CusumParameters parameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
        this.selectedNumberMapping = parameters.getSelectedNumberMapping();
        k = parameters.getParamK();
        h = parameters.getParamH();
        cusumLow = 0.0;
        cusumHigh = 0.0;
        welfordAggregate = new WelfordAggregate();
    }

    @Override
    public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
        Double number = event.getFieldBySelector(selectedNumberMapping).getAsPrimitive().getAsDouble();
        welfordAggregate.update(number);  // update mean and standard deviation
        Double normalized = getZScoreNormalizedValue(number);
        updateStatistics(normalized);


        Boolean isChangeHigh = getTestResult(cusumHigh, h);
        Boolean isChangeLow = getTestResult(cusumLow, h);

        Event updatedEvent = updateEvent(event, cusumLow, cusumHigh, isChangeLow, isChangeHigh);
        collector.collect(updatedEvent);

        if (isChangeHigh || isChangeLow) {
            resetAfterChange();
        }
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        cusumLow = 0.0;
        cusumHigh = 0.0;
    }

    private void resetAfterChange() {
        cusumHigh = 0.0;
        cusumLow = 0.0;
        welfordAggregate = new WelfordAggregate();
    }

    private void updateStatistics(Double newValue) {
        if (newValue.isNaN()) {
            return;
        }
        cusumHigh = Math.max(0, cusumHigh + newValue - k);
        cusumLow = Math.min(0, cusumLow + newValue + k);
    }

    private Boolean getTestResult(Double cusum, Double h) {
        return Math.abs(cusum) > h;
    }

    private Event updateEvent(Event event, Double cusumLow, Double cusumHigh, Boolean decisionLow, Boolean decisionHigh) {
        event.addField(CusumEventFields.VAL_LOW, cusumLow);
        event.addField(CusumEventFields.VAL_HIGH, cusumHigh);
        event.addField(CusumEventFields.DECISION_LOW, decisionLow);
        event.addField(CusumEventFields.DECISION_HIGH, decisionHigh);
        return event;
    }

    private Double getZScoreNormalizedValue(Double value) {
        Double mean = welfordAggregate.getMean();
        Double std = welfordAggregate.getSampleStd();
        return (value - mean) / std;
    }

}
