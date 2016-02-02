package de.fzi.cep.sepa.flink.samples;

import java.util.Arrays;

import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.flink.samples.classification.number.NumberClassificationController;
import de.fzi.cep.sepa.flink.samples.elasticsearch.ElasticSearchController;
import de.fzi.cep.sepa.flink.samples.enrich.timestamp.TimestampController;
import de.fzi.cep.sepa.flink.samples.hasher.FieldHasherController;
import de.fzi.cep.sepa.flink.samples.wordcount.WordCountController;

public class Init {

	public static void main(String[] args) {
		ModelSubmitter.submitMixed(Arrays.asList(new WordCountController(), 
				new ElasticSearchController(),
				new NumberClassificationController(), 
				new TimestampController(),
				new FieldHasherController()), 8094);

	}
}
