package org.streampipes.pe.sinks.standalone.samples.proasense.kpi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.streampipes.pe.sinks.standalone.samples.proasense.ProaSenseTopologyPublisher;
import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.VariableType;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

public class ProaSenseKpiPublisher implements EventListener<byte[]> {

	private StreamPipesKafkaProducer producer;
	private TSerializer serializer;
	private String kpiId;
		   
	private static final Logger logger = LoggerFactory.getLogger(ProaSenseTopologyPublisher.class);

	private int i = 0;
	
	public ProaSenseKpiPublisher(String kafkaHost, int kafkaPort, String topic, String kpiId) {
		this.producer = new StreamPipesKafkaProducer(kafkaHost + ":" +kafkaPort, topic);
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
		this.kpiId = kpiId;
	}

	public void closePublisher() {
		producer.closeProducer();
	}
	
	@Override
	public void onEvent(byte[] json) {
		i++;
		if (i % 500 == 0) System.out.println("Sending, " +i);
		Optional<byte[]> bytesMessage = buildDerivedEvent(new String(json));
		if (bytesMessage.isPresent()) producer.publish(bytesMessage.get());
		else System.out.println("empty event");
	}

	private Optional<byte[]> buildDerivedEvent(String json) {
		System.out.println(json);
		DerivedEvent event = new DerivedEvent();
		
		event.setComponentId("KPI");
		event.setEventName(kpiId);
		
		Map<String, ComplexValue> values = new HashMap<String, ComplexValue>();
		JsonElement element = new JsonParser().parse(json);
		
		try {
			if (element.isJsonObject())
			{
				JsonObject obj = element.getAsJsonObject();
				Set<Entry<String, JsonElement>> entries = obj.entrySet();
				
				for(Map.Entry<String,JsonElement> entry : entries){
					{
						if (entry.getKey().equals("time")) 
							{
							try {
							
								event.setTimestamp(obj.get("time").getAsLong());
							} catch (Exception e) { /*e.printStackTrace();*/}
							}
						else values.put(entry.getKey(), convert(obj.get(entry.getKey())));
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		event.setEventProperties(values);
		return serialize(event);
	}

	private ComplexValue convert(JsonElement jsonElement) throws Exception {
		ComplexValue value = new ComplexValue();
		value.setType(getVariableType(jsonElement));
		value.setValue(jsonElement.getAsString());
		return value;
	}

	private VariableType getVariableType(JsonElement jsonElement) throws Exception {
		if (jsonElement.isJsonPrimitive())
		{
			JsonPrimitive primitive = (JsonPrimitive) jsonElement;
			if (primitive.isNumber()) return VariableType.DOUBLE;
			else if (primitive.isString()) return VariableType.STRING;
			else return VariableType.LONG;
		}
		else throw new Exception();
	}
	
	private Optional<byte[]> serialize(TBase tbase)
    {
    	try {
			return Optional.of(serializer.serialize(tbase));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return Optional.empty();
    }
}
