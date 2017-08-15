package org.streampipes.messaging;

import org.streampipes.model.impl.TransportProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum SpProtocolManager {

  INSTANCE;

  private List<SpProtocolDefinition<? extends TransportProtocol>> availableProtocols;

  SpProtocolManager() {
    this.availableProtocols = new ArrayList<>();
  }

  public void register(SpProtocolDefinition<? extends TransportProtocol> protocolDefinition) {
    availableProtocols.add(protocolDefinition);
  }

  public List<SpProtocolDefinition<? extends TransportProtocol>> getAvailableProtocols() {
    return availableProtocols;
  }

  public <T extends TransportProtocol> Optional<SpProtocolDefinition<T>> findDefinition(T
                                                                         transportProtocol) {
    // TODO add RDF URI for protocol in model
    return this.availableProtocols
            .stream()
            .filter
                    (adf -> adf.getTransportFormatRdfUri().equals(transportProtocol.getClass()
                            .getCanonicalName()))
            .map(s -> (SpProtocolDefinition<T>) s)
            .findFirst();

  }
}
