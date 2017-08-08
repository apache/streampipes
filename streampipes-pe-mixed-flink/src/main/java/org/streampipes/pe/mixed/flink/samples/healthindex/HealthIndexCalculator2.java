package org.streampipes.pe.mixed.flink.samples.healthindex;

import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by riemer on 13.11.2016.
 */
public class HealthIndexCalculator2 extends AbstractHealthIndexCalculator {

    private HealthIndexVariables2 variables;


    public HealthIndexCalculator2(String frictionValueKey, String timestampKey, String machineTypeKey, HealthIndexVariables2 variables) {
        super(frictionValueKey, timestampKey, machineTypeKey);
        this.variables = variables;
    }

    @Override
    public void apply(GlobalWindow window, Iterable<Map<String, Object>> iterable, Collector<Map<String, Object>> collector) {
        Iterator<Map<String, Object>> iterator = iterable.iterator();
        Map<String, Object> oldObject = iterator.next();

        if (iterator.hasNext()) {
            Map<String, Object> newObject = iterator.next();

            Double currentFrictionCoefficientValue = (Double) newObject.get(frictionValueKey);
            Double lastFrictionCoefficientValue = (Double) oldObject.get(frictionValueKey);


            Double ratec = HealthIndexCalculationFormulas2
                    .calculateRateC(lastFrictionCoefficientValue, currentFrictionCoefficientValue, variables.getDeltacx(), variables.getStddev());

            Double deltac = HealthIndexCalculationFormulas2
                    .calculateDeltaC(currentFrictionCoefficientValue, variables.getAverage(), variables.getStddev(), variables.getDeltacx());

            Double part1 = HealthIndexCalculationFormulas2
                    .calculatePart1(deltac, variables.getDivider(), variables.getPower());

            Double part2 = HealthIndexCalculationFormulas2
                    .calculatePart2(deltac);

            Double exp = HealthIndexCalculationFormulas2
                    .calculateExp(deltac, part1, part2);

            Double healthIndex = HealthIndexCalculationFormulas2
                    .calculateHealthIndex(exp);

            System.out.println(healthIndex);

            collector.collect(makeOutputEvent(System.currentTimeMillis(), healthIndex, (String) newObject.get(machineTypeKey)));
        }
    }
}
