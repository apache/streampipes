package org.streampipes.sdk.helpers;

import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.vocabulary.MessageFormat;

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
