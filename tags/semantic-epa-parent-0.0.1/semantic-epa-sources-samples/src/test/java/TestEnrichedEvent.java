import javax.jms.JMSException;

import com.google.gson.JsonObject;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.enriched.EnrichedStream;

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
