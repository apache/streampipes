package de.fzi.cep.sepa.runtime.flat.datatype.json;

import java.util.Map;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;
import de.fzi.cep.sepa.runtime.util.ThriftSerializer;
import eu.proasense.internal.SimpleEvent;

public class ThriftDatatypeDefinition implements DatatypeDefinition {

	private ThriftSerializer thriftSerializer;
	private TDeserializer deserializer;
	private TSerializer serializer;
	
	public ThriftDatatypeDefinition() {
		thriftSerializer = new ThriftSerializer();
		deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		serializer = new TSerializer(new TBinaryProtocol.Factory());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> unmarshal(byte[] input) {
		
		SimpleEvent simpleEvent = new SimpleEvent();
		try {
			deserializer.deserialize(simpleEvent, input);
			System.out.println(simpleEvent.toString());
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return (Map<String, Object>) thriftSerializer.toMap(simpleEvent);
	}

	@Override
	public byte[] marshal(Object event) {
		SimpleEvent simpleEvent = thriftSerializer.toSimpleEvent(event);
		try {
			return serializer.serialize(simpleEvent);
		} catch (TException e) {
			return null;
		}
	}

}
