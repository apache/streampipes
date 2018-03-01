package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import com.github.jqudt.Unit;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class MeasurementUnitConverterParameters extends EventProcessorBindingParams {

    private String convertProperty;
    private Unit inputUnit;
    private Unit outputUnit;

    public MeasurementUnitConverterParameters(DataProcessorInvocation graph, String convertProperty, Unit inputUnit,
                                              Unit outputUnit) {
        super(graph);
        this.convertProperty = convertProperty;
        this.inputUnit = inputUnit;
        this.outputUnit = outputUnit;
    }

    public String getConvertProperty() {
        return convertProperty;
    }

    public Unit getInputUnit() {
        return inputUnit;
    }

    public Unit getOutputUnit() {
        return outputUnit;
    }

}
