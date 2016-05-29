package de.fzi.cep.sepa.flink.samples;


import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
import de.fzi.cep.sepa.flink.samples.classification.number.NumberClassificationController;
import de.fzi.cep.sepa.flink.samples.elasticsearch.ElasticSearchController;
import de.fzi.cep.sepa.flink.samples.enrich.timestamp.TimestampController;
import de.fzi.cep.sepa.flink.samples.hasher.FieldHasherController;
import de.fzi.cep.sepa.flink.samples.rename.FieldRenamerController;
import de.fzi.cep.sepa.flink.samples.wordcount.WordCountController;

public class Init extends StandaloneModelSubmitter {

	public static void main(String[] args) {
        DeclarersSingleton.getInstance()
                .add(new WordCountController())
				.add(new ElasticSearchController())
				.add(new NumberClassificationController())
				.add(new TimestampController())
				.add(new FieldHasherController())
				.add(new FieldRenamerController());

        new Init().init(8094);

	}

}
