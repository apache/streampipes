package de.fzi.cep.sepa.storm.sentiment.main;

import java.util.Arrays;

import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.storm.sentiment.controller.SentimentDetectionController;

public class Init {

	public static void main(String[] args)
	{
		try {
			ModelSubmitter.submitAgent(Arrays.asList(new SentimentDetectionController()), 8093);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
