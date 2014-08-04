package de.fzi.cep.sepa.sources.samples.twitter;

import de.fzi.cep.sepa.desc.ModelSubmitter;

public class Test {

	public static void  main(String[] args) throws Exception
	{
		TwitterStreamProducer producer = new TwitterStreamProducer();
		TwitterStream twitterStream = new TwitterStream();
		
		ModelSubmitter.submitProducer(producer, twitterStream);
	}
	
	
	
}
