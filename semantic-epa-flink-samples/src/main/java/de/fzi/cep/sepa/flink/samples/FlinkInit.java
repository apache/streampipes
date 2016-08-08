package de.fzi.cep.sepa.flink.samples;


import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.osgi.init.OsgiSubmitter;
import de.fzi.cep.sepa.flink.samples.classification.number.NumberClassificationController;
import de.fzi.cep.sepa.flink.samples.elasticsearch.ElasticSearchController;
import de.fzi.cep.sepa.flink.samples.enrich.timestamp.TimestampController;
import de.fzi.cep.sepa.flink.samples.hasher.FieldHasherController;
import de.fzi.cep.sepa.flink.samples.rename.FieldRenamerController;
import de.fzi.cep.sepa.flink.samples.wordcount.WordCountController;

public class FlinkInit extends OsgiSubmitter {

	public static void main(String[] args) {
        DeclarersSingleton.getInstance()
                .add(new WordCountController())
				.add(new ElasticSearchController())
				.add(new NumberClassificationController())
				.add(new TimestampController())
				.add(new FieldHasherController())
				.add(new FieldRenamerController());

        DeclarersSingleton.getInstance().setPort(8094);
        new FlinkInit().init();

	}

	@Override
	public String getContextPath() {
		return "/flink";
	}

	@Override
	public void init() {
		DeclarersSingleton.getInstance().setPort(8080);
		DeclarersSingleton.getInstance().setRoute("flink");
		DeclarersSingleton.getInstance().add(new ElasticSearchController());
	}

}
