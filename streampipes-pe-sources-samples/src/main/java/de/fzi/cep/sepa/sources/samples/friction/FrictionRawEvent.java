package de.fzi.cep.sepa.sources.samples.friction;

/**
 * Created by riemer on 26.10.2016.
 */
public class FrictionRawEvent {

    private String start;
    private String end;
    private FrictionValue gearbox;
    private FrictionValue swivel;

    public FrictionRawEvent(String start, String end, FrictionValue gearbox, FrictionValue swivel) {
        this.start = start;
        this.end = end;
        this.gearbox = gearbox;
        this.swivel = swivel;
    }

    public FrictionRawEvent() {
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public FrictionValue getGearbox() {
        return gearbox;
    }

    public void setGearbox(FrictionValue gearbox) {
        this.gearbox = gearbox;
    }

    public FrictionValue getSwivel() {
        return swivel;
    }

    public void setSwivel(FrictionValue swivel) {
        this.swivel = swivel;
    }
}
