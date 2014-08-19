package de.fzi.cep.sepa.sources.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;

public class Test {

	public static void  main(String[] args) throws Exception
	{
		List<SemanticEventProducerDeclarer> declarers = new ArrayList<SemanticEventProducerDeclarer>();
		
		declarers.add(new TwitterStreamProducer());
		
		//twitterStream.executeStream();
		ModelSubmitter.submitProducer(declarers, SourcesConfig.serverUrl, 8089);
	}
	
	
	
}
