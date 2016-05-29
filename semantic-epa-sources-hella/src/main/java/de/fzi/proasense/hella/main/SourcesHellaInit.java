package de.fzi.proasense.hella.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.proasense.hella.sources.EnrichedEventProducer;
import de.fzi.proasense.hella.sources.EnvironmentalDataProducer;
import de.fzi.proasense.hella.sources.HumanSensorDataProducer;
import de.fzi.proasense.hella.sources.MontracProducer;
import de.fzi.proasense.hella.sources.MouldingMachineProducer;
import de.fzi.proasense.hella.sources.VisualInspectionProducer;

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

    }

}
