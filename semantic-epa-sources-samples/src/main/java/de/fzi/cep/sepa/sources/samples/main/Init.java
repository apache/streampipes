package de.fzi.cep.sepa.sources.samples.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import de.fzi.cep.sepa.sources.samples.ddm.DDMProducer;
import de.fzi.cep.sepa.sources.samples.drillbit.DrillBitProducer;
import de.fzi.cep.sepa.sources.samples.twitter.TwitterStreamProducer;

public class Init {

	public static void  main(String[] args) throws Exception
	{
		List<SemanticEventProducerDeclarer> declarers = new ArrayList<SemanticEventProducerDeclarer>();
		
		declarers.add(new TwitterStreamProducer());
		declarers.add(new DDMProducer());
		declarers.add(new DrillBitProducer());
		//twitterStream.executeStream();
		ModelSubmitter.submitProducer(declarers, SourcesConfig.serverUrl, 8089);
	}
	
	
	
}
