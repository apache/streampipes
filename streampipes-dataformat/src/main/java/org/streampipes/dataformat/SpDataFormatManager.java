package org.streampipes.dataformat;

import org.streampipes.model.grounding.TransportFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum SpDataFormatManager {

  INSTANCE;

  private List<SpDataFormatFactory> availableDataFormats;

  SpDataFormatManager() {
    this.availableDataFormats = new ArrayList<>();
  }

  public void register(SpDataFormatFactory dataFormatDefinition) {
    availableDataFormats.add(dataFormatDefinition);
  }

  public List<SpDataFormatFactory> getAvailableDataFormats() {
    return availableDataFormats;
  }

  public Optional<SpDataFormatDefinition> findDefinition(TransportFormat transportFormat) {
    // TODO why is transportFormat.getRdfType a list?
    return this.availableDataFormats
            .stream()
            .filter
                    (adf -> transportFormat
                            .getRdfType()
                            .stream()
                            .anyMatch(tf -> tf.toString().equals(adf
                                    .getTransportFormatRdfUri())))
            .map(SpDataFormatFactory::createInstance)
            .findFirst();

  }

}
