package org.streampipes.sdk.helpers;

import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.vocabulary.MessageFormat;

/**
 * Created by riemer on 29.01.2017.
 */
public class SupportedFormats {

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging arriving in JSON format
   * @return The resulting {@link org.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat jsonFormat() {
    return new TransportFormat(MessageFormat.Json);
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messaging arriving in Thrift
   * format
   * @return The resulting {@link org.streampipes.model.grounding.TransportFormat}.
   */
  public static TransportFormat thriftFormat() {
    return new TransportFormat(MessageFormat.Thrift);
  }
}
