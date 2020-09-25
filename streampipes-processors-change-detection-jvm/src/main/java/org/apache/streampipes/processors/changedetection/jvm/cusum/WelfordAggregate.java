package org.apache.streampipes.processors.changedetection.jvm.cusum;

public class WelfordAggregate {
    private Integer count;
    private Double mean;
    private Double m2;

    public WelfordAggregate() {
        count = 0;
        mean = 0.0;
        m2 = 0.0;
    }

    public void update(Double newValue) {
        count++;
        Double delta = mean != null ? newValue - mean : 0.0;
        mean += delta / count;
        Double delta2 = newValue - mean;
        m2 += delta * delta2;
    }

    public Double getMean() {
        return mean;
    }

    public Double getPopulationVariance() {
        return m2 / count;
    }

    public Double getSampleVariance() {
        return m2 / (count - 1);
    }

    public Double getSampleStd() {
        return Math.sqrt(getSampleVariance());
    }

    public Double getPopulationStd() {
        return Math.sqrt(getPopulationVariance());
    }
}
