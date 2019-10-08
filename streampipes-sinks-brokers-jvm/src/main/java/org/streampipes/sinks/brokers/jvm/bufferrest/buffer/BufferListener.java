package org.streampipes.sinks.brokers.jvm.bufferrest.buffer;

public interface BufferListener {
    void bufferFull(String messagesJsonArray);
}
