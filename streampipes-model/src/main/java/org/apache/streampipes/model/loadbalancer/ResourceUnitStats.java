package org.apache.streampipes.model.loadbalancer;

public class ResourceUnitStats {


    String resourceId;

    public double eventRateIn;

    public double eventThroughputIn;

    public double eventRateOut;

    public double eventThroughputOut;

    public long lastUpdate;

    public ResourceUnitStats(String resourceId) {
        this.resourceId = resourceId;
        this.lastUpdate = System.currentTimeMillis();
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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
}