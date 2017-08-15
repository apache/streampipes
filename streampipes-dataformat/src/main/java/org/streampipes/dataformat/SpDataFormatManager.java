package org.streampipes.dataformat;

import org.streampipes.model.impl.TransportFormat;

import java.util.List;
import java.util.Optional;

public enum SpDataFormatManager {

  INSTANCE;

  private List<SpDataFormatDefinition> availableDataFormats;

  public void register(SpDataFormatDefinition dataFormatDefinition) {
    availableDataFormats.add(dataFormatDefinition);
  }

  public List<SpDataFormatDefinition> getAvailableDataFormats() {
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
            .findFirst();

  }

}
