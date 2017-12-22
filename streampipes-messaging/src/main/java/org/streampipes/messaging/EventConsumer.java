package org.streampipes.messaging;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.grounding.TransportProtocol;

public interface EventConsumer<TP extends TransportProtocol> {

    void connect(TP protocolSettings, InternalEventProcessor<byte[]> eventProcessor) throws
            SpRuntimeException;

    void disconnect() throws SpRuntimeException;

    Boolean isConnected();
}
