import java.util.Date;
import java.util.HashMap;

import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import eu.proasense.internal.RecommendationEvent;


public class TestRecommendation {

	public static void main(String[] args)
	{
		TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
		
		RecommendationEvent event = new RecommendationEvent();
		event.setAction("Action");
		event.setActor("actor");
		event.setEventName("EventName");
		event.setRecommendationId("abc");
		event.setEventProperties(new HashMap<>());
		event.setTimestamp(new Date().getTime());

		ProaSenseInternalProducer producer = new ProaSenseInternalProducer("ipe-koi04.fzi.de:9092", "de.fzi.cep.sepa.notifications");
		try {
			producer.send(serializer.serialize(event));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
