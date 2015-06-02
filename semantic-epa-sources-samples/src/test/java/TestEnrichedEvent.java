import com.google.gson.JsonObject;

import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.enriched.EnrichedStream;
import de.fzi.cep.sepa.sources.samples.util.ActiveMQPublisher;


public class TestEnrichedEvent {

	public static void main(String[] args)
	{
		JsonObject json = new EnrichedStream().generateSampleEvent();
		
		new ActiveMQPublisher(AkerVariables.Enriched.topic()).send(json.toString());
	}
}
