package de.fzi.cep.sepa.runtime.flat.manager;

import java.util.HashMap;
import java.util.Map;

import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.flat.protocol.Consumer;
import de.fzi.cep.sepa.runtime.flat.protocol.Producer;
import de.fzi.cep.sepa.runtime.flat.protocol.jms.JmsConsumer;
import de.fzi.cep.sepa.runtime.flat.protocol.jms.JmsProducer;
import de.fzi.cep.sepa.runtime.flat.protocol.kafka.KafkaConsumer;
import de.fzi.cep.sepa.runtime.flat.protocol.kafka.KafkaProducer;

public class ProtocolManager {

	public static Map<String, Consumer<?>> consumers = new HashMap<>();
	public static Map<String, Producer> producers = new HashMap<>();
	
	private static final String topicPrefix = "topic://";
	
	public static Consumer<?> findConsumer(TransportProtocol protocol, TransportFormat format) {
		
		if (consumers.containsKey(topicPrefix +topicName(protocol))) return consumers.get(topicPrefix +topicName(protocol));
		else return makeConsumer(protocol, DatatypeManager.findDatatypeDefinition(format));
		
	}
	
	private static Consumer<?> makeConsumer(TransportProtocol protocol, DatatypeDefinition dataType) {
		if (protocol instanceof KafkaTransportProtocol) {
			KafkaConsumer kafkaConsumer = new KafkaConsumer(((KafkaTransportProtocol) protocol).getZookeeperHost(), ((KafkaTransportProtocol) protocol).getZookeeperPort(), topicName(protocol), dataType);
			kafkaConsumer.openConsumer();
			consumers.put(topicPrefix +topicName(protocol), kafkaConsumer); 
			return kafkaConsumer;
		} else if (protocol instanceof JmsTransportProtocol) {
			JmsConsumer jmsConsumer = new JmsConsumer(protocol.getBrokerHostname(), ((JmsTransportProtocol) protocol).getPort(), protocol.getTopicName(), dataType);
			jmsConsumer.openConsumer();
			consumers.put(topicPrefix +topicName(protocol), jmsConsumer);
			return jmsConsumer;
		}
		return null;
	}

	public static Producer findProducer(TransportProtocol protocol, TransportFormat format) {
		if (producers.containsKey(topicPrefix +topicName(protocol))) return producers.get(topicPrefix +topicName(protocol));
		else return makeProducer(protocol, DatatypeManager.findDatatypeDefinition(format));
	}
	
	private static Producer makeProducer(TransportProtocol protocol, DatatypeDefinition dataType) {
		if (protocol instanceof KafkaTransportProtocol) {
			KafkaProducer kafkaProducer = new KafkaProducer(protocol.getBrokerHostname(), ((KafkaTransportProtocol) protocol).getKafkaPort(), topicName(protocol), dataType);
			kafkaProducer.openProducer();
			producers.put(topicPrefix +topicName(protocol), kafkaProducer); 
			
			return kafkaProducer;
		} else if (protocol instanceof JmsTransportProtocol) {
			JmsProducer jmsProducer = new JmsProducer(protocol.getBrokerHostname(), ((JmsTransportProtocol) protocol).getPort(), topicName(protocol), dataType);
			jmsProducer.openProducer();
			producers.put(topicPrefix +topicName(protocol), jmsProducer);
			return jmsProducer;
		}
		return null;
	}

	private static String topicName(TransportProtocol protocol) {
		return protocol.getTopicName();
	}
	
	public static void removeProducer(String topicWithPrefix) {
		producers.get(topicWithPrefix).closeProducer();
		producers.remove(topicWithPrefix);
	}
	
	public static void removeConsumer(String topicWithPrefix) {
		if (consumers.get(topicWithPrefix).getCurrentListenerCount() == 0) {
			consumers.get(topicWithPrefix).closeConsumer();
			consumers.remove(topicWithPrefix);
		}
	}
	
}
