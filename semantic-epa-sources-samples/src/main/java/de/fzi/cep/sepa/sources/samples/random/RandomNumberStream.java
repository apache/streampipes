package de.fzi.cep.sepa.sources.samples.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jms.JMSException;

import kafka.utils.Utils;

import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.codehaus.jettison.json.JSONObject;

import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import twitter4j.Status;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.quality.Accuracy;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.Frequency;
import de.fzi.cep.sepa.model.impl.quality.Latency;
import de.fzi.cep.sepa.sources.samples.config.SampleSettings;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

public class RandomNumberStream implements EventStreamDeclarer {

	ActiveMQPublisher samplePublisher;

	public RandomNumberStream() throws JMSException {
		samplePublisher = new ActiveMQPublisher(Configuration.getInstance().TCP_SERVER_URL + ":61616", "SEPA.SEP.Random.Number");
	}

	@Override
	public EventStream declareModel(SepDescription sep) {
		EventStream stream = new EventStream();

		EventSchema schema = new EventSchema();

		List<EventPropertyQualityDefinition> randomValueQualities = new ArrayList<EventPropertyQualityDefinition>();
		randomValueQualities.add(new Latency(0));
		randomValueQualities.add(new Accuracy((float) 0.5));

		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "randomValue", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"), randomValueQualities));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "randomString", "",
				de.fzi.cep.sepa.commons.Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "count", "",
				de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));

		List<EventStreamQualityDefinition> eventStreamQualities = new ArrayList<EventStreamQualityDefinition>();
		eventStreamQualities.add(new Frequency(1));

		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEPA.SEP.Random.Number"));
		grounding.setTransportFormats(
				de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Thrift)));

		stream.setEventGrounding(grounding);
		schema.setEventProperties(eventProperties);
		stream.setEventSchema(schema);
		stream.setHasEventStreamQualities(eventStreamQualities);
		stream.setName("Random Number Stream");
		stream.setDescription("Random Number Stream Description");
		stream.setUri(sep.getUri() + "/number");

		return stream;
	}

	@Override
	public void executeStream() {

		Runnable r = new Runnable() {

			@Override
			public void run() {
				Random random = new Random();
				TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
				int j = 0;
				for (;;) {
					try {
						// String json = buildJson(System.currentTimeMillis(),
						// random.nextInt(100)).toString();
						// System.out.println(json);

						byte[] payload = serializer
								.serialize(buildSimpleEvent(System.currentTimeMillis(), random.nextInt(100), j));
						samplePublisher.sendBinary(payload);
						Thread.sleep(1000);
						j++;
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(r);
		thread.start();

	}

	@Override
	public boolean isExecutable() {
		return true;
	}

	private JSONObject buildJson(long timestamp, int number) {
		JSONObject json = new JSONObject();

		try {
			json.put("timestamp", timestamp);
			json.put("randomValue", number);
			json.put("randomString", randomString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return json;
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
		return simpleEvent;
	}

	private String randomString() {
		String[] randomStrings = new String[] { "a", "b", "c", "d" };
		Random random = new Random();
		return randomStrings[random.nextInt(3)];
	}

}
