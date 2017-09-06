package org.streampipes.messaging;

import org.streampipes.model.impl.TransportProtocol;

public interface SpProtocolDefinition<TP extends TransportProtocol> {

  EventConsumer<TP> getConsumer();

  EventProducer<TP> getProducer();
}
