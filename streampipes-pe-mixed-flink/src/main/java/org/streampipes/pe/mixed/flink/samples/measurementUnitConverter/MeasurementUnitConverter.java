package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class MeasurementUnitConverter implements FlatMapFunction<Map<String, Object>, Map<String, Object>>  {

    private String unitName;
    private Unit inputUnit;
    private Unit outputtUnit;

    public MeasurementUnitConverter(MeasurementUnitConverterParameters params) {
        unitName = params.getUnitName();
        inputUnit = params.getInputUnit();
        outputtUnit = params.getOutputtUnit();
    }

    @Override
    public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
        double value = (double) in.get(unitName);

        Quantity obs = new Quantity(value, inputUnit);
        double newValue = obs.convertTo(outputtUnit).getValue();

        in.put(unitName, newValue);

        out.collect(in);
    }
}
