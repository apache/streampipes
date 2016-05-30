package de.fzi.cep.sepa.storm.sentiment.main;

import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
import de.fzi.cep.sepa.storm.sentiment.controller.SentimentDetectionController;

public class StormSentimentInit extends StandaloneModelSubmitter {

	public static void main(String[] args)
	{
		try {
			DeclarersSingleton.getInstance().add(new SentimentDetectionController());
			DeclarersSingleton.getInstance().setPort(8093);

		     new StormSentimentInit().init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
