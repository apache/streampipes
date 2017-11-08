package org.streampipes.dataformat.json;

import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.dataformat.SpDataFormatFactory;
import org.streampipes.vocabulary.MessageFormat;

public class JsonDataFormatFactory extends SpDataFormatFactory {

  @Override
  public String getTransportFormatRdfUri() {
    return MessageFormat.Json;
  }

  @Override
  public SpDataFormatDefinition createInstance() {
    return new JsonDataFormatDefinition();
  }
}
