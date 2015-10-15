package de.fzi.cep.sepa.sources.samples.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.sources.samples.ddm.DDMProducer;
import de.fzi.cep.sepa.sources.samples.drillbit.DrillBitProducer;
import de.fzi.cep.sepa.sources.samples.enriched.EnrichedEventProducer;
import de.fzi.cep.sepa.sources.samples.hella.MontracProducer;
import de.fzi.cep.sepa.sources.samples.hella.MouldingMachineProducer;
import de.fzi.cep.sepa.sources.samples.hella.VisualInspectionProducer;
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

		ClientConfiguration config = ClientConfiguration.INSTANCE;
		
		if (config.isTwitterActive()) declarers.add(new TwitterStreamProducer());
		if (config.isMhwirthReplayActive())
		{
			declarers.add(new DDMProducer());
			declarers.add(new DrillBitProducer());
			declarers.add(new EnrichedEventProducer());
			declarers.add(new RamProducer());
		}
		if (config.isRandomNumberActive()) declarers.add(new RandomDataProducer());
		if (config.isTaxiActive()) declarers.add(new NYCTaxiProducer());
		if (config.isProveItActive()) declarers.add(new ProveITEventProducer());	
		if (config.isHellaReplayActive())
		{
			declarers.add(new VisualInspectionProducer());
			declarers.add(new MontracProducer());
			declarers.add(new MouldingMachineProducer());

		}
		
		try {
			ModelSubmitter.submitProducer(declarers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
