package org.streampipes.wrapper.flink.samples.healthindex;

/**
 * Created by riemer on 13.11.2016.
 */
public class HealthIndexCalculationFormulas2 {

    public static Double calculateRateC(Double oldValue, Double newValue, Integer deltacx, Double stddev) {
        return (newValue - oldValue) / (deltacx * stddev);
    }

    public static Double calculateDeltaC(Double currentValue, Double averageValue, Double stddev, Integer deltacx) {
        return (currentValue - averageValue) / (deltacx * stddev);
    }

    public static Double calculatePart1(Double deltac, Integer divider, Integer power) {
        return Math.pow(power, Math.tanh(deltac / divider));
    }

    public static Double calculatePart2(Double deltac) {
        return 0.5 * Math.tanh(deltac) + 0.5;
    }

    public static Double calculateExp(Double deltac, Double part1, Double part2) {
        return deltac * part1 * part2;
    }

    public static Double calculateHealthIndex(double exp) {
        return Math.exp(-Math.abs(exp));
    }
}
