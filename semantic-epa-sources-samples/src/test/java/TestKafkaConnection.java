import org.apache.commons.lang3.RandomStringUtils;

import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.cep.sepa.commons.messaging.kafka.KafkaConsumerGroup;


public class TestKafkaConnection implements IMessageListener {
	
	private static final int MAX_MESSAGES = 10;
	
	private ProaSenseInternalProducer producer;
	
	public TestKafkaConnection(String url, int kafkaTopic, int zookeeperTopic, String topic)
	{
		producer = new ProaSenseInternalProducer(url+kafkaTopic, topic);
		KafkaConsumerGroup kafkaConsumerGroup = new KafkaConsumerGroup(url+zookeeperTopic, topic,
				new String[] {topic}, this);
		kafkaConsumerGroup.run(1);
	}
	
	
	
	public static void main(String[] args)
	{
		TestKafkaConnection connection = new TestKafkaConnection("ipe-koi05.fzi.de:", 9092, 2181, "SEPA.SEP.Random.Number");
		
		for(int i = 0; i < MAX_MESSAGES; i++)
		{
			connection.publishMessage(RandomStringUtils.randomAlphabetic(12).getBytes());
		}
		
	}
	
	public void publishMessage(byte[] bytes)
	{
		producer.send(bytes);
	}
	
	


	@Override
	public void onEvent(String json) {
		System.out.println("Event, "+ json);
	}
	
	
}
