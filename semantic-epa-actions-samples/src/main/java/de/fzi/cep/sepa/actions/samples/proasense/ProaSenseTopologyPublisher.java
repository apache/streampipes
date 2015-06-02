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
import de.fzi.cep.sepa.commons.messaging.ProaSenseConfig;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.VariableType;

public class ProaSenseTopologyPublisher implements IMessageListener{

	private ProaSenseInternalProducer producer;
	private SecInvocation graph;
	private static final String DEFAULT_PROASENSE_TOPIC = "eu.proasense.internal.sp.internal.incoming";
	private TSerializer serializer;
	
	private static final Logger logger = LoggerFactory.getLogger(ProaSenseTopologyPublisher.class);

	private int i = 0;
	
	
	public ProaSenseTopologyPublisher(SecInvocation graph) {
		this.producer = new ProaSenseInternalProducer(ProaSenseConfig.BROKER_URL, DEFAULT_PROASENSE_TOPIC);
		this.graph = graph;
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
	}
	
	@Override
	public void onEvent(String json) {
		System.out.println("Sending event " +i +", " +json);
		i++;
		producer.send(buildDerivedEvent(json));
	}

	private byte[] buildDerivedEvent(String json) {
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
		return serialize(event).get();
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
