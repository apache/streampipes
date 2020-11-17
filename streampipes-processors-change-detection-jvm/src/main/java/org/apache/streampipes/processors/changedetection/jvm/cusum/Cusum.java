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
        System.out.println("Reset after change");
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
