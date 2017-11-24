package org.streampipes.pe.sources.hella.main;

import org.streampipes.container.embedded.init.ContainerModelSubmitter;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.pe.sources.hella.config.SourcesConfig;
import org.streampipes.pe.sources.hella.sources.EnrichedEventProducer;
import org.streampipes.pe.sources.hella.sources.EnvironmentalDataProducer;
import org.streampipes.pe.sources.hella.sources.HumanSensorDataProducer;
import org.streampipes.pe.sources.hella.sources.MontracProducer;
import org.streampipes.pe.sources.hella.sources.MouldingMachineProducer;
import org.streampipes.pe.sources.hella.sources.VisualInspectionProducer;

//public class SourcesHellaInit extends EmbeddedModelSubmitter {
    public class SourcesHellaInit extends ContainerModelSubmitter {

    public void init() {
        DeclarersSingleton.getInstance().setRoute("sources-hella");
        DeclarersSingleton.getInstance()
                .add(new MontracProducer())
                .add(new HumanSensorDataProducer())
                .add(new MouldingMachineProducer())
                .add(new VisualInspectionProducer())
                .add(new EnrichedEventProducer())
                .add(new EnvironmentalDataProducer());

        new SourcesHellaInit().init(SourcesConfig.INSTANCE);

    }

}
