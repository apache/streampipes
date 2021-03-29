package org.apache.streampipes.processors.filters.jvm.processor.schema;

import org.apache.streampipes.model.runtime.Event;

import java.util.LinkedList;
import java.util.List;

public class StreamBuffer {
    private List<Event> buffer;

    public StreamBuffer() {
        buffer = new LinkedList<>();

    }

    public void add(Event event) {
        buffer.add(event);
    }


    public List<Event> getList() {
        return this.buffer;
    }

    public Event get(int i) {
        return this.buffer.get(i);
    }

    public void removeOldEvent(Event event) {
        if (buffer.contains(event)) {
            buffer.remove(event);
        }
    }

    public void reset() {
        this.buffer = new LinkedList<>();
    }
}