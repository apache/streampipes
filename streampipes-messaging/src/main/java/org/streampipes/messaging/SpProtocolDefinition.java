package org.streampipes.messaging;

import org.streampipes.model.grounding.TransportProtocol;

public interface SpProtocolDefinition<TP extends TransportProtocol> {

  EventConsumer<TP> getConsumer();

  EventProducer<TP> getProducer();
}
