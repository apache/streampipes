package org.streampipes.messaging;

import org.streampipes.model.impl.TransportProtocol;

public interface SpProtocolDefinition<TP extends TransportProtocol> {

  String getTransportFormatRdfUri();

  EventConsumer<TP> getConsumer();

  EventProducer<TP> getProducer();
}
