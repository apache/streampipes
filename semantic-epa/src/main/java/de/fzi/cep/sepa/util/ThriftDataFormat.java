package de.fzi.cep.sepa.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

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
