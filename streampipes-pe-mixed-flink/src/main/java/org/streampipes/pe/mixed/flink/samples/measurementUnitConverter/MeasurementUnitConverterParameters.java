package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class MeasurementUnitConverterParameters extends EventProcessorBindingParams {
    public MeasurementUnitConverterParameters(DataProcessorInvocation graph) {
        super(graph);
    }
}
