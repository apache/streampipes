package org.streampipes.messaging;

import org.streampipes.model.grounding.TransportProtocol;

public abstract class SpProtocolDefinitionFactory<T extends TransportProtocol> {

  public abstract String getTransportProtocolClass();

  public abstract SpProtocolDefinition<T> createInstance();
}
