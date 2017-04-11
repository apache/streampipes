package de.fzi.cep.sepa.sources.samples;

import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
import de.fzi.cep.sepa.sources.samples.biggis.BiggisDataProducer;
import de.fzi.cep.sepa.sources.samples.mnist.MnistDataProducer;
import de.fzi.cep.sepa.sources.samples.taxi.TaxiDataProducer;
import de.fzi.cep.sepa.sources.samples.taxiaggregated.AggregatedTaxiDataProducer;

public class MLSourcesInit extends StandaloneModelSubmitter {


    public static void main(String[] args) {

        DeclarersSingleton.getInstance()
                .add(new MnistDataProducer())
                .add(new BiggisDataProducer())
                .add(new AggregatedTaxiDataProducer())
                .add(new TaxiDataProducer());
        DeclarersSingleton.getInstance().setPort(8078);

        new MLSourcesInit().init();
    }

}