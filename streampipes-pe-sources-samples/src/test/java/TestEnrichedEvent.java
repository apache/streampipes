import com.google.gson.JsonObject;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.pe.sources.samples.config.AkerVariables;
import org.streampipes.pe.sources.samples.enriched.EnrichedStream;

import javax.jms.JMSException;

public class TestEnrichedEvent {

	public static void main(String[] args)
	{
		JsonObject json = new EnrichedStream().generateSampleEvent();
		
		try {
			new ActiveMQPublisher(ClientConfiguration.INSTANCE.getJmsUrl(), AkerVariables.Enriched.topic()).sendText(json.toString());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
