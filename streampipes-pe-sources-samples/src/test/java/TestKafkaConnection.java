import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.kafka.SpKafkaConsumer;


public class TestKafkaConnection implements InternalEventProcessor<byte[]> {

	private static final int MAX_MESSAGES = 100;
	private int counter = 0;

	private EventProducer producer;
	private EventProducer producer2;

	public TestKafkaConnection(String url, int kafkaTopic, int zookeeperTopic, String topic)
	{
		//producer = new SpKafkaProducer(url+kafkaTopic, topic);


		SpKafkaConsumer kafkaConsumerGroup = new SpKafkaConsumer(url+kafkaTopic,
				topic, this);

		Thread thread = new Thread(kafkaConsumerGroup);
		thread.start();

	}

	public static void main(String[] args)
	{
		TestKafkaConnection connection = new TestKafkaConnection("ipe-koi06.fzi.de:", 9092, 2181,
						"org.streampipes.datarun.bot.answer");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


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
