package de.fzi.cep.sepa.sdk.helpers;

import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

/**
 * Created by riemer on 29.01.2017.
 */
public class SupportedFormats {

  public static TransportFormat jsonFormat() {
    return new TransportFormat(MessageFormat.Json);
  }

  public static TransportFormat thriftFormat() {
    return new TransportFormat(MessageFormat.Thrift);
  }
}
