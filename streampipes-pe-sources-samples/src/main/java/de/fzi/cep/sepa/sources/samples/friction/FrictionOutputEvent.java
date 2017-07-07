package de.fzi.cep.sepa.sources.samples.friction;

/**
 * Created by riemer on 26.10.2016.
 */
public class FrictionOutputEvent {

    private long timestamp;
    private double zScore;
    private double value;
    private double std;
    private String eventId;

    public FrictionOutputEvent(long timestamp, double zScore, double value, double std, String eventId) {
        this.timestamp = timestamp;
        this.zScore = zScore;
        this.value = value;
        this.std = std;
        this.eventId = eventId;
    }

    public FrictionOutputEvent() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getzScore() {
        return zScore;
    }

    public void setzScore(double zScore) {
        this.zScore = zScore;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
