package org.streampipes.connect.events;

public class EventObjectEndEvent extends Event {
    private byte[] payload;

    public EventObjectEndEvent(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
