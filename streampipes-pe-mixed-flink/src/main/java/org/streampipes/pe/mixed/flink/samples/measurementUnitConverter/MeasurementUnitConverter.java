package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class MeasurementUnitConverter implements FlatMapFunction<Map<String, Object>, Map<String, Object>>  {

    private String convertProperty;
    private Unit inputUnit;
    private Unit outputUnit;

    public MeasurementUnitConverter(MeasurementUnitConverterParameters params) {
        convertProperty = params.getConvertProperty();
        inputUnit = params.getInputUnit();
        outputUnit = params.getOutputUnit();
    }

    @Override
    public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
        double value = (double) in.get(convertProperty);

        Quantity obs = new Quantity(value, inputUnit);
        double newValue = obs.convertTo(outputUnit).getValue();

        in.put(convertProperty, newValue);

        out.collect(in);
    }
}
