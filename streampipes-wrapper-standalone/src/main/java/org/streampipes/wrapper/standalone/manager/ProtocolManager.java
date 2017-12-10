package org.streampipes.wrapper.standalone.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.wrapper.standalone.routing.StandaloneSpInputCollector;
import org.streampipes.wrapper.standalone.routing.StandaloneSpOutputCollector;

import java.util.HashMap;
import java.util.Map;

public class ProtocolManager {

  public static Map<String, StandaloneSpInputCollector> consumers = new HashMap<>();
  public static Map<String, StandaloneSpOutputCollector> producers = new HashMap<>();

  private static final Logger LOG = LoggerFactory.getLogger(ProtocolManager.class);

  // TODO currently only the topic name is used as an identifier for a consumer/producer. Should
  // be changed by some hashCode implementation in streampipes-model, but this requires changes
  // in empire serializers

  public static <T extends TransportProtocol> StandaloneSpInputCollector findInputCollector
          (T
                   protocol,
           TransportFormat format, Boolean
                   singletonEngine)
          throws
          SpRuntimeException {

    if (consumers.containsKey(topicName(protocol))) {
      return consumers.get(topicName(protocol));
    } else {
      consumers.put(topicName(protocol), makeInputCollector(protocol, format, singletonEngine));
      LOG.info("Adding new consumer to consumer map (size=" +consumers.size() +"): " +topicName(protocol));
      return consumers.get(topicName(protocol));
    }

  }

  public static <T extends TransportProtocol> StandaloneSpOutputCollector findOutputCollector
          (T
                   protocol,
           TransportFormat format)
          throws
          SpRuntimeException {

    if (producers.containsKey(topicName(protocol))) {
      return producers.get(topicName(protocol));
    } else {
      producers.put(topicName(protocol), makeOutputCollector(protocol, format));
      LOG.info("Adding new producer to producer map (size=" +producers.size() +"): " +topicName
              (protocol));
      return producers.get(topicName(protocol));
    }

  }

  private static <T extends TransportProtocol> StandaloneSpInputCollector makeInputCollector
          (T protocol,
           TransportFormat format, Boolean
                   singletonEngine) throws
          SpRuntimeException {
    return new StandaloneSpInputCollector<>(protocol, format, singletonEngine);
  }

  public static <T extends TransportProtocol> StandaloneSpOutputCollector makeOutputCollector(T
                                                                                                protocol, TransportFormat format)
          throws
          SpRuntimeException {
    return new StandaloneSpOutputCollector<>(protocol, format);
  }


  private static String topicName(TransportProtocol protocol) {
    return protocol.getTopicName();
  }

  public static <T extends TransportProtocol> void removeInputCollector(T protocol) throws
          SpRuntimeException {
    consumers.remove(topicName(protocol));
    LOG.info("Removing consumer from consumer map (size=" +consumers.size() +"): " +topicName
            (protocol));
  }

  public static <T extends TransportProtocol> void removeOutputCollector(T protocol) throws
          SpRuntimeException {
    producers.remove(topicName(protocol));
    LOG.info("Removing producer from producer map (size=" +producers.size() +"): " +topicName
            (protocol));
  }



}
