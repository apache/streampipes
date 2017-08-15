package org.streampipes.wrapper.standalone.manager;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.EventProducer;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.wrapper.standalone.routing.FlatSpInputCollector;
import org.streampipes.wrapper.standalone.routing.FlatSpOutputCollector;

import java.util.HashMap;
import java.util.Map;

public class ProtocolManager {

  public static Map<String, FlatSpInputCollector> consumers = new HashMap<>();

  private static final String topicPrefix = "topic://";

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

  public static FlatSpOutputCollector makeOutputCollector(TransportProtocol protocol,
                                                          TransportFormat format)
          throws
          SpRuntimeException {
    return new FlatSpOutputCollector(protocol, format);
  }


  private static String topicName(TransportProtocol protocol) {
    return topicPrefix + protocol.getTopicName();
  }

  public static void removeInputCollector(String topicWithPrefix) throws SpRuntimeException {
    consumers.remove(topicWithPrefix);
  }

  public static void startConsumer(FlatSpInputCollector collector, TransportProtocol protocol) {

  }

}
