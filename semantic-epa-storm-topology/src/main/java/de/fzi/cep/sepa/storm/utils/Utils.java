package de.fzi.cep.sepa.storm.utils;

import java.net.URI;

import backtype.storm.spout.Scheme;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.storage.impl.SepaInvocationStorageImpl;
import scala.NotImplementedError;

public class Utils {
	public static final String SEPA_DATA_STREAM = "SEPA_DATA_STREAM";

	public static Scheme getScheme(EventStream eventStream) {
		if (isJson(eventStream))
			return new JsonScheme(eventStream);
		else
			throw new NotImplementedError();

	}

	public static boolean isJson(EventStream eventStream) {
		return eventStream.getEventGrounding().getTransportFormats().get(0).getRdfType()
				.contains(URI.create(MessageFormat.Json));
	}

	public static String getZookeeperUrl(EventStream eventStream) {
		KafkaTransportProtocol tp = (KafkaTransportProtocol) eventStream.getEventGrounding().getTransportProtocol();
		return tp.getZookeeperHost() + ":" + tp.getZookeeperPort();

	}

	public static String getBroker(EventStream eventStream) {
		KafkaTransportProtocol tp = (KafkaTransportProtocol) eventStream.getEventGrounding().getTransportProtocol();
		return tp.getBrokerHostname() + ":" + tp.getKafkaPort();
	}

	public static String getTopic(EventStream eventStream) {
		KafkaTransportProtocol tp = (KafkaTransportProtocol) eventStream.getEventGrounding().getTransportProtocol();
		return tp.getTopicName();
	}
	
	public static EventProperty getEventPropertyById(URI id, EventStream eventStream) {
		
		for (EventProperty p : eventStream.getEventSchema().getEventProperties()) {
			if (p.getElementId().equals(id.toString())) {
				return p;
			}
		}
		
		return null;
	}

	public static SepaInvocation getSepaInvocation(String id) {
		SepaInvocationStorageImpl invocationStorage = new SepaInvocationStorageImpl();
		return invocationStorage.getSepaInvovation(id);

	}
	
	public static String storeSepaInvocation(SepaInvocation invocation) {
		SepaInvocationStorageImpl impl = new SepaInvocationStorageImpl();
		return impl.storeSepaInvocation(invocation);

	}
}
