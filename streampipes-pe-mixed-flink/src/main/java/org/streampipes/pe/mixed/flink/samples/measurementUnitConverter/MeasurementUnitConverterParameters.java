package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import com.github.jqudt.Unit;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class MeasurementUnitConverterParameters extends EventProcessorBindingParams {

    private String unitName;
    private Unit inputUnit;
    private Unit outputtUnit;

    public MeasurementUnitConverterParameters(DataProcessorInvocation graph, String unitName, Unit inputUnit, Unit outputtUnit) {
        super(graph);
        this.unitName = unitName;
        this.inputUnit = inputUnit;
        this.outputtUnit = outputtUnit;
    }

    public String getUnitName() {
        return unitName;
    }

    public Unit getInputUnit() {
        return inputUnit;
    }

    public Unit getOutputtUnit() {
        return outputtUnit;
    }

}
