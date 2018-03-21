package org.streampipes.connect;


import java.util.ArrayList;
import java.util.List;

public class GetNEvents implements EmitBinaryEvent {

    private int n;
    private List<byte[]> events;

    public GetNEvents(int n) {
        this.n = n;
        this.events = new ArrayList<>();
    }

    @Override
    public Boolean emit(byte[] event) {

        events.add(event);
        this.n = n - 1;

        if (n == 0) {
            return false;
        } else {
            return true;
        }
    }

    public List<byte[]> getEvents() {
        return events;
    }
}
