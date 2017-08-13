package org.streampipes.pe.sources.samples.friction;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.graph.SepDescription;

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
        EventProducer gearboxProducer = new SpKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), FrictionVariable.Gearbox.topic());
        EventProducer swivelProducer = new SpKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), FrictionVariable.Swivel.topic());
        Thread thread = new Thread(new FrictionReplay(gearboxProducer, swivelProducer));
        thread.start();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
