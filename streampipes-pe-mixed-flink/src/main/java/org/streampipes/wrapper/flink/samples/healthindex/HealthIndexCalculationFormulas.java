package org.streampipes.wrapper.flink.samples.healthindex;

/**
 * Created by riemer on 17.10.2016.
 */
public class HealthIndexCalculationFormulas {

    public static Double calculateGamma(Integer multiplier, Double frictionCoefficientStdDev) {
        return multiplier * frictionCoefficientStdDev;
    }

    public static Double calculateDegradationValue(Double frictionCoefficientCurrent, Double frictionCoefficientNominal, Double gamma) {
        return (frictionCoefficientCurrent - frictionCoefficientNominal) / gamma;
    }

    public static Double calculateDegradationRate(Double frictionCoefficientCurrent, Double frictionCoefficientLast, Integer timeDifference, Double frictionCoefficientStdDev) {
        return (frictionCoefficientCurrent - frictionCoefficientLast) / (timeDifference * frictionCoefficientStdDev);
    }

    public static Double calculateHealthIndex(Double nominalHealthIndex,
                                              Long timeDifference,
                                              Double degradationRate,
                                              Double degradationValue,
                                              Double degradationRateBase,
                                              Double degradationRateDivider,
                                              Double degradationValueMultiplier,
                                              Double degradationValueOffset) {

        return nominalHealthIndex
                * Math.exp(timeDifference *
                Math.pow(degradationRateBase, Math.tanh(degradationRate / degradationRateDivider)) *
                (degradationValueMultiplier * Math.tanh(degradationValue) + degradationValueOffset));
    }

}
