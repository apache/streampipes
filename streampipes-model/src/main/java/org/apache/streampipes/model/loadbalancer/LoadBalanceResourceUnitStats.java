package org.apache.streampipes.model.loadbalancer;

public class LoadBalanceResourceUnitStats<T> {

    private double eventRateIn;

    private double eventThroughputIn;

    private double eventRateOut;

    private double eventThroughputOut;

    private long lastUpdate;

    private LoadBalanceResourceUnit<T> unit;

    public LoadBalanceResourceUnitStats() {
        this.lastUpdate = System.currentTimeMillis();
    }

    public double getEventRateIn() {
        return eventRateIn;
    }

    public void setEventRateIn(double eventRateIn) {
        this.eventRateIn = eventRateIn;
    }

    public double getEventThroughputIn() {
        return eventThroughputIn;
    }

    public void setEventThroughputIn(double eventThroughputIn) {
        this.eventThroughputIn = eventThroughputIn;
    }

    public double getEventRateOut() {
        return eventRateOut;
    }

    public void setEventRateOut(double eventRateOut) {
        this.eventRateOut = eventRateOut;
    }

    public double getEventThroughputOut() {
        return eventThroughputOut;
    }

    public void setEventThroughputOut(double eventThroughputOut) {
        this.eventThroughputOut = eventThroughputOut;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LoadBalanceResourceUnit<T> getUnit() {
        return unit;
    }

    public void setUnit(LoadBalanceResourceUnit<T> unit) {
        this.unit = unit;
    }
}