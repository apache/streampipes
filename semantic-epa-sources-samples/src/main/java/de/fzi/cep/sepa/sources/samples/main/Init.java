package de.fzi.cep.sepa.sources.samples.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.sources.samples.config.AkerVariables;
import de.fzi.cep.sepa.sources.samples.config.SourcesConfig;
import de.fzi.cep.sepa.sources.samples.ddm.DDMProducer;
import de.fzi.cep.sepa.sources.samples.drillbit.DrillBitProducer;
import de.fzi.cep.sepa.sources.samples.mobile.MobileStreamProducer;
import de.fzi.cep.sepa.sources.samples.random.RandomDataProducer;
import de.fzi.cep.sepa.sources.samples.taxi.NYCTaxiProducer;
import de.fzi.cep.sepa.sources.samples.twitter.TwitterStreamProducer;
import de.fzi.cep.sepa.sources.samples.util.KafkaConsumerGroup;

public class Init {

	public static void  main(String[] args) throws Exception
	{
		List<SemanticEventProducerDeclarer> declarers = new ArrayList<SemanticEventProducerDeclarer>();

		/*declarers.add(new TwitterStreamProducer());
		declarers.add(new DDMProducer());
		declarers.add(new DrillBitProducer());
		declarers.add(new MobileStreamProducer());
		declarers.add(new RandomDataProducer());*/
		declarers.add(new NYCTaxiProducer());
		
		
		String zooKeeper = "89.216.116.44:2181";
		String groupId = "groupId";
		String[] topic = {AkerVariables.DrillingRPM.topic(), AkerVariables.DrillingTorque.topic(), AkerVariables.GearLubeOilTemperature.topic(), AkerVariables.HookLoad.topic(), AkerVariables.SwivelOilTemperature.topic()};
		int threads = 1;

		/*KafkaConsumerGroup example = new KafkaConsumerGroup(zooKeeper, groupId,
				topic);*/
		//example.run(threads);
		
		ModelSubmitter.submitProducer(declarers, SourcesConfig.serverUrl, 8089);
		
	}
}
