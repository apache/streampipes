package de.fzi.cep.sepa.sources.samples.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.sources.samples.ddm.DDMProducer;
import de.fzi.cep.sepa.sources.samples.drillbit.DrillBitProducer;
import de.fzi.cep.sepa.sources.samples.enriched.EnrichedEventProducer;
import de.fzi.cep.sepa.sources.samples.mobile.MobileStreamProducer;
import de.fzi.cep.sepa.sources.samples.proveit.ProveITEventProducer;
import de.fzi.cep.sepa.sources.samples.ram.RamProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.taxi.NYCTaxiProducer;
import de.fzi.cep.sepa.sources.samples.twitter.TwitterStreamProducer;

public class Init  {

	
	public static void  main(String[] args) 
	{	
		new Init().declare();
	}
	
	public void declare() {
		List<SemanticEventProducerDeclarer> declarers = new ArrayList<SemanticEventProducerDeclarer>();

		declarers.add(new TwitterStreamProducer());
//		declarers.add(new DDMProducer());
//		declarers.add(new DrillBitProducer());
//		declarers.add(new EnrichedEventProducer());
//		declarers.add(new RamProducer());
//		declarers.add(new MobileStreamProducer());
		declarers.add(new RandomDataProducer());
//		declarers.add(new NYCTaxiProducer());
//		declarers.add(new ProveITEventProducer());		

		try {
			ModelSubmitter.submitProducer(declarers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
