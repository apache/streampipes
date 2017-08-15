import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;

import java.util.Date;
import java.util.HashMap;


//public class TestRecommendation {
//
//	public static void main(String[] args)
//	{
//		TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
//
//		RecommendationEvent event = new RecommendationEvent();
//		event.setAction("Action");
//		event.setActor("actor");
//		event.setEventName("EventName");
//		event.setRecommendationId("abc");
//		event.setEventProperties(new HashMap<>());
//		event.setTimestamp(new Date().getTime());
//
//		StreamPipesKafkaProducer producer = new StreamPipesKafkaProducer("ipe-koi04.fzi.de:9092", "de.fzi.cep.sepa.notifications");
//		try {
//			producer.publish(serializer.serialize(event));
//		} catch (TException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
