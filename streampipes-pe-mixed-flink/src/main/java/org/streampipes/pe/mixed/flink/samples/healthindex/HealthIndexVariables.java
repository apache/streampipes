package org.streampipes.pe.mixed.flink.samples.healthindex;

import java.io.Serializable;

public class HealthIndexVariables implements Serializable {

    private Double frictionCoefficientNominal;
    private Double frictionCoefficientStdDev;
    private Integer frictionCoefficientStdDevMultiplier;
    private Integer frictionCoefficientDegradationRate;
    private Integer mtbf;

    private Double degradationRateBase;
    private Double degradationRateDivider;
    private Double degradationValueMultiplier;
    private Double degradationValueOffset;

    public HealthIndexVariables() {

    }

    public HealthIndexVariables(Double frictionCoefficientNominal, Double frictionCoefficientStdDev) {
        this.frictionCoefficientNominal = frictionCoefficientNominal;
        this.frictionCoefficientStdDev = frictionCoefficientStdDev;
    }

    public Double getFrictionCoefficientNominal() {
        return frictionCoefficientNominal;
    }

    public void setFrictionCoefficientNominal(Double frictionCoefficientNominal) {
        this.frictionCoefficientNominal = frictionCoefficientNominal;
    }

    public Double getFrictionCoefficientStdDev() {
        return frictionCoefficientStdDev;
    }

    public void setFrictionCoefficientStdDev(Double frictionCoefficientStdDev) {
        this.frictionCoefficientStdDev = frictionCoefficientStdDev;
    }

    public Integer getFrictionCoefficientStdDevMultiplier() {
        return frictionCoefficientStdDevMultiplier;
    }

    public void setFrictionCoefficientStdDevMultiplier(Integer frictionCoefficientStdDevMultiplier) {
        this.frictionCoefficientStdDevMultiplier = frictionCoefficientStdDevMultiplier;
    }

    public Integer getMtbf() {
        return mtbf;
    }

    public void setMtbf(Integer mtbf) {
        this.mtbf = mtbf;
    }

    public Double getDegradationRateBase() {
        return degradationRateBase;
    }

    public void setDegradationRateBase(Double degradationRateBase) {
        this.degradationRateBase = degradationRateBase;
    }

    public Double getDegradationRateDivider() {
        return degradationRateDivider;
    }

    public void setDegradationRateDivider(Double degradationRateDivider) {
        this.degradationRateDivider = degradationRateDivider;
    }

    public Double getDegradationValueMultiplier() {
        return degradationValueMultiplier;
    }

    public void setDegradationValueMultiplier(Double degradationValueMultiplier) {
        this.degradationValueMultiplier = degradationValueMultiplier;
    }

    public Double getDegradationValueOffset() {
        return degradationValueOffset;
    }

    public void setDegradationValueOffset(Double degradationValueOffset) {
        this.degradationValueOffset = degradationValueOffset;
    }

    public Integer getFrictionCoefficientDegradationRate() {
        return frictionCoefficientDegradationRate;
    }

    public void setFrictionCoefficientDegradationRate(Integer frictionCoefficientDegradationRate) {
        this.frictionCoefficientDegradationRate = frictionCoefficientDegradationRate;
    }
}
