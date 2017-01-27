package de.fzi.cep.sepa.sources.samples.mnist;

import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;

public class MnistSourcesInit extends StandaloneModelSubmitter {

    public static void main(String[] args) {

        DeclarersSingleton.getInstance()
                .add(new MLDataProducer());
        DeclarersSingleton.getInstance().setPort(8089);

        new MnistSourcesInit().init();
    }

}