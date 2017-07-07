package de.fzi.cep.sepa.sources.samples.friction;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.messaging.EventProducer;
import de.fzi.cep.sepa.messaging.kafka.StreamPipesKafkaProducer;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

/**
 * Created by riemer on 26.10.2016.
 */
public class FrictionCoefficientSwivel extends FrictionCoefficient implements EventStreamDeclarer {

    protected FrictionCoefficientSwivel() {
        super(FrictionVariable.Swivel);
    }

    @Override
    public EventStream declareModel(SepDescription sep) {
        return prepareStream(sep);
    }

    @Override
    public void executeStream() {
        EventProducer gearboxProducer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), FrictionVariable.Gearbox.topic());
        EventProducer swivelProducer = new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), FrictionVariable.Swivel.topic());
        Thread thread = new Thread(new FrictionReplay(gearboxProducer, swivelProducer));
        thread.start();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
