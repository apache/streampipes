package de.fzi.cep.sepa.runtime.camel.util;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import de.fzi.cep.sepa.runtime.util.ThriftSerializer;
import eu.proasense.internal.SimpleEvent;

public class ThriftDataFormat implements DataFormat {

	TDeserializer deserializer;
	TSerializer serializer;
	ThriftSerializer thriftSerializer;
	
	public ThriftDataFormat()
	{
		deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		serializer = new TSerializer(new TBinaryProtocol.Factory());
		thriftSerializer = new ThriftSerializer();
	}
	
	@Override
	public void marshal(Exchange exchange, Object graph, OutputStream stream)
			throws Exception {
		SimpleEvent simpleEvent = thriftSerializer.toSimpleEvent(graph);
		stream.write(serializer.serialize(simpleEvent));
	}

	@Override
	public Object unmarshal(Exchange exchange, InputStream stream)
			throws Exception {
		byte[] bytes = exchange.getContext().getTypeConverter().mandatoryConvertTo(byte[].class, stream);
		SimpleEvent simpleEvent = new SimpleEvent();
		deserializer.deserialize(simpleEvent, bytes);
    	return thriftSerializer.toMap(simpleEvent);
	}

	

	
	
	
	
}
