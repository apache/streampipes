package org.streampipes.wrapper.flink.samples.healthindex;

import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by riemer on 25.10.2016.
 */
public class HealthIndexCalculator extends AbstractHealthIndexCalculator {

    private HealthIndexVariables variables;
    private Double nominalHealthIndex;

    public HealthIndexCalculator(String frictionValueKey, String timestampKey, String machineTypeKey, HealthIndexVariables variables) {
        super(frictionValueKey, timestampKey, machineTypeKey);
        this.variables = variables;
        this.nominalHealthIndex = 1 / (double) variables.getMtbf();
    }

    @Override
    public void apply(GlobalWindow window, Iterable<Map<String, Object>> iterable, Collector<Map<String, Object>> collector) {
        Iterator<Map<String, Object>> iterator = iterable.iterator();
        Map<String, Object> oldObject = iterator.next();

        if (iterator.hasNext()) {
            Map<String, Object> newObject = iterator.next();

            Double currentFrictionCoefficientValue = (Double) newObject.get(frictionValueKey);
            Double lastFrictionCoefficientValue = (Double) oldObject.get(frictionValueKey);

            System.out.println("Current Friction Value: " + currentFrictionCoefficientValue);
            System.out.println("Last Friction Value: " + lastFrictionCoefficientValue);

            Long currentTimestamp = (Long) newObject.get(timestampKey);
            Long lastTimestamp = (Long) oldObject.get(timestampKey);

            Long timeDifference = (currentTimestamp - lastTimestamp);

            Double gamma = HealthIndexCalculationFormulas.calculateGamma(variables.getFrictionCoefficientStdDevMultiplier(), variables.getFrictionCoefficientStdDev());

            Double degradationValue = HealthIndexCalculationFormulas
                    .calculateDegradationValue(currentFrictionCoefficientValue, variables.getFrictionCoefficientNominal(), gamma);

            System.out.println("Degradation value: " + degradationValue);

            Double degradationRate = HealthIndexCalculationFormulas
                    .calculateDegradationRate(currentFrictionCoefficientValue,
                            lastFrictionCoefficientValue,
                            variables.getFrictionCoefficientDegradationRate(),
                            variables.getFrictionCoefficientStdDev());

            System.out.println("Degradation rate: " + degradationRate);

            Double healthIndex = HealthIndexCalculationFormulas.calculateHealthIndex(nominalHealthIndex,
                    timeDifference,
                    degradationRate,
                    degradationValue,
                    variables.getDegradationRateBase(),
                    variables.getDegradationRateDivider(),
                    variables.getDegradationValueMultiplier(),
                    variables.getDegradationValueOffset());

            System.out.println("Health index: " + healthIndex);

            collector.collect(makeOutputEvent(System.currentTimeMillis(), healthIndex, (String) newObject.get(machineTypeKey)));
        }
    }
}
