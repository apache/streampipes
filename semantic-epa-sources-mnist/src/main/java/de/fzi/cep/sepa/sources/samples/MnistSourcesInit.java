package de.fzi.cep.sepa.sources.samples;

import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
import de.fzi.cep.sepa.sources.samples.mnist.MLDataProducer;

public class MnistSourcesInit extends StandaloneModelSubmitter {

    public static void main(String[] args) {

        DeclarersSingleton.getInstance()
                .add(new MLDataProducer());
        DeclarersSingleton.getInstance().setPort(8078);

        new MnistSourcesInit().init();
    }

}