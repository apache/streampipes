package org.streampipes.messaging;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.impl.TransportProtocol;

/**
 * Created by riemer on 01.10.2016.
 */
public interface EventConsumer<TP extends TransportProtocol> {

    void connect(TP protocolSettings, InternalEventProcessor<byte[]> eventProcessor) throws
            SpRuntimeException;

    void disconnect() throws SpRuntimeException;
}
