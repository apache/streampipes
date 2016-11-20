import de.fzi.cep.sepa.messaging.EventListener;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaConsumer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import eu.proasense.internal.*;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TestKafkaConnection implements EventListener<byte[]> {
	
	private static final int MAX_MESSAGES = 100;
	private int counter = 0;
	
	private EventProducer producer;
	private EventProducer producer2;
	
	public TestKafkaConnection(String url, int kafkaTopic, int zookeeperTopic, String topic)
	{
		producer = new StreamPipesKafkaProducer(url+kafkaTopic, topic);


		StreamPipesKafkaConsumer kafkaConsumerGroup = new StreamPipesKafkaConsumer(url+kafkaTopic,
				topic, this);

		Thread thread = new Thread(kafkaConsumerGroup);
		thread.start();

	}

	public static void main(String[] args)
	{
		TestKafkaConnection connection = new TestKafkaConnection("ipe-koi04.fzi.de:", 9092, 2181, "eu.proasense.internal.sp.internal.outgoing.1000693");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		RecommendationEvent event = new RecommendationEvent();
		event.setAction("action");
		event.setActor("actor");
		event.setEventName("eventName");
		event.setRecommendationId("recId");
		Map<String, ComplexValue> eventProperties = new HashMap<>();
		ComplexValue value = new ComplexValue();
		value.setType(VariableType.LONG);
		value.setValue("123");
		eventProperties.put("action_timestamp", value);
		event.setEventProperties(eventProperties);
		
		PredictedEvent pe = new PredictedEvent();
		pe.setEventName("eventName");
		pe.setPdfType(PDFType.EXPONENTIAL);
		pe.setTimestamp(12345);
		pe.setParams(new ArrayList<Double>());
		pe.setEventProperties(eventProperties);
		
		TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
		
//		for(int i = 0; i < MAX_MESSAGES; i++)
//		{
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			connection.publishMessage(RandomStringUtils.randomAlphabetic(12).getBytes());
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
	}
	
	public void publishMessage(byte[] bytes)
	{
		producer.publish(bytes);
	}
	
	


	@Override
	public void onEvent(byte[] json) {
	System.out.print(new String(json));
		//TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
		//SimpleEvent simpleEvent = new SimpleEvent();
		//try {
			//deserializer.deserialize(simpleEvent, json);
			//System.out.println("ONEV" +simpleEvent.getSensorId());
		//} catch (TException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Error");
		//}
		
		counter++;
		if (counter % 10000 == 0) System.out.println("Counter, "+ counter);
	}
	
	
}
