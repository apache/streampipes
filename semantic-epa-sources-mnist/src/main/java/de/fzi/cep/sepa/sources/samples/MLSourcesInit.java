package de.fzi.cep.sepa.sources.samples;

import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
import de.fzi.cep.sepa.sources.samples.mnist.MnistDataProducer;
import de.fzi.cep.sepa.sources.samples.taxi.TaxiDataProducer;

public class MLSourcesInit extends StandaloneModelSubmitter {


    public static void main(String[] args) {

        DeclarersSingleton.getInstance()
                .add(new MnistDataProducer())
                .add(new TaxiDataProducer());
        DeclarersSingleton.getInstance().setPort(8078);

        new MLSourcesInit().init();
    }

}