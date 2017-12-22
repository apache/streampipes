package org.streampipes.pe.mixed.flink.samples.healthindex;

import java.io.Serializable;

public class HealthIndexVariables2 implements Serializable {

    private Integer deltacx;
    private Integer divider;
    private Integer power;

    private Double average;
    private Double stddev;

    public HealthIndexVariables2(Integer deltacx, Integer divider, Integer power, Double average, Double stddev) {
        this.deltacx = deltacx;
        this.divider = divider;
        this.power = power;
        this.average = average;
        this.stddev = stddev;
    }

    public HealthIndexVariables2() {
    }

    public Integer getDeltacx() {
        return deltacx;
    }

    public void setDeltacx(Integer deltacx) {
        this.deltacx = deltacx;
    }

    public Integer getDivider() {
        return divider;
    }

    public void setDivider(Integer divider) {
        this.divider = divider;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double getStddev() {
        return stddev;
    }

    public void setStddev(Double stddev) {
        this.stddev = stddev;
    }
}
