package org.streampipes.messaging;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.impl.TransportProtocol;

import java.io.Serializable;

/**
 * Created by riemer on 01.10.2016.
 */
public interface EventProducer<TP extends TransportProtocol> extends Serializable {

    void connect(TP protocolSettings) throws SpRuntimeException;

    void publish(byte[] event);

    void disconnect() throws SpRuntimeException;

    Boolean isConnected();
}
