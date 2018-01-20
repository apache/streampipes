package org.streampipes.pe.sources.samples.friction;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.samples.config.SourcesConfig;

public class FrictionCoefficientSwivel extends FrictionCoefficient implements DataStreamDeclarer {

    protected FrictionCoefficientSwivel() {
        super(FrictionVariable.Swivel);
    }

    @Override
    public SpDataStream declareModel(DataSourceDescription sep) {
        return prepareStream(sep);
    }

    @Override
    public void executeStream() {
        EventProducer gearboxProducer = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(),
                FrictionVariable.Gearbox.topic());
        EventProducer swivelProducer = new SpKafkaProducer(SourcesConfig.INSTANCE.getKafkaUrl(),
                FrictionVariable.Swivel.topic());
        Thread thread = new Thread(new FrictionReplay(gearboxProducer, swivelProducer));
        thread.start();
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
