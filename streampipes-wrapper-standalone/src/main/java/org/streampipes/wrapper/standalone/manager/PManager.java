package org.streampipes.wrapper.standalone.manager;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.dataformat.SpDataFormatManager;
import org.streampipes.messaging.SpProtocolDefinition;
import org.streampipes.messaging.SpProtocolManager;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;

import java.util.Optional;

public class PManager {

  public static <T extends TransportProtocol> Optional<SpProtocolDefinition<T>>
  getProtocolDefinition(T
                                protocol) {
    return SpProtocolManager.INSTANCE.findDefinition(protocol);
  }

  public static Optional<SpDataFormatDefinition> getDataFormat(TransportFormat format) throws
          SpRuntimeException {
    return SpDataFormatManager.INSTANCE.findDefinition(format);
  }
}
