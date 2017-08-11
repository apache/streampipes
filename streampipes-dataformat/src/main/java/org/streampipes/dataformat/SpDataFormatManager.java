package org.streampipes.dataformat;

import org.streampipes.commons.exceptions.StreamPipesRuntimeException;
import org.streampipes.model.impl.TransportFormat;

import java.util.List;
import java.util.Optional;

public enum SpDataFormatManager {

  INSTANCE;

  private List<SpDataFormatDefinition<?>> availableDataFormats;

  public void register(SpDataFormatDefinition dataFormatDefinition) {
    availableDataFormats.add(dataFormatDefinition);
  }

  public List<SpDataFormatDefinition<?>> getAvailableDataFormats() {
    return availableDataFormats;
  }

  public SpDataFormatDefinition<?> findDefinition(TransportFormat transportFormat) throws StreamPipesRuntimeException {
    // TODO why is transportFormat.getRdfType a list?
    Optional<SpDataFormatDefinition<?>> matchedFormat = this.availableDataFormats
            .stream()
            .filter
            (adf -> transportFormat
                    .getRdfType()
                    .stream()
                    .anyMatch(tf -> tf.toString().equals(adf
                    .getTransportFormatRdfUri())))
            .findFirst();

    if (matchedFormat.isPresent()) {
      return matchedFormat.get();
    } else {
      throw new StreamPipesRuntimeException("Runtime Exception: could not find any supported data" +
              " format");
    }
  }

}
