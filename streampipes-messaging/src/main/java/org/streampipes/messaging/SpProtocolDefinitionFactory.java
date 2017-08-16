package org.streampipes.messaging;

import org.streampipes.model.impl.TransportProtocol;

public abstract class SpProtocolDefinitionFactory<T extends TransportProtocol> {

  public abstract String getTransportProtocolClass();

  public abstract SpProtocolDefinition<T> createInstance();
}
