package de.fzi.cep.sepa.actions.samples.proasense.kpi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

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

import de.fzi.cep.sepa.actions.samples.proasense.ProaSenseEventNotifier;
import de.fzi.cep.sepa.actions.samples.proasense.ProaSenseTopologyPublisher;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.VariableType;

public class ProaSenseKpiPublisher implements IMessageListener {

	private ProaSenseInternalProducer producer;
	private static final String DEFAULT_PROASENSE_TOPIC = "eu.proasense.internal.sp.internal.kpi";
	private TSerializer serializer;
	private ProaSenseEventNotifier notifier;
	private String kpiName;
		   
	private static final Logger logger = LoggerFactory.getLogger(ProaSenseTopologyPublisher.class);

	private int i = 0;
	
	public ProaSenseKpiPublisher(SecInvocation graph, ProaSenseEventNotifier notifier) {
		this.notifier = notifier;
		this.producer = new ProaSenseInternalProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), DEFAULT_PROASENSE_TOPIC);
		this.serializer = new TSerializer(new TBinaryProtocol.Factory());
		this.kpiName = SepaUtils.getFreeTextStaticPropertyValue(graph, "kpi");
	}
	
	@Override
	public void onEvent(String json) {
		i++;
		notifier.increaseCounter();
		if (i % 500 == 0) System.out.println("Sending, " +i);
		Optional<byte[]> bytesMessage = buildDerivedEvent(json);
		if (bytesMessage.isPresent()) producer.send(bytesMessage.get());
		else System.out.println("empty event");
	}

	private Optional<byte[]> buildDerivedEvent(String json) {
		System.out.println(json);
		DerivedEvent event = new DerivedEvent();
		
		event.setComponentId("KPI");
		event.setEventName(kpiName);
		
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
