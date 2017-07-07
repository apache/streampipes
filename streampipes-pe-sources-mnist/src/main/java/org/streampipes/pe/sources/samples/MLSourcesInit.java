package org.streampipes.pe.sources.samples;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.pe.sources.samples.biggis.BiggisDataProducer;
import org.streampipes.pe.sources.samples.mnist.MnistDataProducer;
import org.streampipes.pe.sources.samples.taxi.TaxiDataProducer;
import org.streampipes.pe.sources.samples.taxiaggregated.AggregatedTaxiDataProducer;

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