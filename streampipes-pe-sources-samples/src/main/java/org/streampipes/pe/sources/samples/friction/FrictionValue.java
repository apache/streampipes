package org.streampipes.pe.sources.samples.friction;

public class FrictionValue {

    private double value;
    private double meanTemperature;

    public FrictionValue(double value, double meanTemperature) {
        this.value = value;
        this.meanTemperature = meanTemperature;
    }

    public FrictionValue() {

    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMeanTemperature() {
        return meanTemperature;
    }

    public void setMeanTemperature(double meanTemperature) {
        this.meanTemperature = meanTemperature;
    }
}
