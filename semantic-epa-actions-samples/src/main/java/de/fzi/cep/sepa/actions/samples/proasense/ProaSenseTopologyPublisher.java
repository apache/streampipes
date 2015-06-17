package de.fzi.cep.sepa.actions.samples.proasense;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import de.fzi.cep.sepa.actions.messaging.jms.IMessageListener;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.VariableType;

public class ProaSenseTopologyPublisher implements IMessageListener {

	private ProaSenseInternalProducer producer;
	private SecInvocation graph;
	private static final String DEFAULT_PROASENSE_TOPIC = "eu.proasense.internal.sp.internal.incoming";
	private TSerializer serializer;
	
	static String testJson =  "{\"ram_pos_measured\":21016.4,\"ram_vel_setpoint\":0.0,\"ram_pos_setpoint\":16510.2,\"ram_vel_measured\":-53.34777,\"pressure_gearbox\":3.423385,\"mru_vel\":-82.86029500000001,\"hook_load\":109.9758,\"mru_pos\":-179.9121,\"torque\":5.354475000000001,\"oil_temp_gearbox\":13.1959,\"wob\":-0.2115,\"ibop\":1.0,\"hoist_press_B\":112.839,\"oil_temp_swivel\":14.7423,\"rpm\":42.0743,\"hoist_press_A\":18.5644,\"eventName\":\"EnrichedEvent\",\"temp_ambient\":12.37,\"timestamp\":1387559130232}";
	   
	private static final Logger logger = LoggerFactory.getLogger(ProaSenseTopologyPublisher.class);

	private int i = 0;
	
	public ProaSenseTopologyPublisher(SecInvocation graph) {
		this.graph = graph;
		this.producer = new ProaSenseInternalProducer(Configuration.getBrokerConfig().getKafkaUrl(), DEFAULT_PROASENSE_TOPIC);
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
	}
	
	@Override
	public void onEvent(String json) {
		//System.out.println("Sending event " +i +", " +json);
		i++;
		if (i % 500 == 0) System.out.println("Sending, " +i);
		Optional<byte[]> bytesMessage = buildDerivedEvent(json);
		if (bytesMessage.isPresent()) producer.send(bytesMessage.get());
		else System.out.println("empty event");
	}

	private Optional<byte[]> buildDerivedEvent(String json) {
		DerivedEvent event = new DerivedEvent();
		
		event.setComponentId("CEP");
		event.setEventName("Generated");
		
		Map<String, ComplexValue> values = new HashMap<String, ComplexValue>();
		JsonElement element = new JsonParser().parse(json);
		
		try {
			if (element.isJsonObject())
			{
				JsonObject obj = element.getAsJsonObject();
				Set<Entry<String, JsonElement>> entries = obj.entrySet();
				
				for(Map.Entry<String,JsonElement> entry : entries){
					{
						if (entry.getKey().equals("timestamp")) 
							{
							try {
								event.setTimestamp(obj.get(entry.getKey()).getAsLong());
								System.out.println("Timestamp, " +event.getTimestamp());
							} catch (Exception e) { e.printStackTrace();}
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
	
	public static void main(String[] args)
	{
		ProaSenseTopologyPublisher publisher = new ProaSenseTopologyPublisher(null);
		long currentTime = System.currentTimeMillis();
		for(int i = 0; i < 1000; i++) publisher.onEvent(testJson);
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - currentTime);
	}

}
