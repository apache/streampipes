package org.streampipes.pe.algorithms.storm.main;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.pe.algorithms.storm.controller.SentimentDetectionController;

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
