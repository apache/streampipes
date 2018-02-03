package org.streampipes.messaging;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.grounding.TransportProtocol;

import java.io.Serializable;

public interface EventProducer<TP extends TransportProtocol> extends Serializable {

    void connect(TP protocolSettings) throws SpRuntimeException;

    void publish(byte[] event);

    void disconnect() throws SpRuntimeException;

    Boolean isConnected();
}
