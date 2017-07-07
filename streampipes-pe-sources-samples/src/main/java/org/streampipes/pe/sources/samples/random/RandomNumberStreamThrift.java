package org.streampipes.pe.sources.samples.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.MessageFormat;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

public class RandomNumberStreamThrift extends RandomNumberStream {

	public static final String TOPIC = "SEPA.SEP.Random.Number.Thrift";
	
	private TSerializer serializer;
	
	public RandomNumberStreamThrift() {
		super(TOPIC);
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
	}
	
	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = prepareStream(TOPIC, MessageFormat.Thrift);
		stream.setName("Random Number Stream (Thrift)");
		stream.setDescription("Random Number Stream Description");
		stream.setUri(sep.getUri() + "/numberthrift");
		
		return stream;
	}

	
	@Override
	protected Optional<byte[]> getMessage(long nanoTime, int randomNumber, int counter) {
		try {
			return Optional.of(serializer.serialize(buildSimpleEvent(nanoTime, randomNumber, counter)));
		} catch (TException e) {
			return Optional.empty();
		}
	}

	private SimpleEvent buildSimpleEvent(long timestamp, int number, int count) {
		Map<String, ComplexValue> map = new HashMap<String, ComplexValue>();
		ComplexValue value = new ComplexValue();
		value.setType(VariableType.LONG);
		value.setValue(String.valueOf(number));

		ComplexValue value2 = new ComplexValue();
		value2.setType(VariableType.STRING);
		value2.setValue(String.valueOf(randomString()));

		ComplexValue value3 = new ComplexValue();
		value3.setType(VariableType.LONG);
		value3.setValue(String.valueOf(count));

		map.put("randomValue", value);
		map.put("randomString", value2);
		map.put("count", value3);
		SimpleEvent simpleEvent = new SimpleEvent(timestamp, "RandomNumber", map);
		simpleEvent.setSensorId("RNS");
		simpleEvent.setSensorIdIsSet(true);
		return simpleEvent;
	}
	
	public static void main(String[] args) {
		new RandomNumberStreamThrift().executeStream();
	}
}