package de.fzi.cep.sepa.sources.samples.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.commons.config.BrokerConfig;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import de.fzi.cep.sepa.sources.samples.ddm.DDMProducer;
import de.fzi.cep.sepa.sources.samples.drillbit.DrillBitProducer;
import de.fzi.cep.sepa.sources.samples.enriched.EnrichedEventProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.taxi.NYCTaxiProducer;
import de.fzi.cep.sepa.sources.samples.twitter.TwitterStreamProducer;
import de.fzi.cep.sepa.sources.samples.util.KafkaConsumerGroup;

public class Init implements Runnable {

	public static boolean subscribeToKafka = false;
	
	public static void  main(String[] args) 
	{
		if ((args.length > 0) && args[0].equals("true")) subscribeToKafka = true;
		new Init().declare();
	}
	
	public void declare() {
		List<SemanticEventProducerDeclarer> declarers = new ArrayList<SemanticEventProducerDeclarer>();

//		declarers.add(new TwitterStreamProducer());
		declarers.add(new DDMProducer());
		declarers.add(new DrillBitProducer());
		declarers.add(new EnrichedEventProducer());
//		declarers.add(new RamProducer());
//		declarers.add(new MobileStreamProducer());
//		declarers.add(new RandomDataProducer());
//		declarers.add(new NYCTaxiProducer());
		//declarers.add(new ProveITEventProducer());
		
		//String zooKeeper = Configuration.getBrokerConfig().getZookeeperUrl();
		String zooKeeper = "kalmar39.fzi.de:2181";
		String groupId = "groupId";
		String[] topic = {
				"eu.proasense.internal.sp.internal.incoming",
				"eu.proasense.internal.sp.internal.outgoing.10000",
				AkerVariables.DrillingRPM.topic(), 
				AkerVariables.DrillingTorque.topic(), 
//				AkerVariables.GearLubeOilTemperature.topic(), 
//				AkerVariables.HookLoad.topic(), 
//				AkerVariables.SwivelOilTemperature.topic(), 
				AkerVariables.Enriched.topic(),
//				AkerVariables.GearBoxPressure.topic(),
//				AkerVariables.RamPositionMeasuredValue.topic(),
//				AkerVariables.RamPositionSetPoint.topic(),
//				AkerVariables.RamVelocityMeasuredValue.topic(),
//				AkerVariables.RamVelocitySetPoint.topic()
				};
		int threads = 1;

		KafkaConsumerGroup example = new KafkaConsumerGroup(zooKeeper, groupId,
				topic);
		example.run(threads);
	
		try {
			ModelSubmitter.submitProducer(declarers, SourcesConfig.serverUrl, 8089);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		declare();
	}
}
