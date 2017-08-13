package org.streampipes.wrapper.standalone.manager;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.dataformat.SpDataFormatManager;
import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.SpProtocolDefinition;
import org.streampipes.messaging.SpProtocolManager;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;

public class PManager {

  public static <T extends TransportProtocol> EventConsumer<T> getConsumer(T
                                                                                   protocol) throws SpRuntimeException {
    return getProtocolDefinition(protocol).getConsumer();
  }

  public static <T extends TransportProtocol> EventProducer<T> getProducer(T protocol) throws
          SpRuntimeException {
   return getProtocolDefinition(protocol).getProducer();
  }

  private static <T extends TransportProtocol>SpProtocolDefinition<T> getProtocolDefinition(T
                                                                                                    protocol) throws SpRuntimeException {
    return SpProtocolManager.INSTANCE.findDefinition(protocol);
  }

  public static SpDataFormatDefinition getDataFormat(TransportFormat format) throws SpRuntimeException {
    return SpDataFormatManager.INSTANCE.findDefinition(format);
  }
}
