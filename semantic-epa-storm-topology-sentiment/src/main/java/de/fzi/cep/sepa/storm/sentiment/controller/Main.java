package de.fzi.cep.sepa.storm.sentiment.controller;

import java.util.Arrays;

import de.fzi.cep.sepa.desc.ModelSubmitter;

public class Main {

	public static void main(String[] args)
	{
		try {
			ModelSubmitter.submitAgent(Arrays.asList(new SentimentDetectionController()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
