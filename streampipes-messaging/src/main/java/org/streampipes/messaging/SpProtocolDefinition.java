package org.streampipes.messaging;

public interface SpProtocolDefinition<RAW> {

  String getTransportFormatRdfUri();

  EventConsumer<RAW> getConsumer();

  EventProducer<RAW> getProducer();
}
