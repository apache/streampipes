package org.streampipes.messaging;

import org.streampipes.model.grounding.TransportProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum SpProtocolManager {

  INSTANCE;

  private List<SpProtocolDefinitionFactory<? extends TransportProtocol>> availableProtocols;

  SpProtocolManager() {
    this.availableProtocols = new ArrayList<>();
  }

  public void register(SpProtocolDefinitionFactory<? extends TransportProtocol> protocolDefinition) {
    availableProtocols.add(protocolDefinition);
  }

  public List<SpProtocolDefinitionFactory<? extends TransportProtocol>> getAvailableProtocols() {
    return availableProtocols;
  }

  public <T extends TransportProtocol> Optional<SpProtocolDefinition<T>> findDefinition(T
                                                                         transportProtocol) {
    // TODO add RDF URI for protocol in model
    return this.availableProtocols
            .stream()
            .filter
                    (adf -> adf.getTransportProtocolClass().equals(transportProtocol.getClass()
                            .getCanonicalName()))
            .map(s -> (SpProtocolDefinitionFactory<T>) s)
            .map(SpProtocolDefinitionFactory::createInstance)
            .findFirst();

  }
}
