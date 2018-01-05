package org.streampipes.sdk.helpers;

import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.vocabulary.MessageFormat;

public class Formats {

  /**
   * Defines the transport format JSON used by a data stream at runtime.
   * @return The {@link org.streampipes.model.grounding.TransportFormat} of type JSON.
   */
  public static TransportFormat jsonFormat() {
    return new TransportFormat(MessageFormat.Json);
  }

  /**
   * Defines the transport format Apache Thrift used by a data stream at runtime.
   * @return The {@link org.streampipes.model.grounding.TransportFormat} of type Thrift.
   */
  public static TransportFormat thriftFormat() {
    return new TransportFormat(MessageFormat.Thrift);
  }
}
