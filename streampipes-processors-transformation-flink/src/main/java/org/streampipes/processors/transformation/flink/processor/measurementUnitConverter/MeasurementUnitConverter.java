/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.transformation.flink.processor.measurementUnitConverter;

import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.units.UnitProvider;

import java.util.Map;

public class MeasurementUnitConverter implements FlatMapFunction<Map<String, Object>, Map<String, Object>>  {

    private String convertProperty;
    private String inputUnitUri;
    private String outputUnitUri;

    public MeasurementUnitConverter(String inputUnitUri, String
            outputUnitUri, String convertProperty) {
        this.convertProperty = convertProperty;

        this.inputUnitUri = inputUnitUri;
        this.outputUnitUri = outputUnitUri;
    }

    @Override
    public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
        Unit inputUnit = UnitProvider.INSTANCE.getUnit(inputUnitUri);
        Unit outputUnit = UnitProvider.INSTANCE.getUnit(outputUnitUri);
        double value = (double) in.get(convertProperty);

        // transform old value to new unit
        Quantity obs = new Quantity(value, inputUnit);
        double newValue = obs.convertTo(outputUnit).getValue();

        in.put(convertProperty, newValue);

        out.collect(in);
    }
}
