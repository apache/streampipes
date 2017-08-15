package org.streampipes.wrapper.standalone.manager;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.wrapper.standalone.routing.FlatSpInputCollector;
import org.streampipes.wrapper.standalone.routing.FlatSpOutputCollector;

import java.util.HashMap;
import java.util.Map;

public class ProtocolManager {

  public static Map<String, FlatSpInputCollector> consumers = new HashMap<>();

  public static <T extends TransportProtocol> FlatSpInputCollector findInputCollector
          (T
                   protocol,
           TransportFormat format, Boolean
                   singletonEngine)
          throws
          SpRuntimeException {

    if (consumers.containsKey(topicName(protocol))) {
      return consumers.get(topicName(protocol));
    } else {
      return makeInputCollector(protocol, format, singletonEngine);
    }

  }

  private static <T extends TransportProtocol> FlatSpInputCollector makeInputCollector
          (T protocol,
           TransportFormat format, Boolean
                   singletonEngine) throws
          SpRuntimeException {
    return new FlatSpInputCollector<>(protocol, format, singletonEngine);
  }

  public static <T extends TransportProtocol> FlatSpOutputCollector makeOutputCollector(T
                                                                                                protocol, TransportFormat format)
          throws
          SpRuntimeException {
    return new FlatSpOutputCollector<>(protocol, format);
  }


  private static String topicName(TransportProtocol protocol) {
    return protocol.getTopicName();
  }

  public static void removeInputCollector(String topic) throws SpRuntimeException {
    consumers.remove(topic);
  }



}
