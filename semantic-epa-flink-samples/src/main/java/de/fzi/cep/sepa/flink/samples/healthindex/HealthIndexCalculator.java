package de.fzi.cep.sepa.flink.samples.healthindex;

import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by riemer on 25.10.2016.
 */
public class HealthIndexCalculator implements AllWindowFunction<Map<String, Object>, Map<String, Object>, GlobalWindow> {

    private HealthIndexVariables variables;
    private String machineTypeKey;
    private String timestampKey;
    private String frictionValueKey;
    private Double nominalHealthIndex;

    public HealthIndexCalculator(String frictionValueKey, String timestampkey, String machineTypeKey, HealthIndexVariables variables) {
        this.frictionValueKey = frictionValueKey;
        this.timestampKey = timestampkey;
        this.machineTypeKey = machineTypeKey;
        this.variables = variables;
        this.nominalHealthIndex = 1 / (double) variables.getMtbf();
    }

    private Map<String, Object> makeOutputEvent(long timestamp, double healthIndexValue, String machineType) {
        Map<String, Object> outputEvent = new HashMap<>();
        outputEvent.put("timestamp", timestamp);
        outputEvent.put("healthIndex", healthIndexValue);
        outputEvent.put("machineId", machineType);

        return outputEvent;
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
                            timeDifference,
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
